package com.example.rentflat.service;

import com.example.rentflat.dto.response.NotificationDTO;
import com.example.rentflat.dto.response.PageResponseDTO;
import com.example.rentflat.entity.Notification;
import com.example.rentflat.entity.User;
import com.example.rentflat.enums.NotificationType;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public PageResponseDTO<NotificationDTO> getMyNotifications(UUID userId, Pageable pageable) {
        return PageResponseDTO.from(
                notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                        .map(NotificationDTO::from));
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAllRead(UUID userId) {
        notificationRepository.markAllReadByUserId(userId);
    }

    @Transactional
    public NotificationDTO markOneRead(UUID notificationId, UUID userId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (!n.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access denied");
        }
        n.setRead(true);
        return NotificationDTO.from(notificationRepository.save(n));
    }

    @Transactional
    public void send(UUID userId, String title, String body, NotificationType type, UUID referenceId) {
        Notification n = Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(type)
                .referenceId(referenceId)
                .build();
        notificationRepository.save(n);
    }

    @Transactional
    public void sendToUsers(List<User> users, String title, String body, NotificationType type) {
        List<Notification> notifications = users.stream()
                .map(u -> Notification.builder()
                        .userId(u.getId())
                        .title(title)
                        .body(body)
                        .type(type)
                        .build())
                .toList();
        notificationRepository.saveAll(notifications);
    }
}
