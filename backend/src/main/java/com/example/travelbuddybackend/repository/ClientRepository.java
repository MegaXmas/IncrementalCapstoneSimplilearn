package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.Client;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ClientRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private static class ClientRowMapper implements RowMapper<Client> {
        @Override
        public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
            Client client = new Client();

            client.setId(rs.getInt("id"));
            client.setUsername(rs.getString("username"));
            client.setEmail(rs.getString("email"));
            client.setPassword(rs.getString("password"));

            client.setFirstName(rs.getString("first_name"));
            client.setLastName(rs.getString("last_name"));
            client.setPhone(rs.getString("phone"));
            client.setAddress(rs.getString("address"));
            client.setCredit_card(rs.getString("credit_card"));

            client.setEnabled(rs.getBoolean("enabled"));
            client.setAccountLocked(rs.getBoolean("account_locked"));

            if (rs.getTimestamp("created_at") != null) {
                client.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("last_login") != null) {
                client.setLastLogin(rs.getTimestamp("last_login").toLocalDateTime());
            }

            return client;
        }
    }



    // ============================================================================
    // CORE CLIENT MANAGEMENT METHODS
    // ============================================================================

    /**
     * Find all clients in the database
     * @return List of all clients (empty list if none found)
     */
    public List<Client> findAll() {
        try {
            String sql = """
                SELECT id, username, email, password, first_name, last_name, 
                       phone, address, credit_card, enabled, account_locked, 
                       created_at, last_login 
                FROM clients 
                ORDER BY created_at DESC
                """;

            List<Client> clients = jdbcTemplate.query(sql, new ClientRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + clients.size() + " clients");
            return clients;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving clients: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Find a client by their unique ID
     * @param id The client's unique identifier
     * @return Optional containing the client if found, empty if not found
     */
    public Optional<Client> findById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid client ID: " + id);
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT id, username, email, password, first_name, last_name, 
                       phone, address, credit_card, enabled, account_locked, 
                       created_at, last_login 
                FROM clients 
                WHERE id = ?
                """;

            List<Client> clients = jdbcTemplate.query(sql, new ClientRowMapper(), id);

            if (clients.isEmpty()) {
                System.out.println("✗ Repository: Client with ID " + id + " not found");
                return Optional.empty();
            }

            System.out.println("✓ Repository: Found client: " + clients.get(0).getUsername());
            return Optional.of(clients.get(0));

        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding client by ID " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Create a new client account
     * @param client The client object containing all necessary information
     * @return true if client was successfully created, false otherwise
     */
    public boolean createClient(Client client) {
        if (client == null) {
            System.out.println("✗ Repository: Cannot create null client");
            return false;
        }

        // Validate required fields before attempting database insertion
        if (!isValidForCreation(client)) {
            return false;
        }

        try {
            String sql = """
                INSERT INTO clients (username, email, password, first_name, last_name, 
                                   phone, address, credit_card, enabled, account_locked, created_at) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            int rowsAffected = jdbcTemplate.update(sql,
                    client.getUsername(),
                    client.getEmail(),
                    client.getPassword(), // This should already be encrypted by the service layer
                    client.getFirstName(),
                    client.getLastName(),
                    client.getPhone(),
                    client.getAddress(),
                    client.getCredit_card(),
                    client.isEnabled(),
                    client.isAccountLocked(),
                    client.getCreatedAt()
            );

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: New client created: " + client.getUsername() + " (" + client.getEmail() + ")");
                return true;
            } else {
                System.out.println("✗ Repository: No rows affected when creating client");
                return false;
            }

        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating client: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing client's information
     * @param client The client object with updated information (must include valid ID)
     * @return true if client was successfully updated, false otherwise
     */
    public boolean updateClient(Client client) {
        if (client == null) {
            System.out.println("✗ Repository: Cannot update null client");
            return false;
        }

        if (client.getId() == null || client.getId() <= 0) {
            System.out.println("✗ Repository: Client must have valid ID for update");
            return false;
        }

        try {
            String sql = """
                UPDATE clients SET 
                    username = ?, email = ?, first_name = ?, last_name = ?, 
                    phone = ?, address = ?, credit_card = ?, enabled = ?, account_locked = ?
                WHERE id = ?
                """;

            int rowsAffected = jdbcTemplate.update(sql,
                    client.getUsername(),
                    client.getEmail(),
                    client.getFirstName(),
                    client.getLastName(),
                    client.getPhone(),
                    client.getAddress(),
                    client.getCredit_card(),
                    client.isEnabled(),
                    client.isAccountLocked(),
                    client.getId()
            );

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Client " + client.getId() + " updated successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Client with ID " + client.getId() + " not found for update");
                return false;
            }

        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating client: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a client account
     * @param id The ID of the client to delete
     * @return true if client was successfully deleted, false otherwise
     */
    public boolean deleteClient(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid client ID for deletion: " + id);
            return false;
        }

        try {
            String sql = "DELETE FROM clients WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Client " + id + " deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Client with ID " + id + " not found for deletion");
                return false;
            }

        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting client: " + e.getMessage());
            return false;
        }
    }

    // ============================================================================
    // AUTHENTICATION-SPECIFIC METHODS
    // These methods are specifically designed for login and registration processes
    // ============================================================================

    /**
     * Find a client by their usernamE
     * This is essential for login functionality.
     * @param username The username to search for (case-sensitive)
     * @return Optional containing the client if found, empty if not found
     */
    public Optional<Client> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("✗ Repository: Cannot search with empty username");
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT id, username, email, password, first_name, last_name, 
                       phone, address, credit_card, enabled, account_locked, 
                       created_at, last_login 
                FROM clients 
                WHERE username = ?
                """;

            List<Client> clients = jdbcTemplate.query(sql, new ClientRowMapper(), username);

            if (clients.isEmpty()) {
                System.out.println("✗ Repository: No client found with username: " + username);
                return Optional.empty();
            }

            System.out.println("✓ Repository: Found client by username: " + username);
            return Optional.of(clients.get(0));

        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding client by username: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find a client by their email address
     * @param email The email address to search for (case-insensitive)
     * @return Optional containing the client if found, empty if not found
     */
    public Optional<Client> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("✗ Repository: Cannot search with empty email");
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT id, username, email, password, first_name, last_name, 
                       phone, address, credit_card, enabled, account_locked, 
                       created_at, last_login 
                FROM clients 
                WHERE LOWER(email) = LOWER(?)
                """;

            List<Client> clients = jdbcTemplate.query(sql, new ClientRowMapper(), email);

            if (clients.isEmpty()) {
                return Optional.empty(); // Don't log this as it's often used for existence checks
            }

            System.out.println("✓ Repository: Found client by email: " + email);
            return Optional.of(clients.get(0));

        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding client by email: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Check if a username already exists in the system
     * @param username The username to check for availability
     * @return true if username already exists, false if available
     */
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        try {
            String sql = "SELECT COUNT(*) FROM clients WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);

            boolean exists = count != null && count > 0;
            if (exists) {
                System.out.println("✓ Repository: Username '" + username + "' already exists");
            }
            return exists;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error checking username existence: " + e.getMessage());
            return false; // Assume it doesn't exist if we can't check
        }
    }

    /**
     * Check if an email address already exists in the system
     * @param email The email address to check for availability
     * @return true if email already exists, false if available
     */
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        try {
            String sql = "SELECT COUNT(*) FROM clients WHERE LOWER(email) = LOWER(?)";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

            boolean exists = count != null && count > 0;
            if (exists) {
                System.out.println("✓ Repository: Email '" + email + "' already exists");
            }
            return exists;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error checking email existence: " + e.getMessage());
            return false; // Assume it doesn't exist if we can't check
        }
    }

    /**
     * Update a client's last login timestamp
     * @param clientId The ID of the client who just logged in
     * @return true if timestamp was successfully updated, false otherwise
     */
    public boolean updateLastLogin(Integer clientId) {
        if (clientId == null || clientId <= 0) {
            return false;
        }

        try {
            String sql = "UPDATE clients SET last_login = ? WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, LocalDateTime.now(), clientId);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Updated last login for client ID: " + clientId);
                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating last login: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update a client's password
     * @param clientId The ID of the client whose password is being changed
     * @param newEncryptedPassword The new password (already encrypted)
     * @return true if password was successfully updated, false otherwise
     */
    public boolean updatePassword(Integer clientId, String newEncryptedPassword) {
        if (clientId == null || clientId <= 0 || newEncryptedPassword == null || newEncryptedPassword.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid parameters for password update");
            return false;
        }

        try {
            String sql = "UPDATE clients SET password = ? WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, newEncryptedPassword, clientId);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Password updated for client ID: " + clientId);
                return true;
            } else {
                System.out.println("✗ Repository: Client ID " + clientId + " not found for password update");
                return false;
            }

        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update a client's account status (enable/disable)
     * @param clientId The ID of the client whose status is being changed
     * @param enabled true to enable the account, false to disable
     * @return true if status was successfully updated, false otherwise
     */
    public boolean updateAccountStatus(Integer clientId, boolean enabled) {
        if (clientId == null || clientId <= 0) {
            return false;
        }

        try {
            String sql = "UPDATE clients SET enabled = ? WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, enabled, clientId);

            if (rowsAffected > 0) {
                String status = enabled ? "enabled" : "disabled";
                System.out.println("✓ Repository: Client ID " + clientId + " account " + status);
                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating account status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lock or unlock a client account
     * @param clientId The ID of the client whose lock status is being changed
     * @param locked true to lock the account, false to unlock
     * @return true if lock status was successfully updated, false otherwise
     */
    public boolean updateAccountLockStatus(Integer clientId, boolean locked) {
        if (clientId == null || clientId <= 0) {
            return false;
        }

        try {
            String sql = "UPDATE clients SET account_locked = ? WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, locked, clientId);

            if (rowsAffected > 0) {
                String status = locked ? "locked" : "unlocked";
                System.out.println("✓ Repository: Client ID " + clientId + " account " + status);
                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating account lock status: " + e.getMessage());
            return false;
        }
    }

    // ============================================================================
    // UTILITY AND VALIDATION METHODS
    // These help ensure data integrity and provide helpful functionality
    // ============================================================================

    /**
     * Validate that a client object has all required fields for creation
     * @param client The client object to validate
     * @return true if client is valid for creation, false otherwise
     */
    private boolean isValidForCreation(Client client) {
        // Check username
        if (client.getUsername() == null || client.getUsername().trim().isEmpty()) {
            System.out.println("✗ Repository: Username is required for client creation");
            return false;
        }

        // Check email
        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            System.out.println("✗ Repository: Email is required for client creation");
            return false;
        }

        // Check password
        if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
            System.out.println("✗ Repository: Password is required for client creation");
            return false;
        }

        // Check first name
        if (client.getFirstName() == null || client.getFirstName().trim().isEmpty()) {
            System.out.println("✗ Repository: First name is required for client creation");
            return false;
        }

        // Check last name
        if (client.getLastName() == null || client.getLastName().trim().isEmpty()) {
            System.out.println("✗ Repository: Last name is required for client creation");
            return false;
        }

        return true;
    }

    /**
     * Get the total number of clients in the system
     * @return The total count of client accounts
     */
    public int getClientCount() {
        try {
            String sql = "SELECT COUNT(*) FROM clients";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error getting client count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Find clients created within a specific time period
     * @param startDate The beginning of the time period
     * @param endDate The end of the time period
     * @return List of clients created within the specified period
     */
    public List<Client> findClientsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            System.out.println("✗ Repository: Start and end dates are required");
            return new ArrayList<>();
        }

        try {
            String sql = """
                SELECT id, username, email, password, first_name, last_name, 
                       phone, address, credit_card, enabled, account_locked, 
                       created_at, last_login 
                FROM clients 
                WHERE created_at BETWEEN ? AND ? 
                ORDER BY created_at DESC
                """;

            List<Client> clients = jdbcTemplate.query(sql, new ClientRowMapper(), startDate, endDate);
            System.out.println("✓ Repository: Found " + clients.size() + " clients created between " + startDate + " and " + endDate);
            return clients;

        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding clients by creation date: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}