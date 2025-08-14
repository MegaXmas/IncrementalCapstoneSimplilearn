package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.AdminUser;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AdminUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class AdminUserRowMapper implements RowMapper<AdminUser> {
        @Override
        public AdminUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            AdminUser adminUser = new AdminUser();
            adminUser.setId(rs.getInt("id"));
            adminUser.setAdminUsername(rs.getString("adminUsername"));
            adminUser.setAdminPassword(rs.getString("adminPassword"));
            return adminUser;
        }
    }

    public List<AdminUser> findAll() {
        try {
            List<AdminUser> adminUsers = jdbcTemplate.query(
                    "SELECT id, adminUsername, adminPassword FROM admin_users",
                    new AdminUserRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + adminUsers.size() + " admin users");
            return adminUsers;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving admin users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<AdminUser> findById(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid client ID: " + id);
            return Optional.empty();
        }

        try {
            List<AdminUser> adminUsers = jdbcTemplate.query(
                    "SELECT id, adminUsername, adminPassword FROM admin_users WHERE id = ?",
                    new AdminUserRowMapper(), id);

            if (adminUsers.isEmpty()) {
                System.out.println("✗ Repository: Admin user with ID " + id + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found admin user: " + adminUsers.get(0).getAdminUsername());
                return Optional.of(adminUsers.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding admin user with ID: " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean newAdminUser(AdminUser adminUser) {
        if (adminUser == null) {
            System.out.println("✗ Repository: Error: Cannot create null admin user");
            return false;
        }

        if (adminUser.getAdminUsername() == null || adminUser.getAdminUsername().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Admin username is required");
            return false;
        }

        if (adminUser.getAdminPassword() == null || adminUser.getAdminPassword().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Admin password is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO admin_users (adminUsername, adminPassword) VALUES (?, ?)",
                    adminUser.getAdminUsername(), adminUser.getAdminPassword());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: New admin user created: " + adminUser.getAdminUsername() + " (" + adminUser.getAdminPassword() + ")");
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create new admin user");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating new admin user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateClient(AdminUser adminUser) {
        if (adminUser == null) {
            System.out.println("✗ Repository: Error: Cannot update null admin user");
            return false;
        }

        if (adminUser.getId() <= 0) {
            System.out.println("✗ Repository: Error: Invalid admin user ID " + adminUser.getId());
            return false;
        }

        if (adminUser.getAdminUsername() == null || adminUser.getAdminUsername().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Admin username is required");
            return false;
        }

        if (adminUser.getAdminPassword() == null || adminUser.getAdminPassword().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Admin user password is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO admin_users (adminUsername, adminPassword) WHERE id = ?",
                    adminUser.getAdminUsername(), adminUser.getAdminPassword());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Admin user " + adminUser.getAdminUsername() + " (" + adminUser.getAdminUsername() + ") updated successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Admin user with ID " + adminUser.getId() + " not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating admin user: " + e.getMessage());
            return false;
        }
    }

    //check this one to see if the logic is working properly. make sure all fields need to be filled out correctly and match what is on file in the database
    public boolean deleteAdminUser(AdminUser adminUser) {
        if (adminUser == null) {
                System.out.println("✗ Repository: Error: Cannot delete null admin user: " + adminUser);
            return false;
        }

        if (adminUser.getId() == null || adminUser.getId() <= 0) {
            System.out.println("✗ Repository: Error: Invalid admin user ID: " + adminUser.getId());
            return false;
        }

        if (adminUser.getAdminUsername() == null || adminUser.getAdminUsername().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Admin username is required");
            return false;
        }

        if (adminUser.getAdminPassword() == null || adminUser.getAdminPassword().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Admin user password is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM admin_users (id, adminUsername, adminPassword) VALUES (?, ?, ?)",
                    adminUser.getId(), adminUser.getAdminUsername(), adminUser.getAdminPassword());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Admin user " + adminUser + " deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Admin user: " + adminUser + " not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting client: " + e.getMessage());
            return false;
        }
    }
}
