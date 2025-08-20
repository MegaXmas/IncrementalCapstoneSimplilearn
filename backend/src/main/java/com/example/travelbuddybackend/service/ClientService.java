package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.BusStation;
import com.example.travelbuddybackend.models.Client;
import com.example.travelbuddybackend.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Enhanced Client Service with Authentication Support
 *
 * This service acts as the business logic layer for client management and authentication.
 * Think of it as your customer service manager who:
 * - Handles new customer registrations
 * - Processes login requests
 * - Manages client accounts and profiles
 * - Enforces business rules and security policies
 *
 * Key Principles:
 * - Passwords are always encrypted before storage
 * - All input is validated before database operations
 * - Clear error messages help users understand issues
 * - Security is built into every authentication operation
 */
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Email validation pattern - ensures proper email format
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    @Autowired
    public ClientService(ClientRepository clientRepository,
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ============================================================================
    // AUTHENTICATION METHODS - Handle login and registration
    // ============================================================================

    /**
     * Register a new client account
     *
     * This is the complete registration process that handles:
     * - Input validation
     * - Duplicate checking (username and email)
     * - Password encryption
     * - Account creation
     *
     * @param registrationRequest Contains all registration information
     * @return RegistrationResponse with success/failure details
     */
    public RegistrationResponse registerClient(RegistrationRequest registrationRequest) {
        System.out.println("Processing registration for: " + registrationRequest.getUsername());

        // Step 1: Validate all input data
        ValidationResult validation = validateRegistrationData(registrationRequest);
        if (!validation.isValid()) {
            return new RegistrationResponse(false, validation.getErrorMessage(), null);
        }

        // Step 2: Check for existing username
        if (clientRepository.existsByUsername(registrationRequest.getUsername())) {
            return new RegistrationResponse(false, "Username '" + registrationRequest.getUsername() + "' is already taken", null);
        }

        // Step 3: Check for existing email
        if (clientRepository.existsByEmail(registrationRequest.getEmail())) {
            return new RegistrationResponse(false, "Email address is already registered", null);
        }

        // Step 4: Create new client with encrypted password
        Client newClient = new Client();
        newClient.setUsername(registrationRequest.getUsername());
        newClient.setEmail(registrationRequest.getEmail());

        // CRITICAL: Always encrypt passwords before storing them
        String encryptedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        newClient.setPassword(encryptedPassword);

        newClient.setFirstName(registrationRequest.getFirstName());
        newClient.setLastName(registrationRequest.getLastName());
        newClient.setPhone(registrationRequest.getPhone());
        newClient.setAddress(registrationRequest.getAddress());
        newClient.setCreatedAt(LocalDateTime.now());

        // New accounts are enabled by default and not locked
        newClient.setEnabled(true);
        newClient.setAccountLocked(false);

        // Step 5: Save to database
        boolean created = clientRepository.createClient(newClient);
        if (created) {
            System.out.println("✓ Registration successful for: " + newClient.getUsername());
            return new RegistrationResponse(true, "Account created successfully", newClient.getUsername());
        } else {
            return new RegistrationResponse(false, "Failed to create account. Please try again.", null);
        }
    }

    /**
     * Authenticate a client login attempt
     *
     * This handles the complete login process:
     * - Finding the user account
     * - Verifying account status
     * - Checking password
     * - Generating authentication token
     * - Updating login timestamp
     *
     * @param loginRequest Contains username/email and password
     * @return LoginResponse with authentication result
     */
    public LoginResponse authenticateClient(LoginRequest loginRequest) {
        System.out.println("Processing login attempt for: " + loginRequest.getUsernameOrEmail());

        // Step 1: Find the client account
        Optional<Client> clientOptional = findClientByUsernameOrEmail(loginRequest.getUsernameOrEmail());

        if (clientOptional.isEmpty()) {
            System.out.println("✗ Client not found: " + loginRequest.getUsernameOrEmail());
            return new LoginResponse(false, "Invalid login credentials", null, null);
        }

        Client client = clientOptional.get();

        // Step 2: Check account status
        if (!client.canLogin()) {
            String reason = client.isAccountLocked() ? "Account is temporarily locked" : "Account is disabled";
            System.out.println("✗ Login denied - " + reason + ": " + client.getUsername());
            return new LoginResponse(false, reason, null, null);
        }

        // Step 3: Verify password
        boolean passwordMatches = passwordEncoder.matches(
                loginRequest.getPassword(),
                client.getPassword()
        );

        if (!passwordMatches) {
            System.out.println("✗ Invalid password for client: " + client.getUsername());
            return new LoginResponse(false, "Invalid login credentials", null, null);
        }

        // Step 4: Generate JWT token for successful login
        String token = jwtService.generateToken(client);

        // Step 5: Update last login timestamp
        clientRepository.updateLastLogin(client.getId());

        System.out.println("✓ Login successful for: " + client.getUsername());

        // Return success response with safe client data (no password)
        return new LoginResponse(true, "Login successful", token, client.getSafeClient());
    }

    /**
     * Change a client's password
     *
     * This allows clients to update their passwords securely.
     * It verifies the old password before setting the new one.
     *
     * @param clientId The ID of the client changing their password
     * @param oldPassword The current password for verification
     * @param newPassword The new password to set
     * @return true if password was changed successfully, false otherwise
     */
    public boolean changePassword(Integer clientId, String oldPassword, String newPassword) {
        if (clientId == null || oldPassword == null || newPassword == null) {
            System.out.println("✗ Service: Invalid parameters for password change");
            return false;
        }

        // Find the client
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isEmpty()) {
            System.out.println("✗ Service: Client not found for password change");
            return false;
        }

        Client client = clientOptional.get();

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, client.getPassword())) {
            System.out.println("✗ Service: Old password is incorrect");
            return false;
        }

        // Validate new password
        if (newPassword.length() < 6) {
            System.out.println("✗ Service: New password must be at least 6 characters long");
            return false;
        }

        // Encrypt and update new password
        String encryptedNewPassword = passwordEncoder.encode(newPassword);
        boolean updated = clientRepository.updatePassword(clientId, encryptedNewPassword);

        if (updated) {
            System.out.println("✓ Service: Password changed successfully for client ID: " + clientId);
        }

        return updated;
    }

    // ============================================================================
    // CLIENT MANAGEMENT METHODS - Handle regular client operations
    // ============================================================================

    /**
     * Get all clients in the system
     * This method works exactly as before - no changes needed
     *
     * @return List of all clients (empty list if none found)
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Get a client by their ID
     * This method works exactly as before - no changes needed
     *
     * @param id The client ID to search for
     * @return Optional containing the client if found, empty otherwise
     */
    public Optional<Client> getClientById(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Client ID cannot be null");
            return Optional.empty();
        }
        return clientRepository.findById(id);
    }

    /**
     * Add a new client to the database
     * This method has been enhanced to handle authentication fields
     *
     * @param client The client to add
     * @return true if client was successfully added, false otherwise
     */
    public boolean addClient(Client client) {
        if (client == null) {
            System.out.println("✗ Service Error: Cannot add null client");
            return false;
        }

        // If the client has a password, encrypt it
        if (client.getPassword() != null && !client.getPassword().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(client.getPassword());
            client.setPassword(encryptedPassword);
        }

        boolean success = clientRepository.createClient(client);
        if (success) {
            System.out.println("✓ Service: Client successfully added: " + client.getUsername());
        } else {
            System.out.println("✗ Service: Failed to add client");
        }
        return success;
    }

    /**
     * Update an existing client's information
     * This method now handles authentication fields properly
     *
     * @param client The client with updated information
     * @return true if client was successfully updated, false otherwise
     */
    public boolean updateClient(Client client) {
        if (client == null) {
            System.out.println("✗ Service Error: Cannot update null client");
            return false;
        }

        if (client.getId() == null || client.getId() <= 0) {
            System.out.println("✗ Service Error: Client must have a valid ID for update");
            return false;
        }

        boolean success = clientRepository.updateClient(client);
        if (success) {
            System.out.println("✓ Service: Client successfully updated: " + client.getUsername());
        } else {
            System.out.println("✗ Service: Failed to update client");
        }
        return success;
    }

    /**
     * Delete a client from the database
     * This method works exactly as before - no changes needed
     *
     * @param id The ID of the client to delete
     * @return true if client was successfully deleted, false otherwise
     */
    public boolean deleteClient(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Client ID cannot be null");
            return false;
        }

        if (id <= 0) {
            System.out.println("✗ Service Error: Invalid client ID: " + id);
            return false;
        }

        boolean success = clientRepository.deleteClient(id);
        if (success) {
            System.out.println("✓ Service: Client successfully deleted");
        } else {
            System.out.println("✗ Service: Failed to delete client");
        }
        return success;
    }

    /**
     * Check if a client exists in the database
     * This method works exactly as before - no changes needed
     *
     * @param id The client ID to check
     * @return true if client exists, false otherwise
     */
    public boolean clientExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return getClientById(id).isPresent();
    }

    /**
     * Get the total number of clients
     * This is useful for dashboard statistics and pagination
     *
     * @return The total count of clients
     */
    public int getClientCount() {
        return clientRepository.getClientCount();
    }

    // ============================================================================
    // ACCOUNT MANAGEMENT METHODS - Handle account status and security
    // ============================================================================

    /**
     * Enable or disable a client account
     *
     * This allows administrators to activate or deactivate accounts.
     * Disabled accounts cannot log in but retain all their data.
     *
     * @param clientId The ID of the client
     * @param enabled true to enable, false to disable
     * @return true if status was updated successfully, false otherwise
     */
    public boolean setClientAccountStatus(Integer clientId, boolean enabled) {
        if (clientId == null || clientId <= 0) {
            System.out.println("✗ Service: Invalid client ID for status update");
            return false;
        }

        boolean success = clientRepository.updateAccountStatus(clientId, enabled);
        if (success) {
            String status = enabled ? "enabled" : "disabled";
            System.out.println("✓ Service: Client account " + status + " for ID: " + clientId);
        }
        return success;
    }

    /**
     * Lock or unlock a client account
     *
     * Account locking is a security feature for temporarily preventing access.
     * This might be used after failed login attempts or security concerns.
     *
     * @param clientId The ID of the client
     * @param locked true to lock, false to unlock
     * @return true if lock status was updated successfully, false otherwise
     */
    public boolean setClientAccountLockStatus(Integer clientId, boolean locked) {
        if (clientId == null || clientId <= 0) {
            System.out.println("✗ Service: Invalid client ID for lock status update");
            return false;
        }

        boolean success = clientRepository.updateAccountLockStatus(clientId, locked);
        if (success) {
            String status = locked ? "locked" : "unlocked";
            System.out.println("✓ Service: Client account " + status + " for ID: " + clientId);
        }
        return success;
    }

    /**
     * Get clients who registered recently
     *
     * This is useful for new user onboarding, welcome campaigns,
     * and understanding user growth patterns.
     *
     * @param days Number of days to look back
     * @return List of clients who registered within the specified days
     */
    public List<Client> getRecentClients(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return clientRepository.findClientsCreatedBetween(cutoffDate, LocalDateTime.now());
    }

    // ============================================================================
    // SEARCH METHODS - For the search functionality with search bar
    // ============================================================================


    public List<Client> findClientByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("✗ Service Error: Username cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(station -> station.getUsername().toLowerCase()
                        .contains(username.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Client> findClientByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("✗ Service Error: email cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(station -> station.getEmail().toLowerCase()
                        .contains(email.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Client> findClientByFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            System.out.println("✗ Service Error: first name cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(station -> station.getFirstName().toLowerCase()
                        .contains(firstName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Client> findClientByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            System.out.println("✗ Service Error: first name cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(station -> station.getLastName().toLowerCase()
                        .contains(lastName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Client> findClientByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: phone number cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(station -> station.getPhone().toLowerCase()
                        .contains(phoneNumber.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Client> findClientByAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            System.out.println("✗ Service Error: address cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(station -> station.getAddress().toLowerCase()
                        .contains(address.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Client> findClientByCreditCard(String creditCard) {
        if (creditCard == null || creditCard.trim().isEmpty()) {
            System.out.println("✗ Service Error: creditCard cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(station -> station.getCredit_card()
                        .contains(creditCard))
                .collect(Collectors.toList());
    }

    public List<Client> searchClients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        List<Client> allBusStations = getAllClients();

        return allBusStations.stream()
                .filter(client ->
                        client.getUsername().toLowerCase().contains(lowerSearchTerm) ||
                        client.getEmail().toLowerCase().contains(lowerSearchTerm) ||
                        client.getFullName().toLowerCase().contains(lowerSearchTerm)  ||
                        client.getFirstName().toLowerCase().contains(lowerSearchTerm)  ||
                        client.getLastName().toLowerCase().contains(lowerSearchTerm)  ||
                        client.getPhone().toLowerCase().contains(lowerSearchTerm)  ||
                        client.getAddress().toLowerCase().contains(lowerSearchTerm)  ||
                        client.getCredit_card().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    // ============================================================================
    // HELPER METHODS - Internal validation and utility functions
    // ============================================================================

    /**
     * Find a client by username or email
     *
     * This supports flexible login - users can log in with either
     * their username or email address.
     *
     * @param usernameOrEmail The login identifier
     * @return Optional containing the client if found
     */
    private Optional<Client> findClientByUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            return Optional.empty();
        }

        // First try to find by username
        Optional<Client> clientByUsername = clientRepository.findByUsername(usernameOrEmail);
        if (clientByUsername.isPresent()) {
            return clientByUsername;
        }

        // If not found by username, try by email
        return clientRepository.findByEmail(usernameOrEmail);
    }

    /**
     * Validate registration data
     *
     * This performs comprehensive validation of all registration fields
     * to ensure data quality and prevent common registration errors.
     *
     * @param request The registration request to validate
     * @return ValidationResult indicating success or specific error
     */
    private ValidationResult validateRegistrationData(RegistrationRequest request) {
        // Check required fields
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return new ValidationResult(false, "Username is required");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return new ValidationResult(false, "Email address is required");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return new ValidationResult(false, "Password is required");
        }

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            return new ValidationResult(false, "First name is required");
        }

        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            return new ValidationResult(false, "Last name is required");
        }

        // Validate username format and length
        if (request.getUsername().length() < 3 || request.getUsername().length() > 50) {
            return new ValidationResult(false, "Username must be between 3 and 50 characters");
        }

        // Check for special characters in username
        if (!request.getUsername().matches("^[a-zA-Z0-9_.-]+$")) {
            return new ValidationResult(false, "Username can only contain letters, numbers, underscore, period, and dash");
        }

        // Validate email format
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            return new ValidationResult(false, "Please provide a valid email address");
        }

        // Validate password strength
        if (request.getPassword().length() < 6) {
            return new ValidationResult(false, "Password must be at least 6 characters long");
        }

        // Check for common weak passwords
        String password = request.getPassword().toLowerCase();
        if (password.equals("password") || password.equals("123456") || password.equals("qwerty")) {
            return new ValidationResult(false, "Please choose a stronger password");
        }

        return new ValidationResult(true, "Valid");
    }

    // ============================================================================
    // DATA TRANSFER OBJECTS (DTOs) - Define request/response structures
    // ============================================================================

    /**
     * Registration request data structure
     * This defines what information is required for client registration
     */
    public static class RegistrationRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String phone;
        private String address;

        // Constructors
        public RegistrationRequest() {}

        public RegistrationRequest(String username, String email, String password,
                                   String firstName, String lastName) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    /**
     * Login request data structure
     * This defines what information is required for client login
     */
    public static class LoginRequest {
        private String usernameOrEmail;
        private String password;

        // Constructors
        public LoginRequest() {}

        public LoginRequest(String usernameOrEmail, String password) {
            this.usernameOrEmail = usernameOrEmail;
            this.password = password;
        }

        // Getters and setters
        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /**
     * Registration response data structure
     * This defines what information is sent back after registration attempt
     */
    public static class RegistrationResponse {
        private boolean success;
        private String message;
        private String username;

        public RegistrationResponse(boolean success, String message, String username) {
            this.success = success;
            this.message = message;
            this.username = username;
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    /**
     * Login response data structure
     * This defines what information is sent back after login attempt
     */
    public static class LoginResponse {
        private boolean success;
        private String message;
        private String token;        // JWT token for authenticated requests
        private Client client;       // Client information (without sensitive data)

        public LoginResponse(boolean success, String message, String token, Client client) {
            this.success = success;
            this.message = message;
            this.token = token;
            this.client = client;
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Client getClient() { return client; }
        public void setClient(Client client) { this.client = client; }
    }

    /**
     * Validation result helper class
     * Used internally to check if data is valid
     */
    private static class ValidationResult {
        private boolean valid;
        private String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }
}