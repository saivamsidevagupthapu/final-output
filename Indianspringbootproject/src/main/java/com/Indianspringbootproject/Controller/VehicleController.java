package com.Indianspringbootproject.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Indianspringbootproject.Entity.VehicleInfo;
import com.Indianspringbootproject.service.VehicleService;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/vehicle")
public class VehicleController {

	@Autowired
	private VehicleService service;	

	// =========================================
	// CREATE (Dynamic)
	// =========================================
	@PostMapping("/{vehicleNumber}/{vendorName}")  // ✅ added leading slash
	public ResponseEntity<?> createVehicle(
	        @RequestBody JsonNode payload,
	        @PathVariable String vehicleNumber,
	        @PathVariable String vendorName) {

	    try {
	        Object result = service.saveDynamicVehicle(payload, vehicleNumber, vendorName);
	        return ResponseEntity.ok(result);
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(404).body(Map.of("message", e.getMessage())); // ✅ proper 404
	    }
	}

	// =========================================
	// READ (Standard - Recommended)
	// =========================================
	@GetMapping("/{vehicleNumber}/{vendorName}")
	public ResponseEntity<VehicleInfo> getVehicle(
	        @PathVariable("vehicleNumber") String vehicleNumber,
	        @PathVariable("vendorName") String vendorName) {
		VehicleInfo vehicle = service.getVehicle(vehicleNumber, vendorName);
		return vehicle != null ? ResponseEntity.ok(vehicle) : ResponseEntity.notFound().build();
	}

	// =========================================
	// UPDATE
	// =========================================
	@PutMapping("/{vehicleNumber}/{vendorName}")
	public ResponseEntity<VehicleInfo> updateVehicle(
	        @PathVariable String vehicleNumber,
	        @PathVariable String vendorName,
	        @RequestBody VehicleInfo vehicleData) {

	    VehicleInfo updated = service.updateVehicle(vehicleNumber, vendorName, vehicleData);
	    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
	}

	// =========================================
	// DELETE
	// =========================================
	@DeleteMapping("/{vehicleNumber}/{vendorName}")
	public ResponseEntity<Void> deleteVehicle(@PathVariable String vendorName, @PathVariable String vehicleNumber) {

		boolean deleted = service.deleteVehicle(vehicleNumber, vendorName);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	// =========================================
	// OPTIONAL: Flexible Input Variants (for testing)
	// =========================================

	@GetMapping("/query")
	public VehicleInfo getVehicleFromQuery(@RequestParam("vehicleno") String vehicleno,
			@RequestParam("vendorname") String vendorname) {
		return service.getVehicle(vehicleno, vendorname);
	}

	@PostMapping("/body")
	public VehicleInfo getVehicleFromBody(@RequestBody Map<String, String> body) {
		String vehicleNumber = body.get("vehiclenumber");
		String vendorname = body.get("vendorname");
		return service.getVehicle(vehicleNumber, vendorname);
	}

	@GetMapping("/header")
	public VehicleInfo getVehicleFromHeaders(@RequestHeader("vehiclenumber") String vehiclenumber,
			@RequestHeader("vendorname") String vendorname) {
		return service.getVehicle(vehiclenumber, vendorname);
	}
}
