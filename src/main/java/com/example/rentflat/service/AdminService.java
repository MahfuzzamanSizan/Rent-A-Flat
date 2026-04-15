package com.example.rentflat.service;

import com.example.rentflat.dto.request.BroadcastNotificationRequestDTO;
import com.example.rentflat.dto.request.UpdateComplaintStatusRequestDTO;
import com.example.rentflat.dto.response.*;
import com.example.rentflat.entity.Complaint;
import com.example.rentflat.enums.ComplaintStatus;
import com.example.rentflat.enums.NotificationType;
import com.example.rentflat.enums.PropertyStatus;
import com.example.rentflat.enums.SubscriptionStatus;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final LeaseRepository leaseRepository;
    private final ComplaintRepository complaintRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final PropertyService propertyService;

    public AdminDashboardDTO getDashboard() {
        return AdminDashboardDTO.builder()
                .totalUsers(userRepository.count())
                .totalProperties(propertyRepository.countByDeletedAtIsNull())
                .pendingProperties(propertyRepository.countByStatus(PropertyStatus.PENDING))
                .openComplaints(complaintRepository.countByStatus(ComplaintStatus.OPEN))
                .totalSubscriptions(subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE))
                .totalRevenue(transactionRepository.sumSuccessfulTransactions())
                .build();
    }

    public PageResponseDTO<UserProfileDTO> listUsers(Pageable pageable) {
        return userService.listUsers(pageable);
    }

    @Transactional
    public UserProfileDTO updateUserStatus(UUID userId, com.example.rentflat.dto.request.UpdateUserStatusRequestDTO req) {
        return userService.updateUserStatus(userId, req);
    }

    @Transactional
    public UserProfileDTO updateKycStatus(UUID userId, com.example.rentflat.dto.request.UpdateKycStatusRequestDTO req) {
        return userService.updateKycStatus(userId, req);
    }

    public PageResponseDTO<PropertySummaryDTO> listPendingProperties(Pageable pageable) {
        return propertyService.listByStatus(PropertyStatus.PENDING, pageable);
    }

    public PageResponseDTO<PropertySummaryDTO> listAllProperties(Pageable pageable) {
        return PageResponseDTO.from(
                propertyRepository.findAll(pageable).map(PropertySummaryDTO::from));
    }

    @Transactional
    public PropertyDetailDTO updatePropertyStatus(UUID propertyId,
            com.example.rentflat.dto.request.UpdatePropertyStatusRequestDTO req) {
        return propertyService.updatePropertyStatus(propertyId, req);
    }

    public PageResponseDTO<ComplaintDTO> listComplaints(ComplaintStatus status, Pageable pageable) {
        if (status != null) {
            return PageResponseDTO.from(
                    complaintRepository.findByStatus(status, pageable).map(ComplaintDTO::from));
        }
        return PageResponseDTO.from(
                complaintRepository.findAllByOrderByCreatedAtDesc(pageable).map(ComplaintDTO::from));
    }

    @Transactional
    public ComplaintDTO updateComplaintStatus(UUID complaintId, UpdateComplaintStatusRequestDTO req) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Complaint not found"));
        complaint.setStatus(req.getStatus());
        if (req.getAdminNotes() != null) complaint.setAdminNotes(req.getAdminNotes());
        if (req.getStatus() == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(java.time.OffsetDateTime.now());
        }
        return ComplaintDTO.from(complaintRepository.save(complaint));
    }

    public PageResponseDTO<TransactionDTO> listTransactions(Pageable pageable) {
        return PageResponseDTO.from(
                transactionRepository.findAllByOrderByCreatedAtDesc(pageable).map(TransactionDTO::from));
    }

    @Transactional
    public void broadcastNotification(BroadcastNotificationRequestDTO req) {
        if (req.getTargetUserIds() != null && !req.getTargetUserIds().isEmpty()) {
            req.getTargetUserIds().forEach(userId ->
                    notificationService.send(userId, req.getTitle(), req.getBody(),
                            NotificationType.SYSTEM, null));
        } else if (req.getTargetRole() != null) {
            userRepository.findAll().stream()
                    .filter(u -> u.getRole() == req.getTargetRole())
                    .forEach(u -> notificationService.send(u.getId(), req.getTitle(), req.getBody(),
                            NotificationType.SYSTEM, null));
        } else {
            notificationService.sendToUsers(
                    userRepository.findAll(), req.getTitle(), req.getBody(),
                    NotificationType.SYSTEM);
        }
    }
}
