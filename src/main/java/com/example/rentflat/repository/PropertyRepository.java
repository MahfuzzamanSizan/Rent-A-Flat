package com.example.rentflat.repository;

import com.example.rentflat.entity.Property;
import com.example.rentflat.enums.PropertyStatus;
import com.example.rentflat.enums.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {

    Optional<Property> findByIdAndDeletedAtIsNull(UUID id);

    Page<Property> findByOwnerIdAndDeletedAtIsNull(UUID ownerId, Pageable pageable);

    @Query("""
        SELECT p FROM Property p
        WHERE p.deletedAt IS NULL
          AND p.status = 'APPROVED'
          AND (:#{#areaId} IS NULL OR p.areaId = :areaId)
          AND (:#{#type} IS NULL OR p.propertyType = :type)
          AND (:minRent IS NULL OR p.rentAmount >= :minRent)
          AND (:maxRent IS NULL OR p.rentAmount <= :maxRent)
          AND (:#{#bedrooms} IS NULL OR p.bedrooms = :bedrooms)
          AND (:keyword IS NULL OR LOWER(p.title) LIKE :keyword
               OR LOWER(p.fullAddress) LIKE :keyword
               OR LOWER(p.description) LIKE :keyword)
        ORDER BY p.boosted DESC, p.createdAt DESC
        """)
    Page<Property> search(
            @Param("areaId") UUID areaId,
            @Param("type") PropertyType type,
            @Param("minRent") BigDecimal minRent,
            @Param("maxRent") BigDecimal maxRent,
            @Param("bedrooms") Short bedrooms,
            @Param("keyword") String keyword,
            Pageable pageable);

    Page<Property> findByStatusAndDeletedAtIsNull(PropertyStatus status, Pageable pageable);

    long countByStatus(PropertyStatus status);
    long countByDeletedAtIsNull();

    @Modifying
    @Query("UPDATE Property p SET p.viewsCount = p.viewsCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Property p SET p.inquiriesCount = p.inquiriesCount + 1 WHERE p.id = :id")
    void incrementInquiryCount(@Param("id") UUID id);

    @Modifying
    @Query(value = "INSERT INTO tenant_shortlists (tenant_id, property_id, created_at) VALUES (:tenantId, :propertyId, NOW()) ON CONFLICT DO NOTHING", nativeQuery = true)
    void addShortlist(@Param("tenantId") UUID tenantId, @Param("propertyId") UUID propertyId);

    @Modifying
    @Query(value = "DELETE FROM tenant_shortlists WHERE tenant_id = :tenantId AND property_id = :propertyId", nativeQuery = true)
    void removeShortlist(@Param("tenantId") UUID tenantId, @Param("propertyId") UUID propertyId);

    @Query(value = "SELECT COUNT(*) > 0 FROM tenant_shortlists WHERE tenant_id = :tenantId AND property_id = :propertyId", nativeQuery = true)
    boolean isShortlisted(@Param("tenantId") UUID tenantId, @Param("propertyId") UUID propertyId);

    @Query(value = """
        SELECT p.* FROM properties p
        INNER JOIN tenant_shortlists ts ON ts.property_id = p.id
        WHERE ts.tenant_id = :tenantId AND p.deleted_at IS NULL
        ORDER BY ts.created_at DESC
        """, nativeQuery = true)
    List<Property> findShortlistedByTenant(@Param("tenantId") UUID tenantId);
}
