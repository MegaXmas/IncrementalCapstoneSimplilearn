package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.Airport;
import com.example.travelbuddybackend.models.FlightDetails;
import com.example.travelbuddybackend.repository.FlightDetailsRepository;
import com.example.travelbuddybackend.repository.AirportRepository;
import com.example.travelbuddybackend.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Flight Details Service - Business Logic Layer
 *
 * Following Spring Boot Best Practices:
 * - Only depends on repositories (never other services for core operations)
 * - Handles all business validation and logic
 * - Coordinates multiple repository operations
 * - No circular dependencies
 */
@Service
public class FlightDetailsService {

    private final FlightDetailsRepository flightDetailsRepository;
    private final AirportRepository airportRepository;
    private final ValidatorService validatorService;

    @Autowired
    public FlightDetailsService(FlightDetailsRepository flightDetailsRepository,
                                AirportRepository airportRepository,
                                ValidatorService validatorService) {
        this.flightDetailsRepository = flightDetailsRepository;
        this.airportRepository = airportRepository;
        this.validatorService = validatorService;
    }

    // ============================================================================
    // CORE BUSINESS OPERATIONS
    // ============================================================================

    public List<FlightDetails> getAllFlightDetails() {
        return flightDetailsRepository.findAll();
    }

    public Optional<FlightDetails> getFlightDetailsById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Service Error: Flight ID must be a positive integer");
            return Optional.empty();
        }
        return flightDetailsRepository.findById(id);
    }

    public Optional<FlightDetails> getFlightDetailsByNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: Flight number cannot be null or empty");
            return Optional.empty();
        }
        return flightDetailsRepository.findByFlightNumber(flightNumber.toUpperCase().trim());
    }

    public List<FlightDetails> getFlightsByRoute(String originCode, String destinationCode) {
        if (originCode == null || originCode.trim().isEmpty() ||
                destinationCode == null || destinationCode.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both origin and destination airport codes are required");
            return new ArrayList<>();
        }

        Optional<Airport> origin = airportRepository.findByAirportCode(originCode.toUpperCase().trim());
        Optional<Airport> destination = airportRepository.findByAirportCode(destinationCode.toUpperCase().trim());

        if (origin.isEmpty()) {
            System.out.println("✗ Service Error: Origin airport not found: " + originCode);
            return new ArrayList<>();
        }

        if (destination.isEmpty()) {
            System.out.println("✗ Service Error: Destination airport not found: " + destinationCode);
            return new ArrayList<>();
        }

        return flightDetailsRepository.findByRoute(origin.get().getAirportCode(), destination.get().getAirportCode());
    }

    public List<FlightDetails> getFlightsByRoute(Airport origin, Airport destination) {
        if (origin == null || destination == null) {
            System.out.println("✗ Service Error: Both airports are required");
            return new ArrayList<>();
        }

        if (origin.getId() == null || destination.getId() == null) {
            System.out.println("✗ Service Error: Airport IDs are required");
            return new ArrayList<>();
        }

        return flightDetailsRepository.findByRoute(origin.getAirportCode(), destination.getAirportCode());
    }

    public List<FlightDetails> getFlightsByDepartureDate(String departureDate) {
        if (departureDate == null || departureDate.trim().isEmpty()) {
            System.out.println("✗ Service Error: Departure date cannot be null or empty");
            return new ArrayList<>();
        }

        if (!validatorService.isValidDate(departureDate)) {
            System.out.println("✗ Service Error: Invalid date format. Use YYYY-MM-DD");
            return new ArrayList<>();
        }

        return flightDetailsRepository.findByDepartureDate(departureDate);
    }

    public boolean addFlightDetails(FlightDetails flightDetails) {
        if (!isValidForService(flightDetails)) {
            return false;
        }

        if (flightDetailsRepository.findByFlightNumber(flightDetails.getFlightNumber()).isPresent()) {
            System.out.println("✗ Service Error: Flight number already exists: " + flightDetails.getFlightNumber());
            return false;
        }

        boolean success = flightDetailsRepository.createFlightDetails(flightDetails);
        if (success) {
            System.out.println("✓ Service: Flight added successfully");
        } else {
            System.out.println("✗ Service: Failed to add flight");
        }
        return success;
    }

    public boolean updateFlightDetails(FlightDetails flightDetails) {
        if (!isValidForService(flightDetails)) {
            return false;
        }

        if (flightDetails.getId() == null || flightDetails.getId() <= 0) {
            System.out.println("✗ Service Error: Valid flight ID required for update");
            return false;
        }

        // Check if flight exists
        if (flightDetailsRepository.findById(flightDetails.getId()).isEmpty()) {
            System.out.println("✗ Service Error: Flight not found for update");
            return false;
        }

        // Check for duplicate flight number (excluding current flight)
        Optional<FlightDetails> existingFlight = flightDetailsRepository.findByFlightNumber(flightDetails.getFlightNumber());
        if (existingFlight.isPresent() && !existingFlight.get().getId().equals(flightDetails.getId())) {
            System.out.println("✗ Service Error: Flight number already exists: " + flightDetails.getFlightNumber());
            return false;
        }

        boolean success = flightDetailsRepository.updateFlightDetails(flightDetails);
        if (success) {
            System.out.println("✓ Service: Flight updated successfully");
        } else {
            System.out.println("✗ Service: Failed to update flight");
        }
        return success;
    }

    public boolean deleteFlightDetails(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Service Error: Valid flight ID required for deletion");
            return false;
        }

        // Check if flight exists before deletion
        if (flightDetailsRepository.findById(id).isEmpty()) {
            System.out.println("✗ Service Error: Flight not found for deletion");
            return false;
        }

        boolean success = flightDetailsRepository.deleteFlightDetails(id);
        if (success) {
            System.out.println("✓ Service: Flight deleted successfully");
        } else {
            System.out.println("✗ Service: Failed to delete flight");
        }
        return success;
    }

    // ============================================================================
    // BUSINESS LOGIC METHODS
    // ============================================================================

    public boolean flightExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return flightDetailsRepository.findById(id).isPresent();
    }

    public boolean flightExistsByNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            return false;
        }
        return flightDetailsRepository.findByFlightNumber(flightNumber.toUpperCase().trim()).isPresent();
    }

    public int getFlightDetailsCount() {
        return flightDetailsRepository.findAll().size();
    }

    // ============================================================================
    // SEARCH AND FILTER METHODS
    // ============================================================================

    public List<FlightDetails> findFlightsByAirline(String airline) {
        if (airline == null || airline.trim().isEmpty()) {
            System.out.println("✗ Service Error: Airline cannot be null or empty");
            return new ArrayList<>();
        }

        return getAllFlightDetails().stream()
                .filter(flight -> flight.getFlightAirline().toLowerCase().contains(airline.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    public List<FlightDetails> findFlightsByPriceRange(String minPrice, String maxPrice) {
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

            return getAllFlightDetails().stream()
                    .filter(flight -> {
                        try {
                            double price = Double.parseDouble(flight.getFlightPrice());
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

    public List<FlightDetails> searchFlights(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        return getAllFlightDetails().stream()
                .filter(flight -> {
                    String originName = flight.getFlightOrigin() != null ?
                            flight.getFlightOrigin().getAirportFullName().toLowerCase() : "";
                    String originCode = flight.getFlightOrigin() != null ?
                            flight.getFlightOrigin().getAirportCode().toLowerCase() : "";
                    String destName = flight.getFlightDestination() != null ?
                            flight.getFlightDestination().getAirportFullName().toLowerCase() : "";
                    String destCode = flight.getFlightDestination() != null ?
                            flight.getFlightDestination().getAirportCode().toLowerCase() : "";

                    return flight.getFlightNumber().toLowerCase().contains(lowerSearchTerm) ||
                            flight.getFlightAirline().toLowerCase().contains(lowerSearchTerm) ||
                            originName.contains(lowerSearchTerm) ||
                            originCode.contains(lowerSearchTerm) ||
                            destName.contains(lowerSearchTerm) ||
                            destCode.contains(lowerSearchTerm) ||
                            flight.getFlightDepartureDate().contains(lowerSearchTerm) ||
                            flight.getFlightPrice().contains(lowerSearchTerm);
                })
                .collect(Collectors.toList());
    }

    // ============================================================================
    // BUSINESS VALIDATION
    // ============================================================================

    private boolean isValidForService(FlightDetails flightDetails) {
        if (flightDetails == null) {
            System.out.println("✗ Service Error: Flight details cannot be null");
            return false;
        }

        // Basic field validation
        if (flightDetails.getFlightNumber() == null || flightDetails.getFlightNumber().trim().isEmpty()) {
            System.out.println("✗ Service Error: Flight number is required");
            return false;
        }

        if (flightDetails.getFlightAirline() == null || flightDetails.getFlightAirline().trim().isEmpty()) {
            System.out.println("✗ Service Error: Airline is required");
            return false;
        }

        if (flightDetails.getFlightOrigin() == null || flightDetails.getFlightDestination() == null) {
            System.out.println("✗ Service Error: Both origin and destination airports are required");
            return false;
        }

        // Business logic validation
        if (flightDetails.getFlightOrigin().getId().equals(flightDetails.getFlightDestination().getId())) {
            System.out.println("✗ Service Error: Origin and destination airports cannot be the same");
            return false;
        }

        // Validate airports exist in database
        if (airportRepository.findById(flightDetails.getFlightOrigin().getId()).isEmpty()) {
            System.out.println("✗ Service Error: Origin airport not found in database");
            return false;
        }

        if (airportRepository.findById(flightDetails.getFlightDestination().getId()).isEmpty()) {
            System.out.println("✗ Service Error: Destination airport not found in database");
            return false;
        }

        // Date and time validation
        if (!validatorService.isValidDate(flightDetails.getFlightDepartureDate()) ||
                !validatorService.isValidDate(flightDetails.getFlightArrivalDate())) {
            System.out.println("✗ Service Error: Invalid date format. Use YYYY-MM-DD");
            return false;
        }

        if (!validatorService.isValidTime(flightDetails.getFlightDepartureTime()) ||
                !validatorService.isValidTime(flightDetails.getFlightArrivalTime())) {
            System.out.println("✗ Service Error: Invalid time format. Use HH:MM");
            return false;
        }

        // Price validation
        if (flightDetails.getFlightPrice() == null || flightDetails.getFlightPrice().trim().isEmpty()) {
            System.out.println("✗ Service Error: Flight price is required");
            return false;
        }

        try {
            double price = Double.parseDouble(flightDetails.getFlightPrice());
            if (price <= 0) {
                System.out.println("✗ Service Error: Flight price must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Service Error: Flight price must be a valid number");
            return false;
        }

        return true;
    }
}