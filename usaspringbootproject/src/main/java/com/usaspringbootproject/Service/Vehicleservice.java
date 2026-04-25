package com.usaspringbootproject.Service;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.usaspringbootproject.Entity.VehicleEntity;
import com.usaspringbootproject.Repository.VehicleDataRepository;
import com.usaspringbootproject.Repository.VehicleMappingRepository;

@Service
public class Vehicleservice {

    @Autowired
    private VehicleMappingRepository mappingRepository;

    @Autowired
    private VehicleDataRepository dataRepository;

    // =========================================================
    // SKIP FIELDS — add any new fields here, no code change needed
    // =========================================================
    private static final List<String> SKIP_FIELDS = List.of(
            "id", "apiId", "vendorId", "mappingId", "platenumber", "vendorname"
    );

    public VehicleEntity saveVehicle(VehicleEntity vehicle) {
        return dataRepository.save(vehicle);
    }

    // =========================================================
    // CREATE (Dynamic Save - Auto Map Payload → VehicleEntity)
    // =========================================================
    public Object saveDynamicVehicle(JsonNode payload, String platenumber, String vendorname) {

        VehicleEntity vehicle = new VehicleEntity();

        // =========================
        // AUTO MAP: Iterate every key in payload
        // and directly set into VehicleEntity if field exists
        // =========================
        Iterator<Map.Entry<String, JsonNode>> fields = payload.fields();

        while (fields.hasNext()) {

            Map.Entry<String, JsonNode> entry = fields.next();

            String payloadKey = entry.getKey();
            String payloadValue = entry.getValue().asText();

            if (isSkipField(payloadKey)) {
                continue;
            }

            // Directly set into VehicleEntity — no mapping table lookup
            setField(vehicle, payloadKey, payloadValue);
        }

        // =========================
        // IDENTIFIERS
        // =========================
        vehicle.setPlatenumber(platenumber);
        vehicle.setVendorname(vendorname);

        return dataRepository.save(vehicle);
    }

    // =========================================================
    // READ
    // =========================================================
    public VehicleEntity getVehicleByNumber(String platenumber, String vendorname) {
        return dataRepository.findByPlatenumberAndVendorname(platenumber, vendorname);
    }

    // =========================================================
    // UPDATE
    // =========================================================
    @Transactional
    public VehicleEntity updateVehicle(String platenumber, String vendorname, VehicleEntity newData) {

        VehicleEntity existing = getVehicleByNumber(platenumber, vendorname);

        if (existing == null) {
            return null;
        }

        Field[] fields = VehicleEntity.class.getDeclaredFields();

        for (Field field : fields) {

            try {
                field.setAccessible(true);

                if (field.getName().equalsIgnoreCase("id")) {
                    continue;
                }

                Object newValue = field.get(newData);

                if (newValue != null) {
                    field.set(existing, newValue);
                }

            } catch (Exception e) {
                System.out.println("[Update] Skipped field: " + field.getName() + " | Reason: " + e.getMessage());
            }
        }

        return dataRepository.save(existing);
    }

    // =========================================================
    // DELETE
    // =========================================================
    @Transactional
    public boolean deleteVehicle(String platenumber, String vendorname) {

        VehicleEntity vehicle = getVehicleByNumber(platenumber, vendorname);

        if (vehicle == null) {
            return false;
        }

        dataRepository.delete(vehicle);
        return true;
    }

    // =========================================================
    // GET ALL
    // =========================================================
    public List<VehicleEntity> getAllVehicles() {
        return dataRepository.findAll();
    }

    // =========================================================
    // HELPER: Set Value Dynamically
    // =========================================================
    private void setField(Object target, String fieldName, String value) {

        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            // Preserved type conversion since USA Entity uses Long/Integer
            if (field.getType() == Long.class) {
                field.set(target, Long.parseLong(value));
            } else if (field.getType() == Integer.class) {
                field.set(target, Integer.parseInt(value));
            } else {
                field.set(target, value);
            }

        } catch (Exception e) {
            System.out.println("[SetField] Failed for field: " + fieldName + " | Reason: " + e.getMessage());
        }
    }

    // =========================================================
    // HELPER: Dynamic skip check — driven by SKIP_FIELDS list
    // =========================================================
    private boolean isSkipField(String name) {
        return SKIP_FIELDS.stream().anyMatch(skip -> skip.equalsIgnoreCase(name));
    }
}