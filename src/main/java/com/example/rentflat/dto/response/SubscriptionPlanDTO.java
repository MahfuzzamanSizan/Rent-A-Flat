package com.example.rentflat.dto.response;

import com.example.rentflat.entity.SubscriptionPlan;
import com.example.rentflat.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SubscriptionPlanDTO {
    private UUID id;
    private String name;
    private UserRole targetRole;
    private BigDecimal price;
    private int durationDays;
    private int maxListings;
    private int maxPhotos;
    private int boostCredits;
    private int maxContacts;
    private Map<String, Object> features;
    private boolean active;
    private OffsetDateTime createdAt;

    public static SubscriptionPlanDTO from(SubscriptionPlan p) {
        return SubscriptionPlanDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .targetRole(p.getTargetRole())
                .price(p.getPrice())
                .durationDays(p.getDurationDays())
                .maxListings(p.getMaxListings())
                .maxPhotos(p.getMaxPhotos())
                .boostCredits(p.getBoostCredits())
                .maxContacts(p.getMaxContacts())
                .features(p.getFeatures())
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
