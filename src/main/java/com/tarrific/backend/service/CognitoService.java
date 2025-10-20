package com.tarrific.backend.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Map;

@Service
public class CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final String clientId;
    private final String userPoolId;

    public CognitoService(Dotenv dotenv) {
        this.clientId = dotenv.get("COGNITO_CLIENT_ID");
        this.userPoolId = dotenv.get("COGNITO_USER_POOL_ID");

        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(dotenv.get("AWS_REGION")))
                .build();
    }

    /**
     * (Optional) Admin helper to mark a password as permanent.
     * You can call this once after creating your Cognito user.
     */
    public void makeAdminPasswordPermanent() {
        cognitoClient.adminSetUserPassword(
                AdminSetUserPasswordRequest.builder()
                        .userPoolId(userPoolId)
                        .username("Admin@admin.com")
                        .password("!Admin123")
                        .permanent(true)
                        .build()
        );

        System.out.println("✅ Password marked permanent for Admin@admin.com");
    }

    /**
     * Authenticates the admin user against AWS Cognito using USER_PASSWORD_AUTH.
     * Returns Cognito's AuthenticationResultType if successful.
     */
    public AuthenticationResultType authenticate(String email, String password) {
        try {
            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .clientId(clientId)
                    .authParameters(Map.of(
                            "USERNAME", email,
                            "PASSWORD", password
                    ))
                    .build();

            InitiateAuthResponse response = cognitoClient.initiateAuth(authRequest);
            return response.authenticationResult();

        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Cognito authentication failed: " + e.awsErrorDetails().errorMessage());
        }
    }
}
