package com.example.rentflat.dto.response;

import com.example.rentflat.entity.ChatMessage;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder
public class ChatMessageDTO {
    private UUID id;
    private UUID inquiryId;
    private UUID senderId;
    private String senderName;
    private String content;
    private boolean read;
    private OffsetDateTime sentAt;

    public static ChatMessageDTO from(ChatMessage m, String senderName) {
        return ChatMessageDTO.builder()
                .id(m.getId())
                .inquiryId(m.getInquiryId())
                .senderId(m.getSenderId())
                .senderName(senderName)
                .content(m.getContent())
                .read(m.isRead())
                .sentAt(m.getSentAt())
                .build();
    }
}
