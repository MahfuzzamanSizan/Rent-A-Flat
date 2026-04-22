package com.example.rentflat.controller;

import com.example.rentflat.dto.response.ChatMessageDTO;
import com.example.rentflat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService             chatService;
    private final SimpMessagingTemplate   messagingTemplate;

    // ── REST (history + polling) ────────────────────────────────────────────────

    @GetMapping("/{inquiryId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable UUID inquiryId) {
        return ResponseEntity.ok(chatService.getMessages(inquiryId));
    }

    @PostMapping("/{inquiryId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @PathVariable UUID inquiryId,
            @RequestBody Map<String, String> body) {

        ChatMessageDTO msg = chatService.sendMessage(inquiryId, body.get("content"));
        // Push to WebSocket subscribers as well
        messagingTemplate.convertAndSend("/topic/chat/" + inquiryId, msg);
        return ResponseEntity.ok(msg);
    }

    @GetMapping("/{inquiryId}/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> unreadCount(@PathVariable UUID inquiryId) {
        return ResponseEntity.ok(Map.of("count", chatService.getUnreadCount(inquiryId)));
    }

    // ── WebSocket (real-time) ───────────────────────────────────────────────────

    @MessageMapping("/chat/{inquiryId}")
    public void handleWebSocketMessage(
            @DestinationVariable UUID inquiryId,
            @Payload Map<String, String> payload) {

        ChatMessageDTO msg = chatService.sendMessage(inquiryId, payload.get("content"));
        messagingTemplate.convertAndSend("/topic/chat/" + inquiryId, msg);
    }
}
