package com.example.rentflat.dto.response;

import com.example.rentflat.entity.Subscription;
import com.example.rentflat.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder
public class SubscriptionDTO {
    private UUID id;
    private UUID userId;
    private SubscriptionPlanDTO plan;
    private SubscriptionStatus status;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private boolean autoRenew;
    private String paymentReference;
    private int boostCreditsRemaining;
    private Integer contactsRemaining;
    private OffsetDateTime createdAt;

    public static SubscriptionDTO from(Subscription s) {
        return SubscriptionDTO.builder()
                .id(s.getId())
                .userId(s.getUserId())
                .plan(s.getPlan() != null ? SubscriptionPlanDTO.from(s.getPlan()) : null)
                .status(s.getStatus())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .autoRenew(s.isAutoRenew())
                .paymentReference(s.getPaymentReference())
                .boostCreditsRemaining(s.getBoostCreditsRemaining())
                .contactsRemaining(s.getContactsRemaining())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
