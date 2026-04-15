package com.example.rentflat.controller;

import com.example.rentflat.dto.response.NotificationDTO;
import com.example.rentflat.dto.response.PageResponseDTO;
import com.example.rentflat.entity.User;
import com.example.rentflat.service.NotificationService;
import com.example.rentflat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<NotificationDTO>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(notificationService.getMyNotifications(user.getId(), PageRequest.of(page, size)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getId())));
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllRead() {
        User user = userService.getCurrentUser();
        notificationService.markAllRead(user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markOneRead(@PathVariable UUID id) {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(notificationService.markOneRead(id, user.getId()));
    }
}
