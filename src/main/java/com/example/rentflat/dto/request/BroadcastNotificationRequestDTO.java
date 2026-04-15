package com.example.rentflat.dto.request;

import com.example.rentflat.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class BroadcastNotificationRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Body is required")
    private String body;
    // If null → all users. Otherwise filter by role or specific IDs.
    private UserRole targetRole;
    private List<UUID> targetUserIds;
}
