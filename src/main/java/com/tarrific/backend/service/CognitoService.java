package com.tarrific.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;
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

    /** ✅ Decode role (Cognito group) from ID token */
    public String getRoleFromIdToken(String idToken) {
        try {
            DecodedJWT decoded = JWT.decode(idToken);
            List<String> groups = decoded.getClaim("cognito:groups").asList(String.class);

            if (groups != null && !groups.isEmpty()) {
                return groups.get(0); // e.g., "Admin", "Agent"
            }
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String calculateSecretHash(String username) {
        try {
            String clientSecret = dotenv.get("COGNITO_CLIENT_SECRET");
            if (clientSecret == null || clientSecret.isEmpty()) {
                return "";
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
