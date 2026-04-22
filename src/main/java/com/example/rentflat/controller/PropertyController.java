package com.example.rentflat.controller;

import com.example.rentflat.dto.request.CreatePropertyRequestDTO;
import com.example.rentflat.dto.response.PageResponseDTO;
import com.example.rentflat.dto.response.PropertyDetailDTO;
import com.example.rentflat.dto.response.PropertySummaryDTO;
import com.example.rentflat.enums.PropertyType;
import com.example.rentflat.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    // ── Public search ──────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<PageResponseDTO<PropertySummaryDTO>> search(
            @RequestParam(required = false) UUID areaId,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) Short bedrooms,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(propertyService.searchProperties(
                areaId, type, minRent, maxRent, bedrooms, keyword,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDetailDTO> getProperty(@PathVariable UUID id) {
        return ResponseEntity.ok(propertyService.getProperty(id));
    }

    // ── Owner CRUD ─────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PropertyDetailDTO> createProperty(@Valid @RequestBody CreatePropertyRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.createProperty(req));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PageResponseDTO<PropertySummaryDTO>> getMyProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(propertyService.getMyProperties(PageRequest.of(page, size)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PropertyDetailDTO> updateProperty(
            @PathVariable UUID id,
            @Valid @RequestBody CreatePropertyRequestDTO req) {
        return ResponseEntity.ok(propertyService.updateProperty(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    // ── Tenant shortlist ───────────────────────────────────────────────────────

    @PostMapping("/{id}/shortlist")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Void> shortlist(@PathVariable UUID id) {
        propertyService.shortlistProperty(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/shortlist")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Void> removeShortlist(@PathVariable UUID id) {
        propertyService.removeShortlist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shortlist")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<PropertySummaryDTO>> getShortlist() {
        return ResponseEntity.ok(propertyService.getShortlist());
    }
}
