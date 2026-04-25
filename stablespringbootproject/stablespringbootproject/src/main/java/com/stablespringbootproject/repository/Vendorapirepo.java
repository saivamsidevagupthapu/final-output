package com.stablespringbootproject.repository;

import com.stablespringbootproject.Entity.Vendorapis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Vendorapirepo extends JpaRepository<Vendorapis, Long> {

    // already existing
    List<Vendorapis> findByVendorIdAndApiType(Long vendorId, String apiType);

    // ADD these two
    List<Vendorapis> findByVendorId(Long vendorId);
    List<Vendorapis> findByApiType(String apiType);
    void deleteAllByVendorId(Long vendorId);
}