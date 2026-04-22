package com.example.rentflat.service;

import com.example.rentflat.dto.response.ChatMessageDTO;
import com.example.rentflat.entity.ChatMessage;
import com.example.rentflat.entity.Inquiry;
import com.example.rentflat.entity.User;
import com.example.rentflat.enums.InquiryStatus;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.ChatMessageRepository;
import com.example.rentflat.repository.InquiryRepository;
import com.example.rentflat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final InquiryRepository     inquiryRepository;
    private final UserRepository        userRepository;
    private final UserService           userService;

    @Transactional
    public List<ChatMessageDTO> getMessages(UUID inquiryId) {
        User currentUser = userService.getCurrentUser();
        Inquiry inquiry = findAndCheckChatAccess(inquiryId, currentUser);

        // Mark incoming messages as read
        chatMessageRepository.markAllReadForInquiry(inquiryId, currentUser.getId());

        List<ChatMessage> messages = chatMessageRepository.findByInquiryIdOrderBySentAtAsc(inquiryId);
        Map<UUID, String> nameCache = buildNameCache(messages);

        return messages.stream()
                .map(m -> ChatMessageDTO.from(m, nameCache.getOrDefault(m.getSenderId(), "Unknown")))
                .toList();
    }

    @Transactional
    public ChatMessageDTO sendMessage(UUID inquiryId, String content) {
        User sender = userService.getCurrentUser();
        Inquiry inquiry = findAndCheckChatAccess(inquiryId, sender);

        if (content == null || content.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Message content cannot be empty");
        }

        ChatMessage saved = chatMessageRepository.save(ChatMessage.builder()
                .inquiryId(inquiryId)
                .senderId(sender.getId())
                .content(content.trim())
                .build());

        return ChatMessageDTO.from(saved, sender.getFullName());
    }

    public long getUnreadCount(UUID inquiryId) {
        User user = userService.getCurrentUser();
        return chatMessageRepository.countByInquiryIdAndReadFalseAndSenderIdNot(inquiryId, user.getId());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Inquiry findAndCheckChatAccess(UUID inquiryId, User user) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        if (!inquiry.getTenantId().equals(user.getId()) && !inquiry.getOwnerId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access denied to this chat");
        }

        if (inquiry.getStatus() != InquiryStatus.ACCEPTED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Chat is only available for accepted inquiries");
        }

        return inquiry;
    }

    private Map<UUID, String> buildNameCache(List<ChatMessage> messages) {
        List<UUID> senderIds = messages.stream().map(ChatMessage::getSenderId).distinct().toList();
        return userRepository.findAllById(senderIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
