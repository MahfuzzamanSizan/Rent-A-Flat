package com.example.rentflat.service;

import com.example.rentflat.dto.response.SubscriptionDTO;
import com.example.rentflat.dto.response.SubscriptionPlanDTO;
import com.example.rentflat.entity.Subscription;
import com.example.rentflat.entity.SubscriptionPlan;
import com.example.rentflat.entity.User;
import com.example.rentflat.enums.SubscriptionStatus;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.SubscriptionPlanRepository;
import com.example.rentflat.repository.SubscriptionRepository;
import com.example.rentflat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<SubscriptionPlanDTO> getActivePlans() {
        return planRepository.findByActiveTrue().stream()
                .map(SubscriptionPlanDTO::from).toList();
    }

    public SubscriptionPlanDTO getPlan(UUID planId) {
        return planRepository.findById(planId)
                .map(SubscriptionPlanDTO::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Plan not found"));
    }

    @Transactional
    public SubscriptionDTO subscribe(UUID planId) {
        User user = userService.getCurrentUser();
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Plan not found"));

        if (!plan.isActive()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Plan is not active");
        }

        OffsetDateTime now = OffsetDateTime.now();
        Subscription subscription = Subscription.builder()
                .userId(user.getId())
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(now)
                .endDate(now.plusDays(plan.getDurationDays()))
                .boostCreditsRemaining(plan.getBoostCredits())
                .contactsRemaining(plan.getMaxContacts())
                .build();

        Subscription saved = subscriptionRepository.save(subscription);

        // Link subscription to user
        user.setActiveSubscriptionId(saved.getId());
        userRepository.save(user);

        return SubscriptionDTO.from(saved);
    }

    public SubscriptionDTO getMySubscription() {
        User user = userService.getCurrentUser();
        if (user.getActiveSubscriptionId() == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No active subscription");
        }
        return subscriptionRepository.findById(user.getActiveSubscriptionId())
                .map(SubscriptionDTO::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Subscription not found"));
    }
}
