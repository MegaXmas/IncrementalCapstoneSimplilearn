package com.example.travelbuddybackend.models;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Client model with authentication capabilities and comprehensive validation
 *
 * This represents a customer in our travel booking system who can:
 * - Create an account and log in
 * - Book travel tickets
 * - Manage their personal information
 *
 * Think of this as a complete customer profile that includes both
 * personal information (for bookings) and login credentials (for security).
 *
 * Note: Admins are handled separately through the AdminUser model.
 */
public class Client {

    // ============================================================================
    // CORE IDENTITY FIELDS
    // ============================================================================

    private Integer id;                    // Unique identifier for each client

    // Authentication credentials - these allow the client to log in
    @NotNull(message = "Username is required")
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username can only contain letters, numbers, underscore, period, and dash")
    private String username;               // Unique username for login

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;                  // Email address (also used for login)

    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")
    private String password;               // Encrypted password - never store plain text!

    // ============================================================================
    // PERSONAL INFORMATION FIELDS - Used for bookings and contact
    // ============================================================================

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, apostrophes, and hyphens")
    private String firstName;              // Client's first name

    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, apostrophes, and hyphens")
    private String lastName;               // Client's last name

    @NotNull(message = "Phone number is required")
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Phone number must be in valid international format (e.g., +1234567890)")
    private String phone;                  // Phone number for booking confirmations

    @NotNull(message = "Address is required")
    @NotBlank(message = "Address cannot be blank")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String address;                // Address for billing and contact

    @Pattern(regexp = "^\\d{13,19}$", message = "Credit card number must be 13-19 digits")
    private String credit_card;            // Credit card for payments (encrypted)

    // ============================================================================
    // ACCOUNT MANAGEMENT FIELDS - Control account access and track activity
    // ============================================================================

    private boolean enabled = true;        // Can the client log in? (account active/inactive)
    private boolean accountLocked = false; // Is account temporarily locked? (security measure)
    private LocalDateTime createdAt;       // When was this account created?
    private LocalDateTime lastLogin;       // When did client last successfully log in?

    // ============================================================================
    // CONSTRUCTORS
    // ============================================================================

    /**
     * Default constructor
     * Automatically sets the account creation timestamp to now
     */
    public Client() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor for creating a new client account
     * This is typically used during registration when all basic info is provided
     *
     * @param username Unique username for login
     * @param email Email address for login and communication
     * @param password Raw password (will be encrypted by the service layer)
     * @param firstName Client's first name
     * @param lastName Client's last name
     */
    public Client(String username, String email, String password, String firstName, String lastName) {
        this(); // Call default constructor to set createdAt
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Constructor for basic client information (useful for booking operations)
     * This might be used when creating a client record from booking information
     *
     * @param firstName Client's first name
     * @param lastName Client's last name
     * @param email Email address
     * @param phone Phone number
     */
    public Client(String firstName, String lastName, String email, String phone) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    // ============================================================================
    // GETTERS AND SETTERS
    // ============================================================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCredit_card() {
        return credit_card;
    }

    public void setCredit_card(String credit_card) {
        this.credit_card = credit_card;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    // ============================================================================
    // UTILITY METHODS
    // ============================================================================

    /**
     * Get the client's full name for display purposes
     * @return Combined first and last name
     */
    public String getName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if the account is currently usable (enabled and not locked)
     * @return true if client can log in and use the system
     */
    public boolean isAccountActive() {
        return enabled && !accountLocked;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", enabled=" + enabled +
                ", accountLocked=" + accountLocked +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                '}';
    }
}