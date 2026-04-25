package com.usaspringbootproject.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usaspringbootproject.Entity.VehicleResponseMappings.Vehicleresponcemappings;

@Repository
public interface VehicleMappingRepository
        extends JpaRepository<Vehicleresponcemappings, Long> {

    List<Vehicleresponcemappings> findByApiId(Long apiId);
}	 