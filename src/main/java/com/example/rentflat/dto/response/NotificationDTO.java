package com.example.rentflat.dto.response;

import com.example.rentflat.entity.Notification;
import com.example.rentflat.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationDTO {
    private UUID id;
    private UUID userId;
    private String title;
    private String body;
    private NotificationType type;
    private boolean read;
    private UUID referenceId;
    private String deepLink;
    private OffsetDateTime createdAt;

    public static NotificationDTO from(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .title(n.getTitle())
                .body(n.getBody())
                .type(n.getType())
                .read(n.isRead())
                .referenceId(n.getReferenceId())
                .deepLink(n.getDeepLink())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
