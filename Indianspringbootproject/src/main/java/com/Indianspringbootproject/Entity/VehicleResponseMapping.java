
package com.Indianspringbootproject.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicleresponcemapping")
public class VehicleResponseMapping {

	@Id
	@Column(name = "mapping_id") // <-- match DB column
	private Long mappingId;

	@Column(name = "api_id")
	private Long apiId;

	@Column(name = "make")
	private String make;

	@Column(name = "model")
	private String model;

	@Column(name = "year")
	private String year;

	@Column(name = "vehicle_number")
	private String vehicleNumber;

	@Column(name = "vehielcId")
	private Long vendorId;

	@Column(name = "vendorname")
	private String vendorname;

	// --- GETTERS AND SETTERS ---

	public String getVendorname() {
		return vendorname;
	}

	public void setVendorname(String vendorname) {
		this.vendorname = vendorname;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	public Long getMappingId() {
		return mappingId;
	}

	public void setMappingId(Long mappingId) {
		this.mappingId = mappingId;
	}

	public Long getApiId() {
		return apiId;
	}

	public void setApiId(Long apiId) {
		this.apiId = apiId;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
}
