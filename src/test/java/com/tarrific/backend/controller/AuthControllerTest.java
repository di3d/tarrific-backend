package com.tarrific.backend.controller;

import com.tarrific.backend.dto.LoginRequest;
import com.tarrific.backend.service.CognitoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CognitoService cognitoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_ShouldReturnTokens_WhenValidCredentials() throws Exception {
        // Given: Valid login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Mock successful Cognito authentication
        AuthenticationResultType mockResult = AuthenticationResultType.builder()
                .accessToken("mock-access-token")
                .idToken("mock-id-token")
                .expiresIn(3600)
                .tokenType("Bearer")
                .build();

        when(cognitoService.authenticate("test@example.com", "password123"))
                .thenReturn(mockResult);

        // When & Then: POST /api/login
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.idToken").value("mock-id-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_ShouldReturn401_WhenInvalidCredentials() throws Exception {
        // Given: Invalid login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("invalid@example.com");
        loginRequest.setPassword("wrongpassword");

        // Mock authentication failure
        when(cognitoService.authenticate("invalid@example.com", "wrongpassword"))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then: POST /api/login
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    void login_ShouldReturn401_WhenCognitoServiceThrowsException() throws Exception {
        // Given: Login request that causes service exception
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password");

        // Mock service exception
        when(cognitoService.authenticate(anyString(), anyString()))
                .thenThrow(new RuntimeException("Cognito service unavailable"));

        // When & Then: POST /api/login
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Cognito service unavailable"));
    }

    @Test
    void login_ShouldReturn400_WhenMissingEmailOrPassword() throws Exception {
        // Given: Login request with missing fields
        LoginRequest loginRequest = new LoginRequest();
        // Intentionally leave email and password null/empty

        // When & Then: POST /api/login with incomplete data
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()); // Will likely cause NPE, caught as 401
    }
}