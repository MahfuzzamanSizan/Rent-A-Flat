package com.example.rentflat.dto.response;

import com.example.rentflat.entity.Area;
import com.example.rentflat.entity.Property;
import com.example.rentflat.enums.PropertyStatus;
import com.example.rentflat.enums.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PropertySummaryDTO {
    private UUID id;
    private UUID ownerId;
    private String title;
    private AreaDTO area;
    private String fullAddress;
    private BigDecimal rentAmount;
    private boolean negotiable;
    private PropertyType propertyType;
    private Short bedrooms;
    private Short bathrooms;
    private BigDecimal sizeSqft;
    private LocalDate availableFrom;
    private PropertyStatus status;
    private boolean boosted;
    private int viewsCount;
    private int shortlistsCount;
    private List<String> photoUrls;
    private OffsetDateTime createdAt;

    public static PropertySummaryDTO from(Property p, Area area) {
        return PropertySummaryDTO.builder()
                .id(p.getId())
                .ownerId(p.getOwnerId())
                .title(p.getTitle())
                .area(area != null ? AreaDTO.from(area) : null)
                .fullAddress(p.getFullAddress())
                .rentAmount(p.getRentAmount())
                .negotiable(p.isNegotiable())
                .propertyType(p.getPropertyType())
                .bedrooms(p.getBedrooms())
                .bathrooms(p.getBathrooms())
                .sizeSqft(p.getSizeSqft())
                .availableFrom(p.getAvailableFrom())
                .status(p.getStatus())
                .boosted(p.isBoosted())
                .viewsCount(p.getViewsCount())
                .shortlistsCount(p.getShortlistsCount())
                .photoUrls(p.getPhotoUrls() != null ? new ArrayList<>(p.getPhotoUrls()) : new ArrayList<>())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
