package com.usaspringbootproject.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usaspringbootproject.Entity.VehicleEntity;

@Repository
public interface VehicleDataRepository extends JpaRepository<VehicleEntity, Long> {

    VehicleEntity findByPlatenumberAndVendorname(String platenumber, String vendorname);
}