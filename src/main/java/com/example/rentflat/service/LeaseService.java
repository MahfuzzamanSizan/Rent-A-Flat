package com.example.rentflat.service;

import com.example.rentflat.dto.request.CreateLeaseRequestDTO;
import com.example.rentflat.dto.request.RecordRentPaymentRequestDTO;
import com.example.rentflat.dto.request.TerminateLeaseRequestDTO;
import com.example.rentflat.dto.response.LeaseDTO;
import com.example.rentflat.dto.response.RentPaymentDTO;
import com.example.rentflat.entity.Lease;
import com.example.rentflat.entity.RentPayment;
import com.example.rentflat.entity.User;
import com.example.rentflat.enums.LeaseStatus;
import com.example.rentflat.enums.RentPaymentStatus;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.LeaseRepository;
import com.example.rentflat.repository.RentPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final RentPaymentRepository rentPaymentRepository;
    private final UserService userService;

    @Transactional
    public LeaseDTO createLease(CreateLeaseRequestDTO req) {
        User owner = userService.getCurrentUser();
        Lease lease = Lease.builder()
                .propertyId(req.getPropertyId())
                .tenantId(req.getTenantId())
                .ownerId(owner.getId())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .rentAmount(req.getRentAmount())
                .securityDeposit(req.getSecurityDeposit())
                .rentDueDay(req.getRentDueDay())
                .noticePeriodDays(req.getNoticePeriodDays())
                .terms(req.getTerms())
                .status(LeaseStatus.DRAFT)
                .build();
        return LeaseDTO.from(leaseRepository.save(lease));
    }

    @Transactional
    public LeaseDTO signLease(UUID leaseId) {
        User user = userService.getCurrentUser();
        Lease lease = findAndCheckAccess(leaseId, user);

        OffsetDateTime now = OffsetDateTime.now();
        if (lease.getOwnerId().equals(user.getId()) && lease.getOwnerSignedAt() == null) {
            lease.setOwnerSignedAt(now);
        } else if (lease.getTenantId().equals(user.getId()) && lease.getTenantSignedAt() == null) {
            lease.setTenantSignedAt(now);
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Already signed");
        }

        if (lease.getOwnerSignedAt() != null && lease.getTenantSignedAt() != null) {
            lease.setStatus(LeaseStatus.ACTIVE);
        } else {
            lease.setStatus(LeaseStatus.PENDING_SIGNATURE);
        }
        return LeaseDTO.from(leaseRepository.save(lease));
    }

    @Transactional
    public LeaseDTO terminateLease(UUID leaseId, TerminateLeaseRequestDTO req) {
        User user = userService.getCurrentUser();
        Lease lease = findAndCheckAccess(leaseId, user);
        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only active leases can be terminated");
        }
        lease.setStatus(LeaseStatus.TERMINATED);
        lease.setTerminationReason(req.getReason());
        lease.setTerminatedById(user.getId());
        lease.setTerminatedAt(OffsetDateTime.now());
        return LeaseDTO.from(leaseRepository.save(lease));
    }

    public List<LeaseDTO> getMyLeases() {
        User user = userService.getCurrentUser();
        List<Lease> leases;
        switch (user.getRole()) {
            case OWNER -> leases = leaseRepository.findByOwnerIdAndDeletedAtIsNull(user.getId());
            case TENANT -> leases = leaseRepository.findByTenantIdAndDeletedAtIsNull(user.getId());
            default -> throw new ApiException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return leases.stream().map(LeaseDTO::from).toList();
    }

    public LeaseDTO getLease(UUID leaseId) {
        User user = userService.getCurrentUser();
        Lease lease = findAndCheckAccess(leaseId, user);
        return LeaseDTO.from(lease);
    }

    @Transactional
    public RentPaymentDTO recordPayment(UUID leaseId, RecordRentPaymentRequestDTO req) {
        User owner = userService.getCurrentUser();
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Lease not found"));
        if (!lease.getOwnerId().equals(owner.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your lease");
        }
        RentPayment payment = RentPayment.builder()
                .leaseId(leaseId)
                .amount(req.getAmount())
                .dueDate(req.getDueDate())
                .paymentMethod(req.getPaymentMethod())
                .transactionReference(req.getTransactionReference())
                .notes(req.getNotes())
                .paidAt(OffsetDateTime.now())
                .status(RentPaymentStatus.PAID)
                .build();
        return RentPaymentDTO.from(rentPaymentRepository.save(payment));
    }

    public List<RentPaymentDTO> getPayments(UUID leaseId) {
        User user = userService.getCurrentUser();
        Lease lease = findAndCheckAccess(leaseId, user);
        return rentPaymentRepository.findByLeaseIdOrderByDueDateAsc(lease.getId())
                .stream().map(RentPaymentDTO::from).toList();
    }

    private Lease findAndCheckAccess(UUID leaseId, User user) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Lease not found"));
        if (!lease.getOwnerId().equals(user.getId()) && !lease.getTenantId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return lease;
    }
}
