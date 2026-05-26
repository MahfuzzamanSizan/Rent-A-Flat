package com.example.rentflat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminDashboardDTO {
    private long totalUsers;
    private long totalOwners;
    private long totalTenants;
    private long totalProperties;
    private long pendingProperties;
    private long activeLeases;
    private long openComplaints;
    private long totalSubscriptions;
    private BigDecimal totalRevenue;
}
