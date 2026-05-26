package com.example.rentflat.service;

import com.example.rentflat.dto.request.CreatePropertyRequestDTO;
import com.example.rentflat.dto.request.UpdatePropertyStatusRequestDTO;
import com.example.rentflat.dto.response.PageResponseDTO;
import com.example.rentflat.dto.response.PropertyDetailDTO;
import com.example.rentflat.dto.response.PropertySummaryDTO;
import com.example.rentflat.entity.Area;
import com.example.rentflat.entity.Property;
import com.example.rentflat.entity.User;
import com.example.rentflat.enums.PropertyStatus;
import com.example.rentflat.enums.PropertyType;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.AreaRepository;
import com.example.rentflat.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final AreaRepository areaRepository;
    private final UserService userService;

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Area findArea(UUID areaId) {
        return areaRepository.findById(areaId).orElse(null);
    }

    private Map<UUID, Area> batchAreas(List<Property> properties) {
        List<UUID> ids = properties.stream().map(Property::getAreaId).distinct().toList();
        return areaRepository.findAllById(ids).stream().collect(Collectors.toMap(Area::getId, a -> a));
    }

    private PageResponseDTO<PropertySummaryDTO> toSummaryPage(Page<Property> page) {
        Map<UUID, Area> areas = batchAreas(page.getContent());
        return PageResponseDTO.from(page.map(p -> PropertySummaryDTO.from(p, areas.get(p.getAreaId()))));
    }

    private List<PropertySummaryDTO> toSummaryList(List<Property> props) {
        Map<UUID, Area> areas = batchAreas(props);
        return props.stream().map(p -> PropertySummaryDTO.from(p, areas.get(p.getAreaId()))).toList();
    }

    // ── CRUD ───────────────────────────────────────────────────────────────────

    @Transactional
    public PropertyDetailDTO createProperty(CreatePropertyRequestDTO req) {
        User owner = userService.getCurrentUser();
        log.info("Creating property for owner={} areaId={} type={}", owner.getId(), req.getAreaId(), req.getPropertyType());
        Property property = Property.builder()
                .ownerId(owner.getId())
                .title(req.getTitle())
                .description(req.getDescription())
                .areaId(req.getAreaId())
                .fullAddress(req.getFullAddress())
                .rentAmount(req.getRentAmount())
                .negotiable(req.isNegotiable())
                .securityDeposit(req.getSecurityDeposit())
                .propertyType(req.getPropertyType())
                .bedrooms(req.getBedrooms())
                .bathrooms(req.getBathrooms())
                .floorNumber(req.getFloorNumber())
                .totalFloors(req.getTotalFloors())
                .sizeSqft(req.getSizeSqft())
                .availableFrom(req.getAvailableFrom())
                .amenities(req.getAmenities())
                .houseRules(req.getHouseRules())
                .preferredTenant(req.getPreferredTenant())
                .videoUrl(req.getVideoUrl())
                .photoUrls(req.getPhotoUrls() != null ? req.getPhotoUrls() : List.of())
                .build();
        Property saved = propertyRepository.save(property);
        log.info("Property created id={}", saved.getId());
        return PropertyDetailDTO.from(saved, findArea(saved.getAreaId()));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<PropertySummaryDTO> getMyProperties(Pageable pageable) {
        User owner = userService.getCurrentUser();
        return toSummaryPage(propertyRepository.findByOwnerIdAndDeletedAtIsNull(owner.getId(), pageable));
    }

    @Transactional
    public PropertyDetailDTO getProperty(UUID id) {
        Property property = propertyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Property not found"));
        propertyRepository.incrementViewCount(id);
        return PropertyDetailDTO.from(property, findArea(property.getAreaId()));
    }

    @Caching(evict = {
        @CacheEvict(value = "properties",      key = "#id"),
        @CacheEvict(value = "property-search", allEntries = true)
    })
    @Transactional
    public PropertyDetailDTO updateProperty(UUID id, CreatePropertyRequestDTO req) {
        User owner = userService.getCurrentUser();
        Property property = propertyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Property not found"));
        if (!property.getOwnerId().equals(owner.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your property");
        }
        log.info("Updating property id={} owner={}", id, owner.getId());
        property.setTitle(req.getTitle());
        property.setDescription(req.getDescription());
        property.setAreaId(req.getAreaId());
        property.setFullAddress(req.getFullAddress());
        property.setRentAmount(req.getRentAmount());
        property.setNegotiable(req.isNegotiable());
        property.setSecurityDeposit(req.getSecurityDeposit());
        property.setPropertyType(req.getPropertyType());
        property.setBedrooms(req.getBedrooms());
        property.setBathrooms(req.getBathrooms());
        property.setFloorNumber(req.getFloorNumber());
        property.setTotalFloors(req.getTotalFloors());
        property.setSizeSqft(req.getSizeSqft());
        property.setAvailableFrom(req.getAvailableFrom());
        property.setAmenities(req.getAmenities());
        property.setHouseRules(req.getHouseRules());
        property.setPreferredTenant(req.getPreferredTenant());
        property.setVideoUrl(req.getVideoUrl());
        if (req.getPhotoUrls() != null) property.setPhotoUrls(req.getPhotoUrls());
        Property saved = propertyRepository.save(property);
        return PropertyDetailDTO.from(saved, findArea(saved.getAreaId()));
    }

    @Caching(evict = {
        @CacheEvict(value = "properties",      key = "#id"),
        @CacheEvict(value = "property-search", allEntries = true)
    })
    @Transactional
    public void deleteProperty(UUID id) {
        User owner = userService.getCurrentUser();
        Property property = propertyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Property not found"));
        if (!property.getOwnerId().equals(owner.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your property");
        }
        property.setDeletedAt(OffsetDateTime.now());
        propertyRepository.save(property);
        log.info("Property soft-deleted id={}", id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "property-search",
               key = "'' + #areaId + '_' + #type + '_' + #minRent + '_' + #maxRent + '_' + #bedrooms + '_' + #keyword + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public PageResponseDTO<PropertySummaryDTO> searchProperties(
            UUID areaId, PropertyType type,
            BigDecimal minRent, BigDecimal maxRent,
            Short bedrooms, String keyword, Pageable pageable) {
        String likeKeyword = (keyword != null && !keyword.isBlank()) ? "%" + keyword.trim().toLowerCase() + "%" : null;
        return toSummaryPage(
                propertyRepository.search(areaId, type, minRent, maxRent, bedrooms, likeKeyword, pageable));
    }

    @Transactional
    public void shortlistProperty(UUID propertyId) {
        User tenant = userService.getCurrentUser();
        propertyRepository.findByIdAndDeletedAtIsNull(propertyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Property not found"));
        propertyRepository.addShortlist(tenant.getId(), propertyId);
    }

    @Transactional
    public void removeShortlist(UUID propertyId) {
        User tenant = userService.getCurrentUser();
        propertyRepository.removeShortlist(tenant.getId(), propertyId);
    }

    @Transactional(readOnly = true)
    public List<PropertySummaryDTO> getShortlist() {
        User tenant = userService.getCurrentUser();
        return toSummaryList(propertyRepository.findShortlistedByTenant(tenant.getId()));
    }

    // Admin
    @Transactional(readOnly = true)
    public PageResponseDTO<PropertySummaryDTO> listByStatus(PropertyStatus status, Pageable pageable) {
        Page<Property> page = (status != null)
                ? propertyRepository.findByStatusAndDeletedAtIsNull(status, pageable)
                : propertyRepository.findByDeletedAtIsNull(pageable);
        return toSummaryPage(page);
    }

    @Transactional
    public PropertyDetailDTO updatePropertyStatus(UUID id, UpdatePropertyStatusRequestDTO req) {
        Property property = propertyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Property not found"));
        property.setStatus(req.getStatus());
        if (req.getRejectionReason() != null) property.setRejectionReason(req.getRejectionReason());
        if (req.getStatus() == PropertyStatus.APPROVED) property.setApprovedAt(OffsetDateTime.now());
        Property saved = propertyRepository.save(property);
        log.info("Property {} status updated to {}", id, req.getStatus());
        return PropertyDetailDTO.from(saved, findArea(saved.getAreaId()));
    }
}
