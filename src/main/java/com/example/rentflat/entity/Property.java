package com.example.rentflat.entity;

import com.example.rentflat.enums.PreferredTenant;
import com.example.rentflat.enums.PropertyStatus;
import com.example.rentflat.enums.PropertyType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "properties")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "area_id", nullable = false)
    private UUID areaId;

    @Column(name = "full_address", columnDefinition = "TEXT")
    private String fullAddress;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "rent_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentAmount;

    @Column(name = "is_negotiable", nullable = false)
    @Builder.Default
    private boolean negotiable = false;

    @Column(name = "security_deposit", precision = 10, scale = 2)
    private BigDecimal securityDeposit;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false, length = 20)
    private PropertyType propertyType;

    private Short bedrooms;
    private Short bathrooms;

    @Column(name = "floor_number")
    private Short floorNumber;

    @Column(name = "total_floors")
    private Short totalFloors;

    @Column(name = "size_sqft", precision = 8, scale = 2)
    private BigDecimal sizeSqft;

    @Column(name = "available_from")
    private LocalDate availableFrom;

    // Stored as JSON string: {"ac":true,"gas":true,"lift":false,...}
    @Column(columnDefinition = "jsonb")
    private String amenities;

    @Column(name = "house_rules", columnDefinition = "TEXT")
    private String houseRules;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_tenant", length = 20)
    private PreferredTenant preferredTenant;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PropertyStatus status = PropertyStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Column(name = "is_boosted", nullable = false)
    @Builder.Default
    private boolean boosted = false;

    @Column(name = "boost_until")
    private OffsetDateTime boostUntil;

    @Column(name = "views_count", nullable = false)
    @Builder.Default
    private int viewsCount = 0;

    @Column(name = "inquiries_count", nullable = false)
    @Builder.Default
    private int inquiriesCount = 0;

    @Column(name = "shortlists_count", nullable = false)
    @Builder.Default
    private int shortlistsCount = 0;

    @ElementCollection
    @CollectionTable(name = "property_photos", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "photo_url")
    @OrderColumn(name = "sort_order")
    @Builder.Default
    private List<String> photoUrls = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @PrePersist
    protected void onCreate() { createdAt = OffsetDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
