package com.example.rentflat.repository;

import com.example.rentflat.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AreaRepository extends JpaRepository<Area, UUID> {
    List<Area> findByActiveTrue();
    List<Area> findByCityAndActiveTrue(String city);
}
