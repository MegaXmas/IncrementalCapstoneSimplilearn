package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.Client;
import com.example.travelbuddybackend.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Get all clients from the database
     * @return List of all clients (empty list if none found or error occurs)
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Get a client by their ID
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
     * @param client The client to add
     * @return true if client was successfully added, false otherwise
     */
    public boolean addClient(Client client) {
        if (client == null) {
            System.out.println("✗ Service Error: Cannot add null client");
            return false;
        }

        boolean success = clientRepository.newClient(client);
        if (success) {
            System.out.println("✓ Service: Client successfully added through service layer");
        } else {
            System.out.println("✗ Service: Failed to add client through service layer");
        }
        return success;
    }

    /**
     * Update an existing client in the database
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
            System.out.println("✓ Service: Client successfully updated through service layer");
        } else {
            System.out.println("✗ Service: Failed to update client through service layer");
        }
        return success;
    }

    /**
     * Delete a client from the database
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
            System.out.println("✓ Service: Client successfully deleted through service layer");
        } else {
            System.out.println("✗ Service: Failed to delete client through service layer");
        }
        return success;
    }

    /**
     * Check if a client exists in the database
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
     * Get the total number of clients in the database
     * @return The count of all clients
     */
    public int getClientCount() {
        List<Client> clients = getAllClients();
        return clients.size();
    }

    /**
     * Find clients by email address
     * @param email The email address to search for
     * @return List of clients with matching email (should typically be 1 or 0)
     */
    public List<Client> findClientsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("✗ Service Error: Email cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(client -> client.getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }

    /**
     * Find clients by partial name match (useful for search functionality)
     * @param partialName The partial name to search for
     * @return List of clients whose names contain the partial name
     */
    public List<Client> findClientsByPartialName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            System.out.println("✗ Service Error: Partial name cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(client -> client.getName().toLowerCase()
                        .contains(partialName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Find clients by phone number
     * @param phone The phone number to search for
     * @return List of clients with matching phone number
     */
    public List<Client> findClientsByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            System.out.println("✗ Service Error: Phone number cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(client -> client.getPhone() != null &&
                        client.getPhone().equals(phone))
                .collect(Collectors.toList());
    }

    public List<Client> findClientsByCreditCard(String credit_card) {
        if (credit_card == null || credit_card.trim().isEmpty()) {
            System.out.println("✗ Service Error: Credit card number cannot be null or empty");
            return new ArrayList<>();
        }

        List<Client> allClients = getAllClients();
        return allClients.stream()
                .filter(client -> client.getCredit_card() != null &&
                        client.getCredit_card().equals(credit_card))
                .collect(Collectors.toList());
    }

    /**
     * Check if email is already registered
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return !findClientsByEmail(email).isEmpty();
    }

    /**
     * Validate client data before creating/updating
     * @param client The client to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidClient(Client client) {
        if (client == null) {
            System.out.println("✗ Service Error: Client cannot be null");
            return false;
        }

        if (client.getName() == null || client.getName().trim().isEmpty()) {
            System.out.println("✗ Service Error: Client name is required");
            return false;
        }

        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            System.out.println("✗ Service Error: Client email is required");
            return false;
        }

        // Basic email format validation
        if (!client.getEmail().contains("@") || !client.getEmail().contains(".")) {
            System.out.println("✗ Service Error: Invalid email format");
            return false;
        }

        return true;
    }
}