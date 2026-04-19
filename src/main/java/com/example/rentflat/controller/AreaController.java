package com.example.rentflat.controller;

import com.example.rentflat.dto.response.AreaDTO;
import com.example.rentflat.entity.Area;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.AreaRepository;
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

    private final AreaRepository areaRepository;

    // ── Public ─────────────────────────────────────────────────────────────────

    @GetMapping("/api/v1/areas")
    public ResponseEntity<List<AreaDTO>> getActiveAreas() {
        return ResponseEntity.ok(
                areaRepository.findByActiveTrue().stream().map(AreaDTO::from).toList()
        );
    }

    // ── Admin ──────────────────────────────────────────────────────────────────

    @GetMapping("/api/v1/admin/areas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AreaDTO>> getAllAreas() {
        return ResponseEntity.ok(
                areaRepository.findAll().stream().map(AreaDTO::from).toList()
        );
    }

    @PostMapping("/api/v1/admin/areas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaDTO> createArea(@Valid @RequestBody AreaRequest req) {
        Area area = Area.builder()
                .city(req.getCity())
                .district(req.getDistrict())
                .areaName(req.getAreaName())
                .subArea(req.getSubArea())
                .active(true)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(AreaDTO.from(areaRepository.save(area)));
    }

    @PutMapping("/api/v1/admin/areas/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaDTO> updateArea(@PathVariable UUID id, @Valid @RequestBody AreaRequest req) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Area not found"));
        area.setCity(req.getCity());
        area.setDistrict(req.getDistrict());
        area.setAreaName(req.getAreaName());
        area.setSubArea(req.getSubArea());
        return ResponseEntity.ok(AreaDTO.from(areaRepository.save(area)));
    }

    @PatchMapping("/api/v1/admin/areas/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaDTO> toggleArea(@PathVariable UUID id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Area not found"));
        area.setActive(!area.isActive());
        return ResponseEntity.ok(AreaDTO.from(areaRepository.save(area)));
    }

    @Data
    public static class AreaRequest {
        @NotBlank private String city;
        @NotBlank private String district;
        @NotBlank private String areaName;
        private String subArea;
    }
}
