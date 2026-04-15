package com.example.rentflat.entity;

import com.example.rentflat.enums.LeaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "leases")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Lease {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "rent_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentAmount;

    @Column(name = "security_deposit", precision = 10, scale = 2)
    private BigDecimal securityDeposit;

    @Column(name = "rent_due_day", nullable = false)
    @Builder.Default
    private short rentDueDay = 5;

    @Column(name = "notice_period_days", nullable = false)
    @Builder.Default
    private int noticePeriodDays = 30;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private LeaseStatus status = LeaseStatus.DRAFT;

    @Column(name = "owner_signed_at")
    private OffsetDateTime ownerSignedAt;

    @Column(name = "tenant_signed_at")
    private OffsetDateTime tenantSignedAt;

    @Column(name = "termination_reason", columnDefinition = "TEXT")
    private String terminationReason;

    @Column(name = "terminated_by_id")
    private UUID terminatedById;

    @Column(name = "terminated_at")
    private OffsetDateTime terminatedAt;

    @Column(name = "lease_document_url", columnDefinition = "TEXT")
    private String leaseDocumentUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = OffsetDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
