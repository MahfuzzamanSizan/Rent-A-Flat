package com.example.rentflat.repository;

import com.example.rentflat.entity.SubscriptionPlan;
import com.example.rentflat.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    List<SubscriptionPlan> findByTargetRoleAndActiveTrue(UserRole targetRole);
    List<SubscriptionPlan> findByActiveTrue();
}
