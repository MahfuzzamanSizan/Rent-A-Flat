package com.example.rentflat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateUserStatusRequestDTO {
    @NotNull(message = "Active status is required")
    private Boolean active;
    private String reason;
}
