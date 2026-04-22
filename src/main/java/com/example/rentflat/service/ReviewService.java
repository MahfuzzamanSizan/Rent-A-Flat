package com.example.rentflat.service;

import com.example.rentflat.dto.request.CreateReviewRequestDTO;
import com.example.rentflat.dto.response.ReviewDTO;
import com.example.rentflat.entity.Lease;
import com.example.rentflat.entity.Review;
import com.example.rentflat.entity.User;
import com.example.rentflat.enums.LeaseStatus;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.LeaseRepository;
import com.example.rentflat.repository.ReviewRepository;
import com.example.rentflat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LeaseRepository   leaseRepository;
    private final UserRepository    userRepository;
    private final UserService       userService;

    @Transactional
    public ReviewDTO createReview(CreateReviewRequestDTO req) {
        User reviewer = userService.getCurrentUser();

        Lease lease = leaseRepository.findById(req.getLeaseId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Lease not found"));

        if (!lease.getTenantId().equals(reviewer.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only the tenant of this lease can submit a review");
        }

        if (lease.getStatus() != LeaseStatus.TERMINATED && lease.getStatus() != LeaseStatus.EXPIRED) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Reviews can only be submitted after a lease has ended (TERMINATED or EXPIRED)");
        }

        if (reviewRepository.existsByReviewerIdAndLeaseId(reviewer.getId(), req.getLeaseId())) {
            throw new ApiException(HttpStatus.CONFLICT, "You have already reviewed this lease");
        }

        Review review = Review.builder()
                .reviewerId(reviewer.getId())
                .targetId(lease.getOwnerId())
                .propertyId(lease.getPropertyId())
                .leaseId(req.getLeaseId())
                .rating((short) req.getRating().intValue())
                .comment(req.getComment())
                .build();

        Review saved = reviewRepository.save(review);

        // Recalculate owner's average rating
        double avg = reviewRepository.getAverageRating(lease.getOwnerId());
        userRepository.findById(lease.getOwnerId()).ifPresent(owner -> {
            owner.setAverageRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
            owner.setTotalReviews(owner.getTotalReviews() + 1);
            userRepository.save(owner);
        });

        return ReviewDTO.from(saved);
    }

    public List<ReviewDTO> getPropertyReviews(UUID propertyId) {
        return reviewRepository.findByPropertyIdAndPublishedTrue(propertyId)
                .stream().map(ReviewDTO::from).toList();
    }

    public List<ReviewDTO> getOwnerReviews(UUID ownerId) {
        return reviewRepository.findByTargetIdAndPublishedTrue(ownerId)
                .stream().map(ReviewDTO::from).toList();
    }
}
