package com.example.rentflat.controller;

import com.example.rentflat.dto.request.BroadcastNotificationRequestDTO;
import com.example.rentflat.dto.request.UpdateComplaintStatusRequestDTO;
import com.example.rentflat.dto.request.UpdateKycStatusRequestDTO;
import com.example.rentflat.dto.request.UpdatePropertyStatusRequestDTO;
import com.example.rentflat.dto.request.UpdateUserStatusRequestDTO;
import com.example.rentflat.dto.response.*;
import com.example.rentflat.enums.ComplaintStatus;
import com.example.rentflat.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ── Dashboard ──────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    // ── Users ──────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<PageResponseDTO<UserProfileDTO>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.listUsers(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<UserProfileDTO> updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserStatusRequestDTO req) {
        return ResponseEntity.ok(adminService.updateUserStatus(userId, req));
    }

    @PatchMapping("/users/{userId}/kyc")
    public ResponseEntity<UserProfileDTO> updateKycStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateKycStatusRequestDTO req) {
        return ResponseEntity.ok(adminService.updateKycStatus(userId, req));
    }

    // ── Properties ─────────────────────────────────────────────────────────────

    @GetMapping("/properties")
    public ResponseEntity<PageResponseDTO<PropertySummaryDTO>> listAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.listAllProperties(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/properties/pending")
    public ResponseEntity<PageResponseDTO<PropertySummaryDTO>> listPendingProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.listPendingProperties(PageRequest.of(page, size)));
    }

    @PatchMapping("/properties/{propertyId}/status")
    public ResponseEntity<PropertyDetailDTO> updatePropertyStatus(
            @PathVariable UUID propertyId,
            @Valid @RequestBody UpdatePropertyStatusRequestDTO req) {
        return ResponseEntity.ok(adminService.updatePropertyStatus(propertyId, req));
    }

    // ── Complaints ─────────────────────────────────────────────────────────────

    @GetMapping("/complaints")
    public ResponseEntity<PageResponseDTO<ComplaintDTO>> listComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.listComplaints(status, PageRequest.of(page, size)));
    }

    @PatchMapping("/complaints/{complaintId}/status")
    public ResponseEntity<ComplaintDTO> updateComplaintStatus(
            @PathVariable UUID complaintId,
            @Valid @RequestBody UpdateComplaintStatusRequestDTO req) {
        return ResponseEntity.ok(adminService.updateComplaintStatus(complaintId, req));
    }

    // ── Transactions ───────────────────────────────────────────────────────────

    @GetMapping("/transactions")
    public ResponseEntity<PageResponseDTO<TransactionDTO>> listTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.listTransactions(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    // ── Broadcast ──────────────────────────────────────────────────────────────

    @PostMapping("/notifications/broadcast")
    public ResponseEntity<Void> broadcast(@Valid @RequestBody BroadcastNotificationRequestDTO req) {
        adminService.broadcastNotification(req);
        return ResponseEntity.ok().build();
    }
}
