package com.example.rentflat.dto.response;

import com.example.rentflat.entity.Area;
import com.example.rentflat.entity.Property;
import com.example.rentflat.enums.PreferredTenant;
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
public class PropertyDetailDTO {
    private UUID id;
    private UUID ownerId;
    private String title;
    private String description;
    private AreaDTO area;
    private String fullAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal rentAmount;
    private boolean negotiable;
    private BigDecimal securityDeposit;
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
    private PropertyStatus status;
    private String rejectionReason;
    private OffsetDateTime approvedAt;
    private boolean boosted;
    private OffsetDateTime boostUntil;
    private int viewsCount;
    private int inquiriesCount;
    private int shortlistsCount;
    private List<String> photoUrls;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static PropertyDetailDTO from(Property p, Area area) {
        return PropertyDetailDTO.builder()
                .id(p.getId())
                .ownerId(p.getOwnerId())
                .title(p.getTitle())
                .description(p.getDescription())
                .area(area != null ? AreaDTO.from(area) : null)
                .fullAddress(p.getFullAddress())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .rentAmount(p.getRentAmount())
                .negotiable(p.isNegotiable())
                .securityDeposit(p.getSecurityDeposit())
                .propertyType(p.getPropertyType())
                .bedrooms(p.getBedrooms())
                .bathrooms(p.getBathrooms())
                .floorNumber(p.getFloorNumber())
                .totalFloors(p.getTotalFloors())
                .sizeSqft(p.getSizeSqft())
                .availableFrom(p.getAvailableFrom())
                .amenities(p.getAmenities() != null ? new ArrayList<>(p.getAmenities()) : new ArrayList<>())
                .houseRules(p.getHouseRules())
                .preferredTenant(p.getPreferredTenant())
                .videoUrl(p.getVideoUrl())
                .status(p.getStatus())
                .rejectionReason(p.getRejectionReason())
                .approvedAt(p.getApprovedAt())
                .boosted(p.isBoosted())
                .boostUntil(p.getBoostUntil())
                .viewsCount(p.getViewsCount())
                .inquiriesCount(p.getInquiriesCount())
                .shortlistsCount(p.getShortlistsCount())
                .photoUrls(p.getPhotoUrls() != null ? new ArrayList<>(p.getPhotoUrls()) : new ArrayList<>())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
