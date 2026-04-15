package com.example.rentflat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateAreaRequestDTO {
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "District is required")
    private String district;
    @NotBlank(message = "Area name is required")
    private String areaName;
    private String subArea;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
