package com.example.rentflat.dto.request;

import com.example.rentflat.enums.PreferredTenant;
import com.example.rentflat.enums.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreatePropertyRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Area is required")
    private UUID areaId;

    private String fullAddress;

    @NotNull(message = "Rent amount is required")
    @Positive(message = "Rent amount must be positive")
    private BigDecimal rentAmount;

    private boolean negotiable;
    private BigDecimal securityDeposit;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    private Short bedrooms;
    private Short bathrooms;
    private Short floorNumber;
    private Short totalFloors;
    private BigDecimal sizeSqft;
    private LocalDate availableFrom;

    private List<String> amenities;
    private String houseRules;
    private PreferredTenant preferredTenant;
    private String videoUrl;
    private List<String> photoUrls;
}
