package com.stablespringbootproject.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiclerequestmapping")
public class vehiclerequestmapping {

    // Define the Enum inside the class so the Service can find it
    public enum LocationType {
        PATH, QUERY, HEADER, BODY_JSON
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "api_id")
    private Long apiId;

    @Column(name = "stable_field")
    private String stableField;

    // Change String to the Enum LocationType
    @Enumerated(EnumType.STRING)
    @Column(name = "location")
    private LocationType location;

    @Column(name = "external_name")
    private String externalName;

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVendorId() {
		return vendorId;
	}
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}
	public Long getApiId() {
		return apiId;
	}
	public void setApiId(Long apiId) {
		this.apiId = apiId;
	}
	public String getStableField() {
		return stableField;
	}
	public void setStableField(String stableField) {
		this.stableField = stableField;
	}
	public String getExternalName() {
		return externalName;
	}
	public void setExternalName(String externalName) {
		this.externalName = externalName;
	}
	public String getConstantValue() {
		return constantValue;
	}
	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}
	@Column(name = "constant_value")
    private String constantValue;

    // Standard Getters and Setters
    public LocationType getLocation() { return location; }
    public void setLocation(LocationType location) { this.location = location; }

    // ... Keep other existing getters and setters ...
}