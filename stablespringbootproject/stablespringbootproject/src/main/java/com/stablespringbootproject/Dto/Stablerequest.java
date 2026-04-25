package com.stablespringbootproject.Dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class Stablerequest {

    // --- Existing fetch fields ---
    private String country;
    private String vendorname;
    private String phone_number;
    private String api_usage_type;
    private String vehicleno;

    // --- Vendor resolution ---
    private Long vendorId;
    private Long apiId;

    // --- API config ---
    private String apiUrl;
    private String httpMethod;
    private Boolean active;

    // --- Response mappings ---
    private Map<String, String> flatMapping;
    private Map<String, String> nestedMapping;

    // --- Request mappings ---
    private List<RequestMappingDto> requestMappings;

    // --- Extra vehicle fields ---
    private String make;
    private String model;
    private String year;

    // ✅ FIXED: initialized so @JsonAnySetter never throws NullPointerException
    // This captures ANY unknown key from the JSON body (e.g. owner_name, chassis_no etc.)
    private Map<String, Object> additionalFields = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalField(String key, Object value) {
        additionalFields.put(key, value); // ✅ no more NullPointerException
    }

    public Map<String, Object> getAdditionalFields() {
        return additionalFields; // ✅ correct return type Map<String, Object>
    }

    // ---- Inner DTO ----
    public static class RequestMappingDto {
        private String stableField;
        private String externalName;
        private String location;       // PATH | QUERY | HEADER | BODY_JSON
        private String constantValue;

        public String getStableField()         { return stableField; }
        public void setStableField(String v)   { this.stableField = v; }
        public String getExternalName()        { return externalName; }
        public void setExternalName(String v)  { this.externalName = v; }
        public String getLocation()            { return location; }
        public void setLocation(String v)      { this.location = v; }
        public String getConstantValue()       { return constantValue; }
        public void setConstantValue(String v) { this.constantValue = v; }
    }

    // --- Getters & Setters ---
    public String getCountry()               { return country; }
    public void setCountry(String v)         { this.country = v; }

    public String getVendorname()            { return vendorname; }
    public void setVendorname(String v)      { this.vendorname = v; }

    public String getPhone_number()          { return phone_number; }
    public void setPhone_number(String v)    { this.phone_number = v; }

    public String getApi_usage_type()        { return api_usage_type; }
    public void setApi_usage_type(String v)  { this.api_usage_type = v; }

    public String getVehicleno()             { return vehicleno; }
    public void setVehicleno(String v)       { this.vehicleno = v; }

    public Long getVendorId()                { return vendorId; }
    public void setVendorId(Long v)          { this.vendorId = v; }

    public Long getApiId()                   { return apiId; }
    public void setApiId(Long v)             { this.apiId = v; }

    public String getApiUrl()                { return apiUrl; }
    public void setApiUrl(String v)          { this.apiUrl = v; }

    public String getHttpMethod()            { return httpMethod; }
    public void setHttpMethod(String v)      { this.httpMethod = v; }

    public Boolean getActive()               { return active; }
    public void setActive(Boolean v)         { this.active = v; }

    public Map<String, String> getFlatMapping()         { return flatMapping; }
    public void setFlatMapping(Map<String, String> v)   { this.flatMapping = v; }

    public Map<String, String> getNestedMapping()       { return nestedMapping; }
    public void setNestedMapping(Map<String, String> v) { this.nestedMapping = v; }

    public List<RequestMappingDto> getRequestMappings()          { return requestMappings; }
    public void setRequestMappings(List<RequestMappingDto> v)    { this.requestMappings = v; }

    public String getMake()                  { return make; }
    public void setMake(String v)            { this.make = v; }

    public String getModel()                 { return model; }
    public void setModel(String v)           { this.model = v; }

    public String getYear()                  { return year; }
    public void setYear(String v)            { this.year = v; }
}