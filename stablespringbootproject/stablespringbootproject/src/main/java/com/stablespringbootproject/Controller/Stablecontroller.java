package com.stablespringbootproject.Controller;

import com.stablespringbootproject.Dto.Stablerequest;
import com.stablespringbootproject.Dto.Stableresponse;
import com.stablespringbootproject.Service.Stableservice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stable")
//@CrossOrigin(origins = "http://localhost:5173") // ✅ FIXED: Added CORS so React can successfully make requests to this controller
public class Stablecontroller {

    private final Stableservice service;

    public Stablecontroller(Stableservice service) {
        this.service = service;
    }

    @PostMapping("/vehicle")
    public ResponseEntity<Stableresponse> execute(@RequestBody Stablerequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        return ResponseEntity.ok(service.handleRequest(request));
    }
}