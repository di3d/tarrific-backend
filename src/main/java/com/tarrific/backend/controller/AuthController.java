package com.tarrific.backend.controller;

import com.tarrific.backend.dto.LoginRequest;
import com.tarrific.backend.service.CognitoService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class AuthController {

    private final CognitoService cognitoService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthenticationResultType result = cognitoService.authenticate(
                    request.getEmail(),
                    request.getPassword()
            );

            // Return Cognito tokens directly to frontend
            return ResponseEntity.ok(Map.of(
                    "accessToken", result.accessToken(),
                    "idToken", result.idToken(),
                    "expiresIn", result.expiresIn(),
                    "tokenType", result.tokenType()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}
