package com.example.rentflat.repository;

import com.example.rentflat.entity.Lease;
import com.example.rentflat.enums.LeaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaseRepository extends JpaRepository<Lease, UUID> {
    List<Lease> findByOwnerIdAndDeletedAtIsNull(UUID ownerId);
    List<Lease> findByTenantIdAndDeletedAtIsNull(UUID tenantId);
    Optional<Lease> findByPropertyIdAndStatus(UUID propertyId, LeaseStatus status);
    Page<Lease> findByDeletedAtIsNull(Pageable pageable);
}
