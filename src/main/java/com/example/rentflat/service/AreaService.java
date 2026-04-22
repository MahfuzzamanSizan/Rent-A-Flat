package com.example.rentflat.service;

import com.example.rentflat.dto.response.AreaDTO;
import com.example.rentflat.entity.Area;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    public List<AreaDTO> getActiveAreas() {
        return new ArrayList<>(areaRepository.findByActiveTrue().stream().map(AreaDTO::from).toList());
    }

    public List<AreaDTO> getAllAreas() {
        return new ArrayList<>(areaRepository.findAll().stream().map(AreaDTO::from).toList());
    }

    @CacheEvict(value = "areas", allEntries = true)
    public AreaDTO createArea(String city, String district, String areaName, String subArea) {
        Area area = Area.builder()
                .city(city).district(district).areaName(areaName).subArea(subArea).active(true)
                .build();
        return AreaDTO.from(areaRepository.save(area));
    }

    @CacheEvict(value = "areas", allEntries = true)
    public AreaDTO updateArea(UUID id, String city, String district, String areaName, String subArea) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Area not found"));
        area.setCity(city);
        area.setDistrict(district);
        area.setAreaName(areaName);
        area.setSubArea(subArea);
        return AreaDTO.from(areaRepository.save(area));
    }

    @CacheEvict(value = "areas", allEntries = true)
    public AreaDTO toggleArea(UUID id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Area not found"));
        area.setActive(!area.isActive());
        return AreaDTO.from(areaRepository.save(area));
    }
}
