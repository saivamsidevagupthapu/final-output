package com.stablespringbootproject.Entity;

import jakarta.persistence.*;


	
	
	@Entity
	@Table(name = "vendor_apis")
	public class Vendorapis {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long apiId;

	    private Long vendorId;

	    private String apiType;     // VEHICLE, RC, etc

	    private String apiUrl;

	    private String httpMethod;  // GET, POST, PUT, DELETE

	    private String bodyType;    // JSON, FORM, RAW

	    public String getBodyType() {
			return bodyType;
		}

		public void setBodyType(String bodyType) {
			this.bodyType = bodyType;
		}

		public Boolean getActive() {
			return active;
		}

		public void setActive(Boolean active) {
			this.active = active;
		}

		private Boolean active;
	    
	    @Column(name = "content_type")
		private String contentType; // e.g. "application/json"

	    // getters & setters

	public Long getApiId() {
		return apiId;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public void setApiId(Long apiId) {
		this.apiId = apiId;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}