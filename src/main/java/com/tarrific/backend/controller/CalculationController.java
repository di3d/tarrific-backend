package com.tarrific.backend.controller;

import com.tarrific.backend.dto.CalculationRequest;
import com.tarrific.backend.dto.CalculationResponse;
import com.tarrific.backend.service.CalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CalculationController {

    private final CalculationService calculationService;

    @PostMapping("/calculate")
    public ResponseEntity<?> calculate(@RequestBody CalculationRequest request) {
        try {
            CalculationResponse response = calculationService.calculate(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to calculate tariff: " + e.getMessage()));
        }
    }
}
