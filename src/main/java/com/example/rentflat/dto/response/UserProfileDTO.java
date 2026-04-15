package com.example.rentflat.dto.response;

import com.example.rentflat.entity.User;
import com.example.rentflat.enums.KycStatus;
import com.example.rentflat.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder
public class UserProfileDTO {
    private UUID id;
    private String phone;
    private String email;
    private String fullName;
    private UserRole role;
    private KycStatus kycStatus;
    private String profilePhotoUrl;
    private String occupation;
    private String incomeRange;
    private boolean active;
    private BigDecimal averageRating;
    private int totalReviews;
    private OffsetDateTime createdAt;

    public static UserProfileDTO from(User u) {
        return UserProfileDTO.builder()
                .id(u.getId())
                .phone(u.getPhone())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .role(u.getRole())
                .kycStatus(u.getKycStatus())
                .profilePhotoUrl(u.getProfilePhotoUrl())
                .occupation(u.getOccupation())
                .incomeRange(u.getIncomeRange())
                .active(u.isActive())
                .averageRating(u.getAverageRating())
                .totalReviews(u.getTotalReviews())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
