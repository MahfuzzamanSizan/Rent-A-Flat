package com.example.rentflat.dto.response;

import com.example.rentflat.entity.Lease;
import com.example.rentflat.enums.LeaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LeaseDTO {
    private UUID id;
    private UUID propertyId;
    private UUID tenantId;
    private UUID ownerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal rentAmount;
    private BigDecimal securityDeposit;
    private short rentDueDay;
    private int noticePeriodDays;
    private String terms;
    private LeaseStatus status;
    private OffsetDateTime ownerSignedAt;
    private OffsetDateTime tenantSignedAt;
    private String terminationReason;
    private UUID terminatedById;
    private OffsetDateTime terminatedAt;
    private String leaseDocumentUrl;
    private OffsetDateTime createdAt;

    public static LeaseDTO from(Lease l) {
        return LeaseDTO.builder()
                .id(l.getId())
                .propertyId(l.getPropertyId())
                .tenantId(l.getTenantId())
                .ownerId(l.getOwnerId())
                .startDate(l.getStartDate())
                .endDate(l.getEndDate())
                .rentAmount(l.getRentAmount())
                .securityDeposit(l.getSecurityDeposit())
                .rentDueDay(l.getRentDueDay())
                .noticePeriodDays(l.getNoticePeriodDays())
                .terms(l.getTerms())
                .status(l.getStatus())
                .ownerSignedAt(l.getOwnerSignedAt())
                .tenantSignedAt(l.getTenantSignedAt())
                .terminationReason(l.getTerminationReason())
                .terminatedById(l.getTerminatedById())
                .terminatedAt(l.getTerminatedAt())
                .leaseDocumentUrl(l.getLeaseDocumentUrl())
                .createdAt(l.getCreatedAt())
                .build();
    }
}
