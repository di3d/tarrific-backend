package com.tarrific.backend.controller;

import com.tarrific.backend.dto.ExchangeResponse;
import com.tarrific.backend.service.ExchangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"${CORS_ORIGIN}"}, allowCredentials = "true")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/exchange")
    public ResponseEntity<?> getExchange(
            @RequestParam(name = "from") String from,
            @RequestParam(name = "to") String to,
            @RequestParam(name = "amount") String amountStr) {

        // ✅ Validate query params
        if (from == null || from.isBlank() || to == null || to.isBlank() ||
                amountStr == null || amountStr.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Missing query parameters (from, to, amount)"));
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid amount format: " + amountStr));
        }

        try {
            // ✅ Call CurrencyAPI via service
            ExchangeResponse resp = exchangeService.convert(from.toUpperCase(), to.toUpperCase(), amount);
            return ResponseEntity.ok(resp);

        } catch (IllegalArgumentException e) {
            // Input validation errors
            System.err.println("⚠️ Validation error: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (RuntimeException e) {
            // Provider-related errors (e.g., 404/401/502)
            System.err.println("❌ Provider error: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of(
                            "error", "Currency provider error",
                            "detail", e.getMessage()
                    ));

        } catch (Exception e) {
            // Unexpected internal errors
            System.err.println("💥 Internal error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Internal server error",
                            "detail", e.getMessage()
                    ));
        }
    }
}
