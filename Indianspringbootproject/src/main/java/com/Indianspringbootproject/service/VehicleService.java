package com.Indianspringbootproject.service;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Indianspringbootproject.Entity.VehicleInfo;
import com.Indianspringbootproject.Repository.VehicleDataRepository;
import com.Indianspringbootproject.Repository.VehicleRepository;
import com.Indianspringbootproject.Repository.VehicleResponseMappingRepository;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class VehicleService {

	@Autowired
	private VehicleRepository mappingRepository;

	@Autowired
	private VehicleResponseMappingRepository flatMappingRepository;

	@Autowired
	private VehicleDataRepository dataRepository;

	// =========================================================
	// SKIP FIELDS — add any new fields here, no code change needed
	// =========================================================
	private static final List<String> SKIP_FIELDS = List.of("id", "apiId", "vendorId", "mappingId", "vehicleNumber",
			"vehicleno", "vendorname");

	// =========================================================
	// CREATE (Dynamic Save - Flat + Nested JSON)
	// =========================================================
	// =========================================================
	// CREATE (Dynamic Save - Auto Map Payload → VehicleInfo)
	// =========================================================
	public Object saveDynamicVehicle(JsonNode payload, String vehicleNumber, String vendorName) {

		VehicleInfo vehicle = new VehicleInfo();

		// =========================
		// AUTO MAP: Iterate every key in payload
		// and directly set into VehicleInfo if field exists
		// =========================
		Iterator<Map.Entry<String, JsonNode>> fields = payload.fields();

		while (fields.hasNext()) {

			Map.Entry<String, JsonNode> entry = fields.next();

			String payloadKey = entry.getKey();
			String payloadValue = entry.getValue().asText();

			if (isSkipField(payloadKey)) {
				continue;
			}

			// Directly set into VehicleInfo — no mapping table lookup
			setValue(vehicle, payloadKey, payloadValue);
		}

		// =========================
		// IDENTIFIERS
		// =========================
		vehicle.setVehicleNumber(vehicleNumber);
		vehicle.setVendorname(vendorName);

		return dataRepository.save(vehicle);
	}

	// =========================================================
	// READ
	public VehicleInfo getVehicle(String vehicleNumber, String vendorName) {

		return dataRepository.findByVehicleNumberAndVendorname(vehicleNumber, vendorName);
	}

	// =========================================================
	// UPDATE
	// =========================================================
	@Transactional
	public VehicleInfo updateVehicle(String vehicleNumber, String vendorName, VehicleInfo newData) {

		VehicleInfo existing = getVehicle(vehicleNumber, vendorName);

		if (existing == null) {
			return null;
		}

		Field[] fields = VehicleInfo.class.getDeclaredFields();

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
	public boolean deleteVehicle(String vehicleNumber, String vendorName) {

		VehicleInfo vehicle = getVehicle(vehicleNumber, vendorName);

		if (vehicle == null) {
			return false;
		}

		dataRepository.delete(vehicle);
		return true;
	}

	// =========================================================
	// GET ALL
	// =========================================================
	public List<VehicleInfo> getAllVehicles() {
		return dataRepository.findAll();
	}

	// =========================================================
	// HELPER: Set Value Dynamically
	// =========================================================
	private void setValue(Object object, String fieldName, String value) {

		try {
			Field field = object.getClass().getDeclaredField(fieldName);

			field.setAccessible(true);
			field.set(object, value);

		} catch (Exception e) {
			System.out.println("[SetValue] Failed for field: " + fieldName + " | Reason: " + e.getMessage());
		}
	}

	// =========================================================
	// HELPER: Dynamic skip check — driven by SKIP_FIELDS list
	// ✅ CHANGED: replaces old isSystemField() hardcoded method
	// =========================================================
	private boolean isSkipField(String name) {
		return SKIP_FIELDS.stream().anyMatch(skip -> skip.equalsIgnoreCase(name));
	}
}