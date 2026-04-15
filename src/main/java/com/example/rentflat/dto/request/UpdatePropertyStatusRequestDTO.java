package com.example.rentflat.dto.request;

import com.example.rentflat.enums.PropertyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdatePropertyStatusRequestDTO {
    @NotNull(message = "Status is required")
    private PropertyStatus status;
    private String rejectionReason;
}
