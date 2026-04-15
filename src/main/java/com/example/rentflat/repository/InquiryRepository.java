package com.example.rentflat.repository;

import com.example.rentflat.entity.Inquiry;
import com.example.rentflat.enums.InquiryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InquiryRepository extends JpaRepository<Inquiry, UUID> {
    Page<Inquiry> findByOwnerIdAndDeletedAtIsNull(UUID ownerId, Pageable pageable);
    Page<Inquiry> findByTenantIdAndDeletedAtIsNull(UUID tenantId, Pageable pageable);
    Optional<Inquiry> findByTenantIdAndPropertyId(UUID tenantId, UUID propertyId);
    long countByStatus(InquiryStatus status);
}
