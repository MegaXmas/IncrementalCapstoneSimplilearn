package com.example.travelbuddybackend.controller;

import com.example.travelbuddybackend.models.Client;
import com.example.travelbuddybackend.service.ClientService;
import com.example.travelbuddybackend.service.ClientService.LoginRequest;
import com.example.travelbuddybackend.service.ClientService.LoginResponse;
import com.example.travelbuddybackend.service.ClientService.RegistrationRequest;
import com.example.travelbuddybackend.service.ClientService.RegistrationResponse;
import com.example.travelbuddybackend.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Client Controller for Authentication and Account Management
 *
 * This controller handles all client-related HTTP requests for authentication
 * and basic account management. It serves as the bridge between your Angular
 * frontend and the Spring Boot backend services.
 *
 * Key responsibilities:
 * - Process registration requests from the frontend form
 * - Handle login attempts and return JWT tokens
 * - Provide client profile information
 * - Handle password changes and account updates
 * - Validate and refresh JWT tokens
 *
 * All endpoints use proper HTTP status codes and return JSON responses
 * that your Angular component can easily consume.
 */
@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "http://localhost:4200")  // Allow requests from your Angular app
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Register a new client account
     *
     * This endpoint handles the registration form submission from your Angular component.
     * It matches the form fields exactly: username, email, password, firstName, lastName, phone.
     *
     * Request body should match your Angular ClientRegistration interface:
     * {
     *   "username": "johndoe",
     *   "email": "john@example.com",
     *   "password": "SecurePass123!",
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "phone": "+1234567890"
     * }
     *
     * @param registrationRequest Registration data from Angular form
     * @return ResponseEntity with registration result and appropriate HTTP status
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegistrationResult>> registerClient(@RequestBody RegistrationRequest registrationRequest) {

        System.out.println("Registration request received for username: " + registrationRequest.getUsername());

        try {
            // Process the registration through the service layer
            RegistrationResponse response = clientService.registerClient(registrationRequest);

            if (response.isSuccess()) {
                // Registration successful
                RegistrationResult result = new RegistrationResult(
                        response.getUsername(),
                        "Account created successfully. You can now log in."
                );

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(true, response.getMessage(), result));

            } else {
                // Registration failed due to validation or duplicate data
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, response.getMessage(), null));
            }

        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Registration failed due to server error", null));
        }
    }

    /**
     * Authenticate a client login attempt
     *
     * This endpoint handles login form submissions from your Angular component.
     * It supports login with either username or email address.
     *
     * Request body format:
     * {
     *   "usernameOrEmail": "johndoe",  // or "john@example.com"
     *   "password": "SecurePass123!"
     * }
     *
     * @param loginRequest Login credentials from Angular form
     * @return ResponseEntity with authentication result and JWT token if successful
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResult>> loginClient(
            @RequestBody LoginRequest loginRequest) {

        System.out.println("Login request received for: " + loginRequest.getUsernameOrEmail());

        try {
            // Process the login attempt through the service layer
            LoginResponse response = clientService.authenticateClient(loginRequest);

            if (response.isSuccess()) {
                // Login successful - return token and client data
                LoginResult result = new LoginResult(
                        response.getToken(),
                        response.getClient(),
                        "Login successful"
                );

                return ResponseEntity.ok(new ApiResponse<>(true, response.getMessage(), result));

            } else {
                // Login failed - invalid credentials or account issues
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, response.getMessage(), null));
            }

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Login failed due to server error", null));
        }
    }

    /**
     * Get current client profile information
     *
     * This endpoint allows the Angular app to retrieve client information
     * using a JWT token. The token should be sent in the Authorization header.
     *
     * Header format: Authorization: Bearer <your-jwt-token>
     *
     * @param authHeader The Authorization header containing the JWT token
     * @return ResponseEntity with client profile data
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Client>> getClientProfile(
            @RequestHeader("Authorization") String authHeader) {

        try {
            // Extract token from "Bearer <token>" format
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid authorization header format", null));
            }

            // Validate token and get client
            Optional<Client> clientOptional = clientService.validateTokenAndGetClient(token);

            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                // Return safe client data (without password)
                return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", client));

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid or expired token", null));
            }

        } catch (Exception e) {
            System.err.println("Profile retrieval error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve profile", null));
        }
    }

    /**
     * Update client profile information
     *
     * This endpoint allows clients to update their profile data.
     * The request must include a valid JWT token and the updated client data.
     *
     * @param authHeader The Authorization header containing the JWT token
     * @param updatedClient The updated client information
     * @return ResponseEntity with update result
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Client>> updateClientProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Client updatedClient) {

        try {
            // Extract and validate token
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid authorization header format", null));
            }

            // Validate token and get current client
            Optional<Client> currentClientOptional = clientService.validateTokenAndGetClient(token);

            if (currentClientOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid or expired token", null));
            }

            Client currentClient = currentClientOptional.get();

            // Update the client ID to match the authenticated user (security measure)
            updatedClient.setId(currentClient.getId());

            // Don't allow password updates through this endpoint
            updatedClient.setPassword(currentClient.getPassword());

            // Update the client
            boolean success = clientService.updateClient(updatedClient);

            if (success) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", updatedClient));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Failed to update profile", null));
            }

        } catch (Exception e) {
            System.err.println("Profile update error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Profile update failed due to server error", null));
        }
    }

    /**
     * Change client password
     *
     * This endpoint handles password change requests. It requires the current
     * password for verification and the new password to set.
     *
     * Request body format:
     * {
     *   "currentPassword": "OldPass123!",
     *   "newPassword": "NewPass456!"
     * }
     *
     * @param authHeader The Authorization header containing the JWT token
     * @param passwordChangeRequest The password change data
     * @return ResponseEntity with change result
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PasswordChangeRequest passwordChangeRequest) {

        try {
            // Extract and validate token
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid authorization header format", null));
            }

            // Validate token and get client
            Optional<Client> clientOptional = clientService.validateTokenAndGetClient(token);

            if (clientOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid or expired token", null));
            }

            Client client = clientOptional.get();

            // Change the password
            boolean success = clientService.changePassword(
                    client.getId(),
                    passwordChangeRequest.getCurrentPassword(),
                    passwordChangeRequest.getNewPassword()
            );

            if (success) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Current password is incorrect or new password is invalid", null));
            }

        } catch (Exception e) {
            System.err.println("Password change error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Password change failed due to server error", null));
        }
    }

    /**
     * Refresh JWT token
     *
     * This endpoint allows the Angular app to get a new JWT token before
     * the current one expires, providing seamless authentication.
     *
     * @param authHeader The Authorization header containing the current JWT token
     * @return ResponseEntity with new token if successful
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResult>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {

        try {
            // Extract token from header
            String currentToken = extractTokenFromHeader(authHeader);
            if (currentToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid authorization header format", null));
            }

            // Refresh the token
            String newToken = clientService.refreshToken(currentToken);

            if (newToken != null) {
                TokenRefreshResult result = new TokenRefreshResult(newToken, "Token refreshed successfully");
                return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", result));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Unable to refresh token - please log in again", null));
            }

        } catch (Exception e) {
            System.err.println("Token refresh error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Token refresh failed due to server error", null));
        }
    }

    /**
     * Validate JWT token
     *
     * This endpoint allows the Angular app to check if a token is still valid.
     * Useful for determining if the user needs to log in again.
     *
     * @param authHeader The Authorization header containing the JWT token
     * @return ResponseEntity indicating if token is valid
     */
    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<TokenValidationResult>> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        try {
            // Extract token from header
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid authorization header format", null));
            }

            // Validate token and get client
            Optional<Client> clientOptional = clientService.validateTokenAndGetClient(token);

            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                TokenValidationResult result = new TokenValidationResult(true, client.getUsername());
                return ResponseEntity.ok(new ApiResponse<>(true, "Token is valid", result));
            } else {
                TokenValidationResult result = new TokenValidationResult(false, null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Token is invalid or expired", result));
            }

        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            TokenValidationResult result = new TokenValidationResult(false, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Token validation failed due to server error", result));
        }
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Extract JWT token from Authorization header
     *
     * Handles the standard "Bearer <token>" format used by JWT authentication.
     *
     * @param authHeader The Authorization header value
     * @return The JWT token string, or null if header is invalid
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    // ============================================================================
    // DATA TRANSFER OBJECTS (DTOs) for API responses
    // ============================================================================

    /**
     * Standard API response wrapper
     * This ensures all API responses have a consistent structure
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }

    /**
     * Registration result data
     */
    public static class RegistrationResult {
        private String username;
        private String message;

        public RegistrationResult(String username, String message) {
            this.username = username;
            this.message = message;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * Login result data
     */
    public static class LoginResult {
        private String token;
        private Client client;
        private String message;

        public LoginResult(String token, Client client, String message) {
            this.token = token;
            this.client = client;
            this.message = message;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Client getClient() { return client; }
        public void setClient(Client client) { this.client = client; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * Password change request data
     */
    public static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    /**
     * Token refresh result data
     */
    public static class TokenRefreshResult {
        private String token;
        private String message;

        public TokenRefreshResult(String token, String message) {
            this.token = token;
            this.message = message;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * Token validation result data
     */
    public static class TokenValidationResult {
        private boolean valid;
        private String username;

        public TokenValidationResult(boolean valid, String username) {
            this.valid = valid;
            this.username = username;
        }

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}