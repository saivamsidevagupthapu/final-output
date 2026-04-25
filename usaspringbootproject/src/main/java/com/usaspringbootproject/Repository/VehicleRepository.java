package com.usaspringbootproject.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usaspringbootproject.Entity.VehicleEntity;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {

    VehicleEntity findByVendorIdAndPlatenumber(Long vendorId, String platenumber);

    VehicleEntity findByPlatenumberAndVendorname(String platenumber, String vendorname);

    Optional<VehicleEntity> findFirstByVendorId(Long vendorId);

    void deleteByPlatenumberAndVendorname(String platenumber, String vendorname);

    List<VehicleEntity> findByVendorname(String vendorname);
}