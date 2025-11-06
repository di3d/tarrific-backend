package com.tarrific.backend.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@Service
public class CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final String clientId;
    private final Dotenv dotenv;

    public CognitoService(Dotenv dotenv) {
        this.dotenv = dotenv;
        this.clientId = dotenv.get("COGNITO_CLIENT_ID");

        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(dotenv.get("AWS_REGION")))
                .build();
    }


    public String getRoleForUser(String username) {
        return "Admin";
    }

    /**
     * Authenticate admin user with Cognito USER_PASSWORD_AUTH flow
     * and compute SECRET_HASH if client has a secret.
     */
    public AuthenticationResultType authenticate(String email, String password) {
        try {
            String secretHash = calculateSecretHash(email);

            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .clientId(clientId)
                    .authParameters(Map.of(
                            "USERNAME", email,
                            "PASSWORD", password,
                            "SECRET_HASH", secretHash
                    ))
                    .build();

            InitiateAuthResponse response = cognitoClient.initiateAuth(authRequest);
            return response.authenticationResult();

        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Cognito authentication failed: " + e.awsErrorDetails().errorMessage());
        }
    }

    /** Compute SECRET_HASH using HmacSHA256 if client secret exists */
    private String calculateSecretHash(String username) {
        try {
            String clientSecret = dotenv.get("COGNITO_CLIENT_SECRET");
            if (clientSecret == null || clientSecret.isEmpty()) {
                return ""; // no secret hash required
            }

            String message = username + clientId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(clientSecret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate SECRET_HASH: " + e.getMessage());
        }
    }
}
