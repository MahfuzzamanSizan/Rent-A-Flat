package com.example.rentflat.controller;

import com.example.rentflat.dto.response.SubscriptionPlanDTO;
import com.example.rentflat.entity.SubscriptionPlan;
import com.example.rentflat.enums.UserRole;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.SubscriptionPlanRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class PlanController {

    private final SubscriptionPlanRepository planRepository;

    @GetMapping("/api/v1/admin/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllPlans() {
        return ResponseEntity.ok(
                planRepository.findAll().stream().map(SubscriptionPlanDTO::from).toList()
        );
    }

    @PostMapping("/api/v1/admin/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionPlanDTO> createPlan(@Valid @RequestBody PlanRequest req) {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(req.getName())
                .targetRole(req.getTargetRole())
                .price(req.getPrice())
                .durationDays(req.getDurationDays())
                .maxListings(req.getMaxListings())
                .maxPhotos(req.getMaxPhotos())
                .boostCredits(req.getBoostCredits())
                .maxContacts(req.getMaxContacts())
                .features(req.getFeatures())
                .active(true)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(SubscriptionPlanDTO.from(planRepository.save(plan)));
    }

    @PutMapping("/api/v1/admin/plans/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionPlanDTO> updatePlan(@PathVariable UUID id, @Valid @RequestBody PlanRequest req) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Plan not found"));
        plan.setName(req.getName());
        plan.setTargetRole(req.getTargetRole());
        plan.setPrice(req.getPrice());
        plan.setDurationDays(req.getDurationDays());
        plan.setMaxListings(req.getMaxListings());
        plan.setMaxPhotos(req.getMaxPhotos());
        plan.setBoostCredits(req.getBoostCredits());
        plan.setMaxContacts(req.getMaxContacts());
        plan.setFeatures(req.getFeatures());
        return ResponseEntity.ok(SubscriptionPlanDTO.from(planRepository.save(plan)));
    }

    @PatchMapping("/api/v1/admin/plans/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionPlanDTO> togglePlan(@PathVariable UUID id) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Plan not found"));
        plan.setActive(!plan.isActive());
        return ResponseEntity.ok(SubscriptionPlanDTO.from(planRepository.save(plan)));
    }

    @Data
    public static class PlanRequest {
        @NotBlank private String name;
        @NotNull  private UserRole targetRole;
        @NotNull  private BigDecimal price;
        @Min(1)   private int durationDays;
        @Min(0)   private int maxListings;
        @Min(0)   private int maxPhotos;
        @Min(0)   private int boostCredits;
        @Min(0)   private int maxContacts;
        private Map<String, Object> features;
    }
}
