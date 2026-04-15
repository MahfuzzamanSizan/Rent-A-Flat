package com.example.rentflat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateInquiryRequestDTO {
    @NotNull(message = "Property ID is required")
    private UUID propertyId;
    private String message;
}
