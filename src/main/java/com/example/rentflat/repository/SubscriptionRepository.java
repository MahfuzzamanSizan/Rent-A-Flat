package com.example.rentflat.repository;

import com.example.rentflat.entity.Subscription;
import com.example.rentflat.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByUserIdAndStatus(UUID userId, SubscriptionStatus status);
    long countByStatus(SubscriptionStatus status);
}
