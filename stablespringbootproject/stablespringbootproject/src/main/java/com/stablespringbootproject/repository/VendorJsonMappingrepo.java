package com.stablespringbootproject.repository;

import com.stablespringbootproject.Entity.Vehicleresponcemapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorJsonMappingrepo extends JpaRepository<Vehicleresponcemapping, Long> {

    // already existing
    List<Vehicleresponcemapping> findByVendorId(Long vendorId);

    // ADD this
    List<Vehicleresponcemapping> findByApiId(Long apiId);

    List<Vehicleresponcemapping> findByVendorIdAndApiId(Long vendorId, Long apiId);

    void deleteByVendorIdAndApiId(Long vendorId, Long apiId);
}