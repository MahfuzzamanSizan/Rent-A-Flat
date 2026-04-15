package com.example.rentflat.controller;

import com.example.rentflat.dto.request.CreateInquiryRequestDTO;
import com.example.rentflat.dto.request.RespondInquiryRequestDTO;
import com.example.rentflat.dto.response.InquiryDTO;
import com.example.rentflat.dto.response.PageResponseDTO;
import com.example.rentflat.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<InquiryDTO> createInquiry(@Valid @RequestBody CreateInquiryRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiryService.createInquiry(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InquiryDTO> getInquiry(@PathVariable UUID id) {
        return ResponseEntity.ok(inquiryService.getInquiry(id));
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PageResponseDTO<InquiryDTO>> getOwnerInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(inquiryService.getMyInquiriesAsOwner(PageRequest.of(page, size)));
    }

    @GetMapping("/tenant")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<PageResponseDTO<InquiryDTO>> getTenantInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(inquiryService.getMyInquiriesAsTenant(PageRequest.of(page, size)));
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<InquiryDTO> accept(
            @PathVariable UUID id,
            @RequestBody RespondInquiryRequestDTO req) {
        return ResponseEntity.ok(inquiryService.acceptInquiry(id, req));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<InquiryDTO> reject(
            @PathVariable UUID id,
            @RequestBody RespondInquiryRequestDTO req) {
        return ResponseEntity.ok(inquiryService.rejectInquiry(id, req));
    }
}
