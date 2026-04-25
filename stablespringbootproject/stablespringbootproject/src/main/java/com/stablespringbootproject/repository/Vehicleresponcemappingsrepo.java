package com.stablespringbootproject.repository;

import com.stablespringbootproject.Entity.Vehicleresponcemappings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Vehicleresponcemappingsrepo extends JpaRepository<Vehicleresponcemappings, Long> {

    // already existing
    List<Vehicleresponcemappings> findByVendorId(Long vendorId);

    // ADD this
    List<Vehicleresponcemappings> findByApiId(Long apiId);

    List<Vehicleresponcemappings> findByVendorIdAndApiId(Long vendorId, Long apiId);

    void deleteByVendorIdAndApiId(Long vendorId, Long apiId);
}