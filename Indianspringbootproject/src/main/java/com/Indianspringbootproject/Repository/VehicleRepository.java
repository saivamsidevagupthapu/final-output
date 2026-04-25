package com.Indianspringbootproject.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Indianspringbootproject.Entity.Vehicleresponcemappings;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicleresponcemappings, Long> {

	List<Vehicleresponcemappings> findByVehicleNumberAndVendorname(String vehicleNumber, String vendorName);
	
}
