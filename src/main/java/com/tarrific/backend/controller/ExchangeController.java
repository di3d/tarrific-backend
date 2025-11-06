package com.tarrific.backend.controller;

import com.tarrific.backend.dto.ExchangeResponse;
import com.tarrific.backend.service.ExchangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class ExchangeController {
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/exchange")
    public ResponseEntity<?> getExchange(@RequestParam(name = "from") String from,
                                         @RequestParam(name = "to") String to,
                                         @RequestParam(name = "amount") String amountStr) {
        if (from == null || from.isBlank() || to == null || to.isBlank() || amountStr == null || amountStr.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("error", "Missing query parameters (from,to,amount)"));
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("error", "Invalid amount"));
        }

        try {
            ExchangeResponse resp = exchangeService.convert(from, to, amount);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // If the provider returned something bad, map to 502
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(java.util.Map.of("error", "Provider error", "detail", e.getMessage()));
        }
    }
}
