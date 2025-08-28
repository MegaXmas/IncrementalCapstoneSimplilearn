package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.BusDetails;
import com.example.travelbuddybackend.models.BusStation;
import com.example.travelbuddybackend.repository.BusDetailsRepository;
import com.example.travelbuddybackend.repository.BusStationRepository;
import com.example.travelbuddybackend.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Bus Details Service - Business Logic Layer
 *
 * Fixed Architecture:
 * - Only depends on repositories (no service-to-service dependencies)
 * - Handles business validation and logic
 * - Coordinates repository operations
 * - No circular dependencies
 */
@Service
public class BusDetailsService {

    private final BusDetailsRepository busDetailsRepository;
    private final BusStationRepository busStationRepository;
    private final ValidatorService validatorService;

    @Autowired
    public BusDetailsService(BusDetailsRepository busDetailsRepository,
                             BusStationRepository busStationRepository,
                             ValidatorService validatorService) {
        this.busDetailsRepository = busDetailsRepository;
        this.busStationRepository = busStationRepository;
        this.validatorService = validatorService;
    }

    // ============================================================================
    // CORE BUSINESS OPERATIONS
    // ============================================================================

    public List<BusDetails> getAllBusDetails() {
        return busDetailsRepository.findAll();
    }

    public Optional<BusDetails> getBusDetailsById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Service Error: Bus ID must be a positive integer");
            return Optional.empty();
        }
        return busDetailsRepository.findById(id);
    }

    public Optional<BusDetails> getBusDetailsByNumber(String busNumber) {
        if (busNumber == null || busNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: Bus number cannot be null or empty");
            return Optional.empty();
        }
        return busDetailsRepository.findByBusNumber(busNumber.toUpperCase().trim());
    }

    public List<BusDetails> getBusesByRoute(String departureStationCode, String arrivalStationCode) {
        if (departureStationCode == null || departureStationCode.trim().isEmpty() ||
                arrivalStationCode == null || arrivalStationCode.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both departure and arrival station codes are required");
            return new ArrayList<>();
        }

        Optional<BusStation> departureStation = busStationRepository.findByStationCode(departureStationCode.toUpperCase().trim());
        Optional<BusStation> arrivalStation = busStationRepository.findByStationCode(arrivalStationCode.toUpperCase().trim());

        if (departureStation.isEmpty()) {
            System.out.println("✗ Service Error: Departure station not found: " + departureStationCode);
            return new ArrayList<>();
        }

        if (arrivalStation.isEmpty()) {
            System.out.println("✗ Service Error: Arrival station not found: " + arrivalStationCode);
            return new ArrayList<>();
        }

        return busDetailsRepository.findByRouteStationCodes(departureStation.get().getBusStationCode(), arrivalStation.get().getBusStationCode());
    }

    public List<BusDetails> getBusesByRoute(BusStation departureStation, BusStation arrivalStation) {
        if (departureStation == null || arrivalStation == null) {
            System.out.println("✗ Service Error: Both stations are required");
            return new ArrayList<>();
        }

        if (departureStation.getBusStationCode() == null || arrivalStation.getBusStationCode() == null) {
            System.out.println("✗ Service Error: Station codes are required");
            return new ArrayList<>();
        }

        return busDetailsRepository.findByRouteStationCodes(departureStation.getBusStationCode(), arrivalStation.getBusStationCode());
    }

    public List<BusDetails> getBusesByDepartureDate(String departureDate) {
        if (departureDate == null || departureDate.trim().isEmpty()) {
            System.out.println("✗ Service Error: Departure date cannot be null or empty");
            return new ArrayList<>();
        }

        if (!validatorService.isValidDate(departureDate)) {
            System.out.println("✗ Service Error: Invalid date format. Use YYYY-MM-DD");
            return new ArrayList<>();
        }

        return busDetailsRepository.findByDepartureDate(departureDate);
    }

    public boolean addBusDetails(BusDetails busDetails) {
        if (!isValidForService(busDetails)) {
            return false;
        }

        boolean success = busDetailsRepository.createBusDetails(busDetails);
        if (success) {
            System.out.println("✓ Service: Bus added successfully");
        } else {
            System.out.println("✗ Service: Failed to add bus");
        }
        return success;
    }

    public boolean updateBusDetails(BusDetails busDetails) {
        if (!isValidForService(busDetails)) {
            return false;
        }

        if (busDetails.getId() == null || busDetails.getId() <= 0) {
            System.out.println("✗ Service Error: Valid id required for update");
            return false;
        }

        // Check if bus exists
        if (busDetailsRepository.findById(busDetails.getId()).isEmpty()) {
            System.out.println("✗ Service Error: Bus details not found for update");
            return false;
        }

        boolean success = busDetailsRepository.updateBusDetails(busDetails);
        if (success) {
            System.out.println("✓ Service: Bus updated successfully");
        } else {
            System.out.println("✗ Service: Failed to update bus");
        }
        return success;
    }

    public boolean deleteBusDetails(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Service Error: Valid bus ID required for deletion");
            return false;
        }

        // Check if bus exists before deletion
        if (busDetailsRepository.findById(id).isEmpty()) {
            System.out.println("✗ Service Error: Bus not found for deletion");
            return false;
        }

        boolean success = busDetailsRepository.deleteBusDetails(id);
        if (success) {
            System.out.println("✓ Service: Bus deleted successfully");
        } else {
            System.out.println("✗ Service: Failed to delete bus");
        }
        return success;
    }

    // ============================================================================
    // BUSINESS LOGIC METHODS
    // ============================================================================

    public boolean busExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return busDetailsRepository.findById(id).isPresent();
    }

    public boolean busExistsByNumber(String busNumber) {
        if (busNumber == null || busNumber.trim().isEmpty()) {
            return false;
        }
        return busDetailsRepository.findByBusNumber(busNumber.toUpperCase().trim()).isPresent();
    }

    public int getBusDetailsCount() {
        return busDetailsRepository.findAll().size();
    }

    // ============================================================================
    // SEARCH AND FILTER METHODS
    // ============================================================================

    public List<BusDetails> findBusesByLine(String busLine) {
        if (busLine == null || busLine.trim().isEmpty()) {
            System.out.println("✗ Service Error: Bus line cannot be null or empty");
            return new ArrayList<>();
        }

        return getAllBusDetails().stream()
                .filter(bus -> bus.getBusLine().toLowerCase().contains(busLine.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    public List<BusDetails> findBusesByPriceRange(String minPrice, String maxPrice) {
        if (minPrice == null || minPrice.trim().isEmpty() ||
                maxPrice == null || maxPrice.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both minimum and maximum prices are required");
            return new ArrayList<>();
        }

        try {
            double min = Double.parseDouble(minPrice.trim());
            double max = Double.parseDouble(maxPrice.trim());

            if (min < 0 || max < 0 || min > max) {
                System.out.println("✗ Service Error: Invalid price range");
                return new ArrayList<>();
            }

            return getAllBusDetails().stream()
                    .filter(bus -> {
                        try {
                            double price = Double.parseDouble(bus.getBusRidePrice());
                            return price >= min && price <= max;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            System.out.println("✗ Service Error: Invalid price format");
            return new ArrayList<>();
        }
    }

    public List<BusDetails> searchBuses(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        return getAllBusDetails().stream()
                .filter(bus -> {
                    String depName = bus.getBusDepartureStation() != null ?
                            bus.getBusDepartureStation().getBusStationFullName().toLowerCase() : "";
                    String depCode = bus.getBusDepartureStation() != null ?
                            bus.getBusDepartureStation().getBusStationCode().toLowerCase() : "";
                    String arrName = bus.getBusArrivalStation() != null ?
                            bus.getBusArrivalStation().getBusStationFullName().toLowerCase() : "";
                    String arrCode = bus.getBusArrivalStation() != null ?
                            bus.getBusArrivalStation().getBusStationCode().toLowerCase() : "";

                    return bus.getBusNumber().toLowerCase().contains(lowerSearchTerm) ||
                            bus.getBusLine().toLowerCase().contains(lowerSearchTerm) ||
                            depName.contains(lowerSearchTerm) ||
                            depCode.contains(lowerSearchTerm) ||
                            arrName.contains(lowerSearchTerm) ||
                            arrCode.contains(lowerSearchTerm) ||
                            bus.getBusDepartureDate().contains(lowerSearchTerm) ||
                            bus.getBusRidePrice().contains(lowerSearchTerm);
                })
                .collect(Collectors.toList());
    }

    // ============================================================================
    // BUSINESS VALIDATION
    // ============================================================================

    private boolean isValidForService(BusDetails busDetails) {
        if (busDetails == null) {
            System.out.println("✗ Service Error: Bus details cannot be null");
            return false;
        }

        // Basic field validation
        if (busDetails.getBusNumber() == null || busDetails.getBusNumber().trim().isEmpty()) {
            System.out.println("✗ Service Error: Bus number is required");
            return false;
        }

        if (busDetails.getBusLine() == null || busDetails.getBusLine().trim().isEmpty()) {
            System.out.println("✗ Service Error: Bus line is required");
            return false;
        }

        if (busDetails.getBusDepartureStation() == null || busDetails.getBusArrivalStation() == null) {
            System.out.println("✗ Service Error: Both departure and arrival stations are required");
            return false;
        }

        // Business logic validation
        if (busDetails.getBusDepartureStation().getId().equals(busDetails.getBusArrivalStation().getId())) {
            System.out.println("✗ Service Error: Departure and arrival stations cannot be the same");
            return false;
        }

        // Validate stations exist in database
        if (busStationRepository.findById(busDetails.getBusDepartureStation().getId()).isEmpty()) {
            System.out.println("✗ Service Error: Departure station not found in database");
            return false;
        }

        if (busStationRepository.findById(busDetails.getBusArrivalStation().getId()).isEmpty()) {
            System.out.println("✗ Service Error: Arrival station not found in database");
            return false;
        }

        // Date and time validation
        if (!validatorService.isValidDate(busDetails.getBusDepartureDate()) ||
                !validatorService.isValidDate(busDetails.getBusArrivalDate())) {
            System.out.println("✗ Service Error: Invalid date format. Use YYYY-MM-DD");
            return false;
        }

        if (!validatorService.isValidTime(busDetails.getBusDepartureTime()) ||
                !validatorService.isValidTime(busDetails.getBusArrivalTime())) {
            System.out.println("✗ Service Error: Invalid time format. Use HH:MM");
            return false;
        }

        // Price validation
        if (busDetails.getBusRidePrice() == null || busDetails.getBusRidePrice().trim().isEmpty()) {
            System.out.println("✗ Service Error: Bus price is required");
            return false;
        }

        try {
            double price = Double.parseDouble(busDetails.getBusRidePrice());
            if (price <= 0) {
                System.out.println("✗ Service Error: Bus price must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Service Error: Bus price must be a valid number");
            return false;
        }

        return true;
    }
}