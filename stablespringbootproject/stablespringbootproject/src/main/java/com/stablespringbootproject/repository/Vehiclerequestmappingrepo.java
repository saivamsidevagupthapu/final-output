package com.stablespringbootproject.repository;

import com.stablespringbootproject.Entity.vehiclerequestmapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Vehiclerequestmappingrepo extends JpaRepository<vehiclerequestmapping, Long> {

    // already existing
    List<vehiclerequestmapping> findByVendorIdAndApiId(Long vendorId, Long apiId);

    // ADD this
    void deleteByVendorIdAndApiId(Long vendorId, Long apiId);
} 