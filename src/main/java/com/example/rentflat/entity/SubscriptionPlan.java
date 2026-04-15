package com.example.rentflat.entity;

import com.example.rentflat.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_plans")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_role", nullable = false, length = 20)
    private UserRole targetRole;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "max_listings", nullable = false)
    @Builder.Default
    private int maxListings = 1;

    @Column(name = "max_photos", nullable = false)
    @Builder.Default
    private int maxPhotos = 3;

    @Column(name = "boost_credits", nullable = false)
    @Builder.Default
    private int boostCredits = 0;

    @Column(name = "max_contacts", nullable = false)
    @Builder.Default
    private int maxContacts = 3;

    // Stored as JSON string
    @Column(columnDefinition = "jsonb")
    private String features;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = OffsetDateTime.now(); }
}
