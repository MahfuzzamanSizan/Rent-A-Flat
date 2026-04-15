package com.example.rentflat.controller;

import com.example.rentflat.dto.response.SubscriptionDTO;
import com.example.rentflat.dto.response.SubscriptionPlanDTO;
import com.example.rentflat.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanDTO>> getPlans() {
        return ResponseEntity.ok(subscriptionService.getActivePlans());
    }

    @GetMapping("/plans/{planId}")
    public ResponseEntity<SubscriptionPlanDTO> getPlan(@PathVariable UUID planId) {
        return ResponseEntity.ok(subscriptionService.getPlan(planId));
    }

    @PostMapping("/subscribe/{planId}")
    public ResponseEntity<SubscriptionDTO> subscribe(@PathVariable UUID planId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.subscribe(planId));
    }

    @GetMapping("/my")
    public ResponseEntity<SubscriptionDTO> getMySubscription() {
        return ResponseEntity.ok(subscriptionService.getMySubscription());
    }
}
