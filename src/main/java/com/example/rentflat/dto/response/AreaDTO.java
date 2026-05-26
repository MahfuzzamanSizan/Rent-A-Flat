package com.example.rentflat.dto.response;

import com.example.rentflat.entity.Area;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AreaDTO {
    private UUID id;
    private String city;
    private String district;
    private String areaName;
    private String subArea;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean active;
    private OffsetDateTime createdAt;

    public static AreaDTO from(Area a) {
        return AreaDTO.builder()
                .id(a.getId())
                .city(a.getCity())
                .district(a.getDistrict())
                .areaName(a.getAreaName())
                .subArea(a.getSubArea())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .active(a.isActive())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
