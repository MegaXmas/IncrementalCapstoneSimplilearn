package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.BusDetails;
import com.example.travelbuddybackend.repository.BusDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusDetailsService {

    private final BusDetailsRepository busDetailsRepository;

    @Autowired
    public BusDetailsService(BusDetailsRepository busDetailsRepository) {
        this.busDetailsRepository = busDetailsRepository;
    }

    /**
     * Get all bus details from the database
     * @return List of all bus details (empty list if none found or error occurs)
     */
    public List<BusDetails> getAllBusDetails() {
        return busDetailsRepository.findAll();
    }

    /**
     * Get bus details by their ID
     * @param id The bus details ID to search for
     * @return Optional containing the bus details if found, empty otherwise
     */
    public Optional<BusDetails> getBusDetailsById(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Bus details ID cannot be null");
            return Optional.empty();
        }
        return busDetailsRepository.findById(id);
    }

    /**
     * Get bus details by bus number
     * @param busNumber The bus number to search for
     * @return Optional containing the bus details if found, empty otherwise
     */
    public Optional<BusDetails> getBusDetailsByNumber(String busNumber) {
        if (busNumber == null || busNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: Bus number cannot be null or empty");
            return Optional.empty();
        }
        return busDetailsRepository.findByBusNumber(busNumber);
    }

    /**
     * Add new bus details to the database
     * @param busDetails The bus details to add
     * @return true if bus details were successfully added, false otherwise
     */
    public boolean addBusDetails(BusDetails busDetails) {
        if (busDetails == null) {
            System.out.println("✗ Service Error: Cannot add null bus details");
            return false;
        }

        boolean success = busDetailsRepository.createBusDetails(busDetails);
        if (success) {
            System.out.println("✓ Service: Bus details successfully added through service layer");
        } else {
            System.out.println("✗ Service: Failed to add bus details through service layer");
        }
        return success;
    }

    /**
     * Update existing bus details in the database
     * @param busDetails The bus details with updated information
     * @return true if bus details were successfully updated, false otherwise
     */
    public boolean updateBusDetails(BusDetails busDetails) {
        if (busDetails == null) {
            System.out.println("✗ Service Error: Cannot update null bus details");
            return false;
        }

        if (busDetails.getId() == null || busDetails.getId() <= 0) {
            System.out.println("✗ Service Error: Bus details must have a valid ID for update");
            return false;
        }

        boolean success = busDetailsRepository.updateBusDetails(busDetails);
        if (success) {
            System.out.println("✓ Service: Bus details successfully updated through service layer");
        } else {
            System.out.println("✗ Service: Failed to update bus details through service layer");
        }
        return success;
    }

    /**
     * Delete bus details from the database
     * @param id The ID of the bus details to delete
     * @return true if bus details were successfully deleted, false otherwise
     */
    public boolean deleteBusDetails(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Bus details ID cannot be null");
            return false;
        }

        if (id <= 0) {
            System.out.println("✗ Service Error: Invalid bus details ID: " + id);
            return false;
        }

        boolean success = busDetailsRepository.deleteBusDetails(id);
        if (success) {
            System.out.println("✓ Service: Bus details successfully deleted through service layer");
        } else {
            System.out.println("✗ Service: Failed to delete bus details through service layer");
        }
        return success;
    }

    /**
     * Check if bus details exist in the database by ID
     * @param id The bus details ID to check
     * @return true if bus details exist, false otherwise
     */
    public boolean busDetailsExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return getBusDetailsById(id).isPresent();
    }

    /**
     * Check if bus details exist in the database by bus number
     * @param busNumber The bus number to check
     * @return true if bus details exist, false otherwise
     */
    public boolean busDetailsExistsByNumber(String busNumber) {
        if (busNumber == null || busNumber.trim().isEmpty()) {
            return false;
        }
        return getBusDetailsByNumber(busNumber).isPresent();
    }

    /**
     * Get the total number of bus details in the database
     * @return The count of all bus details
     */
    public int getBusDetailsCount() {
        List<BusDetails> busDetailsList = getAllBusDetails();
        return busDetailsList.size();
    }

    /**
     * Find bus details by departure and arrival stations
     * @param departureStation The departure station to search for
     * @param arrivalStation The arrival station to search for
     * @return List of bus details matching the route
     */
    public List<BusDetails> findBusDetailsByRoute(String departureStation, String arrivalStation) {
        if (departureStation == null || departureStation.trim().isEmpty() ||
                arrivalStation == null || arrivalStation.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both departure and arrival stations are required");
            return new ArrayList<>();
        }

        List<BusDetails> allBusDetails = getAllBusDetails();
        return allBusDetails.stream()
                .filter(bus -> bus.getBusDepartureStation().equalsIgnoreCase(departureStation) &&
                        bus.getBusArrivalStation().equalsIgnoreCase(arrivalStation))
                .collect(Collectors.toList());
    }

    /**
     * Find bus details by departure date
     * @param departureDate The departure date to search for (format should match database)
     * @return List of bus details departing on the specified date
     */
    public List<BusDetails> findBusDetailsByDate(String departureDate) {
        if (departureDate == null || departureDate.trim().isEmpty()) {
            System.out.println("✗ Service Error: Departure date cannot be null or empty");
            return new ArrayList<>();
        }

        List<BusDetails> allBusDetails = getAllBusDetails();
        return allBusDetails.stream()
                .filter(bus -> bus.getBusDepartureDate().equals(departureDate))
                .collect(Collectors.toList());
    }
}