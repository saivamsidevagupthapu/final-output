package com.usaspringbootproject.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.usaspringbootproject.Entity.VehicleEntity;
import com.usaspringbootproject.Service.Vehicleservice;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/usa") 
public class VehicleController {

    @Autowired
    private Vehicleservice service;

    // =========================================
    // CREATE (Dynamic)
    // =========================================
    @PostMapping("/{platenumber}/{vendorname}")
    public ResponseEntity<?> createVehicle(
            @RequestBody JsonNode payload,
            @PathVariable String platenumber,
            @PathVariable String vendorname) {

        try {
            // Note: This relies on the refactored saveDynamicVehicle from the previous step
            Object result = service.saveDynamicVehicle(payload, platenumber, vendorname);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // =========================================
    // READ (Standard - Recommended)
    // =========================================
    @GetMapping("/{platenumber}/{vendorname}")
    public ResponseEntity<VehicleEntity> getVehicle(
            @PathVariable("platenumber") String platenumber,
            @PathVariable("vendorname") String vendorname) {
        
        VehicleEntity vehicle = service.getVehicleByNumber(platenumber, vendorname);
        return vehicle != null ? ResponseEntity.ok(vehicle) : ResponseEntity.notFound().build();
    }

    // =========================================
    // UPDATE
    // =========================================
    @PutMapping("/{platenumber}/{vendorname}")
    public ResponseEntity<VehicleEntity> updateVehicle(
            @PathVariable String platenumber,
            @PathVariable String vendorname,
            @RequestBody VehicleEntity vehicleData) {

        VehicleEntity updated = service.updateVehicle(platenumber, vendorname, vehicleData);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // =========================================
    // DELETE
    // =========================================
    @DeleteMapping("/{platenumber}/{vendorname}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable String vendorname, 
            @PathVariable String platenumber) {

        boolean deleted = service.deleteVehicle(platenumber, vendorname);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // =========================================
    // OPTIONAL: Flexible Input Variants (for testing)
    // =========================================

    @GetMapping("/query")
    public VehicleEntity getVehicleFromQuery(
            @RequestParam("platenumber") String platenumber,
            @RequestParam("vendorname") String vendorname) {
        return service.getVehicleByNumber(platenumber, vendorname);
    }

    @PostMapping("/body")
    public VehicleEntity getVehicleFromBody(@RequestBody Map<String, String> body) {
        String platenumber = body.get("platenumber");
        String vendorname = body.get("vendorname");
        return service.getVehicleByNumber(platenumber, vendorname);
    }

    @GetMapping("/header")
    public VehicleEntity getVehicleFromHeaders(
            @RequestHeader("platenumber") String platenumber,
            @RequestHeader("vendorname") String vendorname) {
        return service.getVehicleByNumber(platenumber, vendorname);
    }
}