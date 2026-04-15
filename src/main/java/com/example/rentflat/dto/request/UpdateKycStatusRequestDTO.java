package com.example.rentflat.dto.request;

import com.example.rentflat.enums.KycStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateKycStatusRequestDTO {
    @NotNull(message = "KYC status is required")
    private KycStatus kycStatus;
    private String rejectionReason;
}
