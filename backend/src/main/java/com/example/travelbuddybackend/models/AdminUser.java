package com.example.travelbuddybackend.models;

import java.time.LocalDateTime;

public class AdminUser {

    private Integer id;
    private String adminUsername;
    private String adminPassword;

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
    public AdminUser() {
        this.createdAt = LocalDateTime.now();
    }

    public AdminUser(Integer id, String adminUsername, String adminPassword) {
        this();
        this.id = id;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() { return adminPassword; }

    public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }

    public boolean isEnabled() { return enabled; }

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

    /**
     * Check if this admin can log in to their account
     *
     * For an admin to log in successfully, their account must be:
     * - Enabled (not deactivated)
     * - Not locked (not temporarily suspended)
     *
     * @return true if client can log in, false otherwise
     */
    public boolean canLogin() {
        return enabled && !accountLocked;
    }

    @Override
    public String toString() {
        return "adminUserModel{" +
                "id=" + id +
                ", adminUsername='" + adminUsername + '\'' +
                ", adminPassword='" + adminPassword + '\'' +
                '}';
    }
}
