package com.example.rentflat.controller;

import com.example.rentflat.dto.response.AreaDTO;
import com.example.rentflat.service.AreaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    // ── Public ─────────────────────────────────────────────────────────────────

    @GetMapping("/api/v1/areas")
    public ResponseEntity<List<AreaDTO>> getActiveAreas() {
        return ResponseEntity.ok(areaService.getActiveAreas());
    }

    // ── Admin ──────────────────────────────────────────────────────────────────

    @GetMapping("/api/v1/admin/areas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AreaDTO>> getAllAreas() {
        return ResponseEntity.ok(areaService.getAllAreas());
    }

    @PostMapping("/api/v1/admin/areas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaDTO> createArea(@Valid @RequestBody AreaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(areaService.createArea(req.getCity(), req.getDistrict(), req.getAreaName(), req.getSubArea()));
    }

    @PutMapping("/api/v1/admin/areas/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaDTO> updateArea(@PathVariable UUID id, @Valid @RequestBody AreaRequest req) {
        return ResponseEntity.ok(areaService.updateArea(id, req.getCity(), req.getDistrict(), req.getAreaName(), req.getSubArea()));
    }

    @PatchMapping("/api/v1/admin/areas/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaDTO> toggleArea(@PathVariable UUID id) {
        return ResponseEntity.ok(areaService.toggleArea(id));
    }

    @Data
    public static class AreaRequest {
        @NotBlank private String city;
        @NotBlank private String district;
        @NotBlank private String areaName;
        private String subArea;
    }
}
