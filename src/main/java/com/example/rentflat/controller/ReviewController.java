package com.example.rentflat.controller;

import com.example.rentflat.dto.request.CreateReviewRequestDTO;
import com.example.rentflat.dto.response.ReviewDTO;
import com.example.rentflat.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ReviewDTO> create(@Valid @RequestBody CreateReviewRequestDTO req) {
        return ResponseEntity.ok(reviewService.createReview(req));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ReviewDTO>> byProperty(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(reviewService.getPropertyReviews(propertyId));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ReviewDTO>> byOwner(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(reviewService.getOwnerReviews(ownerId));
    }
}
