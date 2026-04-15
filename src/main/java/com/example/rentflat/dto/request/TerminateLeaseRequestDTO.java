package com.example.rentflat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TerminateLeaseRequestDTO {
    @NotBlank(message = "Termination reason is required")
    private String reason;
}
