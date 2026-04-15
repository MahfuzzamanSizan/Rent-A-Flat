package com.example.rentflat.repository;

import com.example.rentflat.entity.RentPayment;
import com.example.rentflat.enums.RentPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RentPaymentRepository extends JpaRepository<RentPayment, UUID> {
    List<RentPayment> findByLeaseIdOrderByDueDateAsc(UUID leaseId);
    long countByStatus(RentPaymentStatus status);
}
