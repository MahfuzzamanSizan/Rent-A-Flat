package com.example.rentflat.controller;

import com.example.rentflat.dto.request.CreateLeaseRequestDTO;
import com.example.rentflat.dto.request.RecordRentPaymentRequestDTO;
import com.example.rentflat.dto.request.TerminateLeaseRequestDTO;
import com.example.rentflat.dto.response.LeaseDTO;
import com.example.rentflat.dto.response.RentPaymentDTO;
import com.example.rentflat.service.LeaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leases")
@RequiredArgsConstructor
public class LeaseController {

    private final LeaseService leaseService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<LeaseDTO> createLease(@Valid @RequestBody CreateLeaseRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaseService.createLease(req));
    }

    @GetMapping
    public ResponseEntity<List<LeaseDTO>> getMyLeases() {
        return ResponseEntity.ok(leaseService.getMyLeases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaseDTO> getLease(@PathVariable UUID id) {
        return ResponseEntity.ok(leaseService.getLease(id));
    }

    @PostMapping("/{id}/sign")
    public ResponseEntity<LeaseDTO> signLease(@PathVariable UUID id) {
        return ResponseEntity.ok(leaseService.signLease(id));
    }

    @PostMapping("/{id}/terminate")
    public ResponseEntity<LeaseDTO> terminateLease(
            @PathVariable UUID id,
            @Valid @RequestBody TerminateLeaseRequestDTO req) {
        return ResponseEntity.ok(leaseService.terminateLease(id, req));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RentPaymentDTO> recordPayment(
            @PathVariable UUID id,
            @Valid @RequestBody RecordRentPaymentRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaseService.recordPayment(id, req));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<RentPaymentDTO>> getPayments(@PathVariable UUID id) {
        return ResponseEntity.ok(leaseService.getPayments(id));
    }
}
