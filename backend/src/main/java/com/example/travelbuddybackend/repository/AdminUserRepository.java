package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.AdminUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Admin Repository for Admin User Management and Authentication
 *
 * This repository handles all database operations for admin accounts.
 * It follows the same patterns and conventions as ClientRepository.
 *
 * Think of this as a secure filing cabinet that can:
 * - Store admin profiles with login credentials
 * - Find admins by username for login
 * - Track admin account activity and security status
 * - Manage all admin information needed for system administration
 *
 * The key principle here is that every method has a single, clear purpose
 * and the code is easy to understand and maintain.
 */
@Repository
public class AdminUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Row mapper to convert database results to AdminUser objects
     */
    private static class AdminRowMapper implements RowMapper<AdminUser> {
        @Override
        public AdminUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            AdminUser admin = new AdminUser();

            // Core identity fields
            admin.setId(rs.getInt("id"));
            admin.setAdminUsername(rs.getString("adminUsername"));
            admin.setAdminPassword(rs.getString("adminPassword"));

            // Account management fields
            admin.setEnabled(rs.getBoolean("enabled"));
            admin.setAccountLocked(rs.getBoolean("accountLocked"));

            // Handle timestamps (these might be null for some records)
            if (rs.getTimestamp("createdAt") != null) {
                admin.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            }
            if (rs.getTimestamp("lastLogin") != null) {
                admin.setLastLogin(rs.getTimestamp("lastLogin").toLocalDateTime());
            }

            return admin;
        }
    }

    // ============================================================================
    // CORE ADMIN MANAGEMENT METHODS
    // These handle basic admin operations that your system needs
    // ============================================================================

    /**
     * Find all admin users in the database
     *
     * This retrieves every admin account, which is useful for:
     * - Super admin dashboards showing all administrators
     * - Reports and analytics
     * - System maintenance operations
     *
     * @return List of all admin users (empty list if none found)
     */
    public List<AdminUser> findAll() {
        try {
            String sql = """
                SELECT id, adminUsername, adminPassword, enabled, accountLocked, 
                       createdAt, lastLogin 
                FROM admin_users 
                ORDER BY createdAt DESC
                """;

            List<AdminUser> admins = jdbcTemplate.query(sql, new AdminRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + admins.size() + " admin users");
            return admins;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving admin users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Find an admin user by their unique ID
     *
     * This is the most direct way to get admin information when you already
     * know their ID (for example, from a session or JWT token).
     *
     * @param id The admin's unique identifier
     * @return Optional containing the admin if found, empty if not found
     */
    public Optional<AdminUser> findById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid admin ID: " + id);
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT id, adminUsername, adminPassword, enabled, accountLocked, 
                       createdAt, lastLogin 
                FROM admin_users 
                WHERE id = ?
                """;

            List<AdminUser> admins = jdbcTemplate.query(sql, new AdminRowMapper(), id);

            if (admins.isEmpty()) {
                System.out.println("✗ Repository: Admin with ID " + id + " not found");
                return Optional.empty();
            }

            System.out.println("✓ Repository: Found admin: " + admins.get(0).getAdminUsername());
            return Optional.of(admins.get(0));

        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding admin by ID " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Create a new admin account
     *
     * This is called when adding a new administrator to the system.
     * All the essential information is provided and the account is created
     * with appropriate default settings (enabled, not locked).
     *
     * @param admin The admin object containing all necessary information
     * @return true if admin was successfully created, false otherwise
     */
    public boolean createAdmin(AdminUser admin) {
        if (admin == null) {
            System.out.println("✗ Repository: Cannot create null admin");
            return false;
        }

        // Validate required fields before attempting database insertion
        if (!isValidForCreation(admin)) {
            return false;
        }

        try {
            String sql = """
                INSERT INTO admin_users (adminUsername, adminPassword, enabled, accountLocked, createdAt) 
                VALUES (?, ?, ?, ?, ?)
                """;

            int rowsAffected = jdbcTemplate.update(sql,
                    admin.getAdminUsername(),
                    admin.getAdminPassword(), // This should already be encrypted by the service layer
                    admin.isEnabled(),
                    admin.isAccountLocked(),
                    admin.getCreatedAt()
            );

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: New admin created: " + admin.getAdminUsername());
                return true;
            } else {
                System.out.println("✗ Repository: No rows affected when creating admin");
                return false;
            }

        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating admin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing admin's information
     *
     * This allows updating admin account settings and information.
     * The admin ID must be valid and the admin must exist in the database.
     *
     * @param admin The admin object with updated information (must include valid ID)
     * @return true if admin was successfully updated, false otherwise
     */
    public boolean updateAdmin(AdminUser admin) {
        if (admin == null) {
            System.out.println("✗ Repository: Cannot update null admin");
            return false;
        }

        if (admin.getId() == null || admin.getId() <= 0) {
            System.out.println("✗ Repository: Admin must have valid ID for update");
            return false;
        }

        try {
            String sql = """
                UPDATE admin_users SET 
                    adminUsername = ?, enabled = ?, accountLocked = ?
                WHERE id = ?
                """;

            int rowsAffected = jdbcTemplate.update(sql,
                    admin.getAdminUsername(),
                    admin.isEnabled(),
                    admin.isAccountLocked(),
                    admin.getId()
            );

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Admin " + admin.getId() + " updated successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Admin with ID " + admin.getId() + " not found for update");
                return false;
            }

        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating admin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete an admin account
     *
     * This permanently removes an admin from the system. Use with extreme caution!
     * In many systems, you might prefer to disable accounts rather than delete them
     * to maintain data integrity for audit logs.
     *
     * @param id The ID of the admin to delete
     * @return true if admin was successfully deleted, false otherwise
     */
    public boolean deleteAdmin(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid admin ID for deletion: " + id);
            return false;
        }

        try {
            String sql = "DELETE FROM admin_users WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Admin " + id + " deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Admin with ID " + id + " not found for deletion");
                return false;
            }

        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting admin: " + e.getMessage());
            return false;
        }
    }

    // ============================================================================
    // AUTHENTICATION-SPECIFIC METHODS
    // These methods are specifically designed for admin login processes
    // ============================================================================

    /**
     * Find an admin by their username
     *
     * This is essential for login functionality. When an admin enters their username
     * at the login screen, this method finds their account so we can verify
     * their password and grant access to the admin system.
     *
     * @param adminUsername The admin username to search for (case-sensitive)
     * @return Optional containing the admin if found, empty if not found
     */
    public Optional<AdminUser> findByAdminUsername(String adminUsername) {
        if (adminUsername == null || adminUsername.trim().isEmpty()) {
            System.out.println("✗ Repository: Cannot search with empty admin username");
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT id, adminUsername, adminPassword, enabled, accountLocked, 
                       createdAt, lastLogin 
                FROM admin_users 
                WHERE adminUsername = ?
                """;

            List<AdminUser> admins = jdbcTemplate.query(sql, new AdminRowMapper(), adminUsername);

            if (admins.isEmpty()) {
                System.out.println("✗ Repository: No admin found with username: " + adminUsername);
                return Optional.empty();
            }

            System.out.println("✓ Repository: Found admin by username: " + adminUsername);
            return Optional.of(admins.get(0));

        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding admin by username: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Check if an admin username already exists in the system
     *
     * This is crucial when creating new admin accounts to ensure usernames are unique.
     * We check this before creating a new account to prevent conflicts
     * and provide clear error messages.
     *
     * @param adminUsername The admin username to check for availability
     * @return true if username already exists, false if available
     */
    public boolean existsByAdminUsername(String adminUsername) {
        if (adminUsername == null || adminUsername.trim().isEmpty()) {
            return false;
        }

        try {
            String sql = "SELECT COUNT(*) FROM admin_users WHERE adminUsername = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, adminUsername);

            boolean exists = count != null && count > 0;
            if (exists) {
                System.out.println("✓ Repository: Admin username '" + adminUsername + "' already exists");
            }
            return exists;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error checking admin username existence: " + e.getMessage());
            return false; // Assume it doesn't exist if we can't check
        }
    }

    /**
     * Update an admin's last login timestamp
     *
     * This is called whenever an admin successfully logs in. It helps track
     * admin activity, identify inactive accounts, and can be useful for
     * security monitoring and audit purposes.
     *
     * @param adminId The ID of the admin who just logged in
     * @return true if timestamp was successfully updated, false otherwise
     */
    public boolean updateLastLogin(Integer adminId) {
        if (adminId == null || adminId <= 0) {
            return false;
        }

        try {
            String sql = "UPDATE admin_users SET lastLogin = ? WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, LocalDateTime.now(), adminId);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Updated last login for admin ID: " + adminId);
                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating admin last login: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an admin's password
     *
     * This is used when admins change their passwords or when super administrators
     * reset passwords. The new password should already be encrypted by the
     * service layer before calling this method.
     *
     * @param adminId The ID of the admin whose password is being changed
     * @param newEncryptedPassword The new password (already encrypted)
     * @return true if password was successfully updated, false otherwise
     */
    public boolean updatePassword(Integer adminId, String newEncryptedPassword) {
        if (adminId == null || adminId <= 0 || newEncryptedPassword == null || newEncryptedPassword.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid parameters for admin password update");
            return false;
        }

        try {
            String sql = "UPDATE admin_users SET adminPassword = ? WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, newEncryptedPassword, adminId);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Password updated for admin ID: " + adminId);
                return true;
            } else {
                System.out.println("✗ Repository: Admin ID " + adminId + " not found for password update");
                return false;
            }

        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating admin password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an admin's account status (enable/disable)
     *
     * This allows super administrators to activate or deactivate admin accounts.
     * Disabled accounts cannot log in, which is useful for temporarily
     * suspending access without deleting the account.
     *
     * @param adminId The ID of the admin whose status is being changed
     * @param enabled true to enable the account, false to disable
     * @return true if status was successfully updated, false otherwise
     */
    public boolean updateAccountStatus(Integer adminId, boolean enabled) {
        if (adminId == null || adminId <= 0) {
            System.out.println("✗ Repository: Invalid admin ID for status update: " + adminId);
            return false;
        }

        try {
            String sql = "UPDATE admin_users SET enabled = ? WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, enabled, adminId);

            if (rowsAffected > 0) {
                String status = enabled ? "enabled" : "disabled";
                System.out.println("✓ Repository: Admin ID " + adminId + " " + status + " successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Admin ID " + adminId + " not found for status update");
                return false;
            }

        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating admin account status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an admin's account lock status
     *
     * This allows locking/unlocking admin accounts for security purposes.
     * Locked accounts cannot log in even if they are enabled.
     *
     * @param adminId The ID of the admin whose lock status is being changed
     * @param locked true to lock the account, false to unlock
     * @return true if lock status was successfully updated, false otherwise
     */
    public boolean updateAccountLockStatus(Integer adminId, boolean locked) {
        if (adminId == null || adminId <= 0) {
            System.out.println("✗ Repository: Invalid admin ID for lock status update: " + adminId);
            return false;
        }

        try {
            String sql = "UPDATE admin_users SET accountLocked = ? WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, locked, adminId);

            if (rowsAffected > 0) {
                String status = locked ? "locked" : "unlocked";
                System.out.println("✓ Repository: Admin ID " + adminId + " " + status + " successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Admin ID " + adminId + " not found for lock status update");
                return false;
            }

        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating admin lock status: " + e.getMessage());
            return false;
        }
    }

    // ============================================================================
    // UTILITY AND STATISTICS METHODS
    // ============================================================================

    /**
     * Get the total number of admin users in the system
     *
     * This is useful for dashboard statistics and system monitoring.
     *
     * @return The total count of admin accounts
     */
    public int getAdminCount() {
        try {
            String sql = "SELECT COUNT(*) FROM admin_users";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error getting admin count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get the number of active (enabled and unlocked) admin users
     *
     * This provides insight into how many admins can currently use the system.
     * It's useful for security monitoring and access control.
     *
     * @return The count of active admin accounts
     */
    public int getActiveAdminCount() {
        try {
            String sql = "SELECT COUNT(*) FROM admin_users WHERE enabled = true AND accountLocked = false";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error getting active admin count: " + e.getMessage());
            return 0;
        }
    }

    // ============================================================================
    // PRIVATE HELPER METHODS
    // ============================================================================

    /**
     * Validate that an admin object has all required fields for database creation
     *
     * This is called before attempting to insert a new admin record.
     * It checks that all essential fields are present and properly formatted.
     *
     * @param admin The admin object to validate
     * @return true if admin is valid for creation, false otherwise
     */
    private boolean isValidForCreation(AdminUser admin) {
        // Check username
        if (admin.getAdminUsername() == null || admin.getAdminUsername().trim().isEmpty()) {
            System.out.println("✗ Repository: Admin username is required for admin creation");
            return false;
        }

        // Check password
        if (admin.getAdminPassword() == null || admin.getAdminPassword().trim().isEmpty()) {
            System.out.println("✗ Repository: Admin password is required for admin creation");
            return false;
        }

        return true;
    }
}