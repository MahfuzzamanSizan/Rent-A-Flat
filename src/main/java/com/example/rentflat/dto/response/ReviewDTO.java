package com.example.rentflat.dto.response;

import com.example.rentflat.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewDTO {
    private UUID id;
    private UUID reviewerId;
    private UUID targetId;
    private UUID propertyId;
    private UUID leaseId;
    private short rating;
    private String comment;
    private boolean published;
    private OffsetDateTime createdAt;

    public static ReviewDTO from(Review r) {
        return ReviewDTO.builder()
                .id(r.getId())
                .reviewerId(r.getReviewerId())
                .targetId(r.getTargetId())
                .propertyId(r.getPropertyId())
                .leaseId(r.getLeaseId())
                .rating(r.getRating())
                .comment(r.getComment())
                .published(r.isPublished())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
