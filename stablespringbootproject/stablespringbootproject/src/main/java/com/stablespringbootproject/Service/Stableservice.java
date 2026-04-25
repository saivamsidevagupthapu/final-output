package com.stablespringbootproject.Service;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stablespringbootproject.Dto.Stablerequest;
import com.stablespringbootproject.Dto.Stableresponse;
import com.stablespringbootproject.Entity.Countryentity;
import com.stablespringbootproject.Entity.Countryserviceentity;
import com.stablespringbootproject.Entity.Vendorapis;
import com.stablespringbootproject.Entity.vehiclerequestmapping;
import com.stablespringbootproject.repository.Countryrepo;
import com.stablespringbootproject.repository.Countryservicerepo;
import com.stablespringbootproject.repository.Vehiclerequestmappingrepo;
import com.stablespringbootproject.repository.Vehicleresponcemappingsrepo;
import com.stablespringbootproject.repository.VendorJsonMappingrepo;
import com.stablespringbootproject.repository.Vendorapirepo;

@Service
public class Stableservice {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	private final Countryrepo countryRepo;
	private final Countryservicerepo serviceRepo;
	private final Vendorapirepo vendorApiRepo;
	private final Vehiclerequestmappingrepo requestMappingRepo;
	private final VendorJsonMappingrepo flatRepo;
	private final Vehicleresponcemappingsrepo nestedRepo;

	public Stableservice(RestTemplate restTemplate, Countryrepo countryRepo, Countryservicerepo serviceRepo,
			Vendorapirepo vendorApiRepo, Vehiclerequestmappingrepo requestMappingRepo, VendorJsonMappingrepo flatRepo,
			Vehicleresponcemappingsrepo nestedRepo) {

		this.restTemplate = restTemplate;
		this.objectMapper = new ObjectMapper();

		this.countryRepo = countryRepo;
		this.serviceRepo = serviceRepo;
		this.vendorApiRepo = vendorApiRepo;
		this.requestMappingRepo = requestMappingRepo;
		this.flatRepo = flatRepo;
		this.nestedRepo = nestedRepo;
	}

	public Stableresponse handleRequest(Stablerequest request) {
		return execute(request);
	}

	private Stableresponse execute(Stablerequest request) {

		Countryentity country = countryRepo.findByCountryCode(request.getCountry())
				.orElseThrow(() -> notFound("Country not found"));
		

		Countryserviceentity service = serviceRepo.findFirstByCountryCodeAndActiveTrue(country.getCountryCode())
				.orElseThrow(() -> notFound("Service not active"));

		if (request.getVendorId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "vendorId required");
		}

		Vendorapis api = resolveApi(request);
		System.out.println(api.toString());
		List<vehiclerequestmapping> reqMappings = requestMappingRepo.findByVendorIdAndApiId(api.getVendorId(),
				api.getApiId());

		JsonNode rawResponse = callVendor(service, api, reqMappings, request);

		List<?> respMappings = resolveResponseMapping(api.getVendorId(), api.getApiId());
		Map<String, Object> finalMap = mapResponse(rawResponse, respMappings);

		if (finalMap.isEmpty()) {
		    Map<String, Object> rawMap = new LinkedHashMap<>();
		    rawResponse.fields().forEachRemaining(entry -> {
		        JsonNode val = entry.getValue();
		        if (val.isObject() || val.isArray()) {
		            rawMap.put(entry.getKey(), val.toString());
		        } else {
		            rawMap.put(entry.getKey(), val.asText());
		        }
		    });
		    finalMap = rawMap;  // ✅ assign after lambda, no error
		}

		if (finalMap.isEmpty()) {
		    Map<String, Object> rawMap = new LinkedHashMap<>();
		    rawResponse.fields().forEachRemaining(entry -> {
		        JsonNode val = entry.getValue();
		        if (val.isObject() || val.isArray()) {
		            rawMap.put(entry.getKey(), val.toString());
		        } else {
		            rawMap.put(entry.getKey(), val.asText());
		        }
		    });
		    finalMap = rawMap;  // ✅ assign after lambda, no error
		}
		Stableresponse response = new Stableresponse();
		response.setCountry(country.getCountryCode());

		// For DELETE/no-content responses, just set a success message
		if (rawResponse.isEmpty()) {
	        finalMap.put("status", "success");
	        finalMap.put("message", "Operation completed successfully");
	    }

	    response.setVehicleDetails(finalMap);
	    return response;
	}

	private Vendorapis resolveApi(Stablerequest request) {

		List<Vendorapis> apis = vendorApiRepo.findByVendorIdAndApiType(request.getVendorId(),
				request.getApi_usage_type());

		// DEBUG: Print what we got from DB
		System.out.println("=== resolveApi DEBUG ===");
		System.out.println("Request vendorId     : " + request.getVendorId());
		System.out.println("Request api_usage_type: " + request.getApi_usage_type());
		System.out.println("Request httpMethod   : '" + request.getHttpMethod() + "'");
		System.out.println("Request apiUrl       : '" + request.getApiUrl() + "'");
		System.out.println("Total APIs fetched   : " + apis.size());

		for (int i = 0; i < apis.size(); i++) {
			Vendorapis a = apis.get(i);
			System.out.println("--- API[" + i + "] ---");
			System.out.println("  vendorId  : " + a.getVendorId());
			System.out.println("  apiId     : " + a.getApiId());
			System.out.println("  apiType   : " + a.getApiType());
			System.out.println("  httpMethod: '" + a.getHttpMethod() + "'");
			System.out.println("  apiUrl    : '" + a.getApiUrl() + "'");
			System.out.println("  bodyType  : " + a.getBodyType());
			System.out.println("  active    : " + a.getActive());

			// Show filter evaluation per record
			boolean methodMatch = equalsIgnoreCase(a.getHttpMethod(), request.getHttpMethod());
			boolean urlMatch = request.getApiUrl() == null || a.getApiUrl().equalsIgnoreCase(request.getApiUrl());
			System.out.println("  methodMatch: " + methodMatch + " (DB='" + a.getHttpMethod() + "' vs REQ='"
					+ request.getHttpMethod() + "')");
			System.out.println("  urlMatch   : " + urlMatch + " (DB='" + a.getApiUrl() + "' vs REQ='"
					+ request.getApiUrl() + "')");
			System.out.println("  WILL MATCH : " + (methodMatch && urlMatch));
		}
		System.out.println("========================");

		return apis.stream()
				.filter(a -> equalsIgnoreCase(a.getHttpMethod(), request.getHttpMethod())
						&& (request.getApiUrl() == null || a.getApiUrl().equalsIgnoreCase(request.getApiUrl())))
				.findFirst().orElseThrow(() -> notFound("API not found"));
	}

	private JsonNode callVendor(Countryserviceentity service, Vendorapis api, List<vehiclerequestmapping> mappings,
			Stablerequest request) {

		Map<String, String> input = toMap(request);

		Map<String, String> query = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		Map<String, Object> body = new HashMap<>();
		Map<String, String> pathVars = new HashMap<>();

// Fields to skip — internal stable-service fields
		Set<String> skipFields = new HashSet<>(
				java.util.Arrays.asList("country", "vendorId", "api_usage_type", "httpMethod", "apiUrl"));

// Extract path variable names from URL template automatically
// e.g. /vehicle/{vendorname}/{vehiclenumber} → [vendorname, vehiclenumber]
		Set<String> pathVarNames = new HashSet<>();
		java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\{(\\w+)\\}").matcher(api.getApiUrl());
		while (matcher.find()) {
			pathVarNames.add(matcher.group(1));
		}

		System.out.println("=== callVendor DEBUG ===");
		System.out.println("HTTP Method         : " + api.getHttpMethod());
		System.out.println("Body Type           : " + api.getBodyType());
		System.out.println("Base URL            : " + service.getBaseUrl());
		System.out.println("API URL             : " + api.getApiUrl());
		System.out.println("Input map           : " + input);
		System.out.println("Path var names      : " + pathVarNames);
		System.out.println("Total DB mappings   : " + mappings.size());

		if (mappings == null || mappings.isEmpty()) {
// ✅ NO DB MAPPINGS — auto route based on HTTP method and URL template
			System.out.println("No DB mappings found — using AUTO routing");

			for (Map.Entry<String, String> entry : input.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				if (skipFields.contains(key)) {
					System.out.println("  SKIP (internal)   : " + key);
					continue;
				}

				if (pathVarNames.contains(key)) {
					pathVars.put(key, value);
					System.out.println("  AUTO PATH         : " + key + " = " + value);
				} else if ("PUT".equalsIgnoreCase(api.getHttpMethod())
						|| "POST".equalsIgnoreCase(api.getHttpMethod())) {
					body.put(key, value);
					System.out.println("  AUTO BODY_JSON    : " + key + " = " + value);
				} else if ("GET".equalsIgnoreCase(api.getHttpMethod())) {
					body.put(key, value);
					System.out.println("  AUTO HEADER       : " + key + " = " + value);
				} else {
					System.out.println("  SKIP (no route)   : " + key);
				}
			}

		} else {
// ✅ DB MAPPINGS EXIST — use them first
			System.out.println("Using DB mappings");

			for (vehiclerequestmapping m : mappings) {
				System.out.println("--- DB Mapping ---");
				System.out.println("  stableField  : '" + m.getStableField() + "'");
				System.out.println("  externalName : '" + m.getExternalName() + "'");
				System.out.println("  location     : " + m.getLocation());

				String value = input.get(m.getStableField());
				System.out.println("  resolvedValue: '" + value + "'");

				if (value == null) {
					System.out.println("  STATUS: SKIPPED (null value)");
					continue;
				}

				switch (m.getLocation()) {
				case QUERY -> {
					query.put(m.getExternalName(), value);
					System.out.println("  STATUS: added to QUERY");
				}
				case HEADER -> {
					headers.put(m.getExternalName(), value);
					System.out.println("  STATUS: added to HEADER");
				}
				case BODY_JSON -> {
					body.put(m.getExternalName(), value);
					System.out.println("  STATUS: added to BODY_JSON");
				}
				case PATH -> {
					pathVars.put(m.getExternalName(), value);
					System.out.println("  STATUS: added to PATH");
				}
				}
			}

// ✅ FOR PUT/POST — auto-add extra fields not covered by DB mappings
			if ("PUT".equalsIgnoreCase(api.getHttpMethod()) || "POST".equalsIgnoreCase(api.getHttpMethod())) {

				Set<String> mappedFields = mappings.stream().map(m -> m.getStableField())
						.collect(java.util.stream.Collectors.toSet());

				System.out.println("Checking for extra fields not in DB mappings...");

				for (Map.Entry<String, String> entry : input.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();

					if (skipFields.contains(key))
						continue;
					if (mappedFields.contains(key))
						continue;
					if (pathVarNames.contains(key))
						continue;

					body.put(key, value);
					System.out.println("  EXTRA BODY_JSON   : " + key + " = " + value);
				}
			}
		}

		System.out.println("--- After mapping ---");
		System.out.println("Query params : " + query);
		System.out.println("Headers      : " + headers);
		System.out.println("Body         : " + body);
		System.out.println("Path vars    : " + pathVars);

// Build URI with proper encoding (handles spaces and special characters)
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(service.getBaseUrl() + api.getApiUrl());
		query.forEach(builder::queryParam);

		URI finalUri = builder.buildAndExpand(pathVars).toUri();
		System.out.println("Final encoded URI : " + finalUri);

		HttpHeaders httpHeaders = new HttpHeaders();
		headers.forEach(httpHeaders::add);

// Set Content-Type for JSON body
		if (!body.isEmpty() || "JSON".equalsIgnoreCase(api.getBodyType())) {
			httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		}

		Object requestBody = body.isEmpty() ? null : buildBody("JSON", body);
		System.out.println("Request body  : " + requestBody);
		System.out.println("========================");

		HttpEntity<?> entity = new HttpEntity<>(requestBody, httpHeaders);

		try {
			ResponseEntity<String> response = restTemplate.exchange(finalUri, HttpMethod.valueOf(api.getHttpMethod()),
					entity, String.class);

			System.out.println("Vendor response status : " + response.getStatusCode());
			System.out.println("Vendor response body   : " + response.getBody());

// Handle null body (e.g. DELETE returns 204 No Content)
			if (response.getBody() == null || response.getBody().isEmpty()) {
				return objectMapper.createObjectNode();
			}

			return objectMapper.readTree(response.getBody());

		} catch (Exception e) {
			System.out.println("Vendor call FAILED: " + e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Vendor API failed: " + e.getMessage());
		}
	}

	private Object buildBody(String bodyType, Map<String, Object> body) {

		if (bodyType == null)
			return null;

		switch (bodyType.toUpperCase()) {

		case "JSON":
			return body;

		case "FORM":
			MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
			body.forEach((k, v) -> form.add(k, v.toString()));
			return form;

		case "RAW":
			return body.getOrDefault("raw", null);

		default:
			return null;
		}
	}

	// ✅ FIXED METHOD (ONLY CHANGE)
	private Map<String, Object> mapResponse(JsonNode root, List<?> mappings) {

	    Map<String, Object> result = new LinkedHashMap<>();

	    // ✅ No mappings — return full raw response
	    if (mappings == null || mappings.isEmpty()) {
	        System.out.println("No response mappings — returning raw vendor response");
	        root.fields().forEachRemaining(entry -> {
	            JsonNode val = entry.getValue();
	            if (val.isObject() || val.isArray()) {
	                result.put(entry.getKey(), val.toString());
	            } else {
	                result.put(entry.getKey(), val.asText());
	            }
	        });
	        return result;
	    }

	    System.out.println("=== DYNAMIC RESPONSE MAPPING ===");

	    for (Object obj : mappings) {
	        try {
	            String jsonPath = null;
	            String outputKey = null;

	            for (Field field : obj.getClass().getDeclaredFields()) {
	                field.setAccessible(true);
	                Object value = field.get(obj);
	                if (value == null) continue;

	                String strVal = value.toString().trim();

	                if (strVal.startsWith("/")) {
	                    jsonPath = strVal;
	                } else if (!strVal.matches("\\d+")) {
	                    outputKey = strVal;
	                }
	            }

	            if (jsonPath == null || outputKey == null) {
	                System.out.println("Skipped mapping (invalid): " + obj);
	                continue;
	            }

	            JsonNode valueNode = root.at(jsonPath);

	            // ✅ Handle nested objects too
	            if (valueNode.isObject() || valueNode.isArray()) {
	                result.put(outputKey, valueNode.toString());
	            } else {
	                result.put(outputKey, valueNode.isMissingNode() ? "" : valueNode.asText());
	            }

	            System.out.println("Mapped: " + outputKey + " -> " + result.get(outputKey));

	        } catch (Exception e) {
	            System.out.println("Mapping error: " + e.getMessage());
	        }
	    }

	    System.out.println("Final Result: " + result);
	    System.out.println("==============================");

	    return result;
	}

	private List<?> resolveResponseMapping(Long vendorId, Long apiId) {

		List<?> flat = flatRepo.findByVendorIdAndApiId(vendorId, apiId);
		if (flat != null && !flat.isEmpty())
			return flat;

		return nestedRepo.findByVendorIdAndApiId(vendorId, apiId);
	}

	private Map<String, String> toMap(Object obj) {
	    Map<String, String> map = new HashMap<>();

	    for (Field f : obj.getClass().getDeclaredFields()) {
	        try {
	            f.setAccessible(true);
	            Object val = f.get(obj);
	            if (val == null) continue;

	            // ✅ Handle the additionalFields map specially
	            if (val instanceof Map) {
	                ((Map<?, ?>) val).forEach((k, v) -> {
	                    if (v != null) map.put(k.toString(), v.toString());
	                });
	            } else {
	                map.put(f.getName(), val.toString());
	            }
	        } catch (Exception ignored) {}
	    }
	    return map;
	}

	private boolean equalsIgnoreCase(String a, String b) {
		return a != null && b != null && a.equalsIgnoreCase(b);
	}

	private ResponseStatusException notFound(String msg) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
	}
}
