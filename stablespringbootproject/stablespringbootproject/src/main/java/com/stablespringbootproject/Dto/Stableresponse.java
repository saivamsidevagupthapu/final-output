package com.stablespringbootproject.Dto;

import java.util.Map;

public class Stableresponse {

    private String country;
    private Map<String, Object> vehicleDetails;
    private String message;   // used by CRUD operations

    public String getCountry()                       { return country; }
    public void setCountry(String v)                 { this.country = v; }
    public Map<String, Object> getVehicleDetails()   { return vehicleDetails; }
    public void setVehicleDetails(Map<String, Object> v) { this.vehicleDetails = v; }
    public String getMessage()                       { return message; }
    public void setMessage(String v)                 { this.message = v; }
}