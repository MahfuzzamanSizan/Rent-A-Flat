package com.example.rentflat.repository;

import com.example.rentflat.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByTargetIdAndPublishedTrue(UUID targetId);
    List<Review> findByPropertyIdAndPublishedTrue(UUID propertyId);
    boolean existsByReviewerIdAndLeaseId(UUID reviewerId, UUID leaseId);
    Optional<Review> findByReviewerIdAndLeaseId(UUID reviewerId, UUID leaseId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.targetId = :targetId AND r.published = true")
    double getAverageRating(@Param("targetId") UUID targetId);
}
