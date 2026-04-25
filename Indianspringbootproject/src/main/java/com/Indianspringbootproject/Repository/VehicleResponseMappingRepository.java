package com.Indianspringbootproject.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Indianspringbootproject.Entity.VehicleResponseMapping;

@Repository
public interface VehicleResponseMappingRepository 
        extends JpaRepository<VehicleResponseMapping, Long> {

    List<VehicleResponseMapping> 
        findByVehicleNumberAndVendorname(String vehicleNumber, String vendorname);
}