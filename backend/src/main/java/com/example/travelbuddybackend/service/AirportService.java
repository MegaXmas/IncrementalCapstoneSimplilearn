package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.Airport;
import com.example.travelbuddybackend.repository.AirportRepository;
import com.example.travelbuddybackend.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Airport Service - Business Logic Layer
 *
 * Optimized version that leverages database queries instead of in-memory filtering
 * for better performance with large datasets
 */
@Service
public class AirportService {

    private final AirportRepository airportRepository;
    private final ValidatorService validatorService;

    @Autowired
    public AirportService(AirportRepository airportRepository, ValidatorService validatorService) {
        this.airportRepository = airportRepository;
        this.validatorService = validatorService;
    }

    // ============================================================================
    // CORE BUSINESS OPERATIONS
    // ============================================================================

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public Optional<Airport> getAirportById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Service Error: Airport ID must be a positive integer");
            return Optional.empty();
        }
        return airportRepository.findById(id);
    }

    public Optional<Airport> getAirportByCode(String airportCode) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            System.out.println("✗ Service Error: Airport code cannot be null or empty");
            return Optional.empty();
        }
        return airportRepository.findByAirportCode(airportCode.toUpperCase().trim());
    }

    public boolean addAirport(Airport airport) {
        if (!isValidForService(airport)) {
            return false;
        }

        // Check for duplicate airport code
        if (airportRepository.findByAirportCode(airport.getAirportCode()).isPresent()) {
            System.out.println("✗ Service Error: Airport code already exists: " + airport.getAirportCode());
            return false;
        }

        boolean success = airportRepository.createAirport(airport);
        if (success) {
            System.out.println("✓ Service: Airport added successfully");
        } else {
            System.out.println("✗ Service: Failed to add airport");
        }
        return success;
    }

    public boolean updateAirport(Airport airport) {
        if (!isValidForService(airport)) {
            return false;
        }

        if (airport.getId() == null || airport.getId() <= 0) {
            System.out.println("✗ Service Error: Valid airport ID required for update");
            return false;
        }

        // Check if airport exists
        if (airportRepository.findById(airport.getId()).isEmpty()) {
            System.out.println("✗ Service Error: Airport not found for update");
            return false;
        }

        // Check for duplicate airport code (excluding current airport)
        Optional<Airport> existingAirport = airportRepository.findByAirportCode(airport.getAirportCode());
        if (existingAirport.isPresent() && !existingAirport.get().getId().equals(airport.getId())) {
            System.out.println("✗ Service Error: Airport code already exists: " + airport.getAirportCode());
            return false;
        }

        boolean success = airportRepository.updateAirport(airport);
        if (success) {
            System.out.println("✓ Service: Airport updated successfully");
        } else {
            System.out.println("✗ Service: Failed to update airport");
        }
        return success;
    }

    public boolean deleteAirport(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Service Error: Valid airport ID required for deletion");
            return false;
        }

        // Check if airport exists before deletion
        if (airportRepository.findById(id).isEmpty()) {
            System.out.println("✗ Service Error: Airport not found for deletion");
            return false;
        }

        boolean success = airportRepository.deleteAirport(id);
        if (success) {
            System.out.println("✓ Service: Airport deleted successfully");
        } else {
            System.out.println("✗ Service: Failed to delete airport");
        }
        return success;
    }

    // ============================================================================
    // BUSINESS LOGIC METHODS
    // ============================================================================

    public boolean airportExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return airportRepository.findById(id).isPresent();
    }

    public boolean airportExistsByCode(String airportCode) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            return false;
        }
        return airportRepository.findByAirportCode(airportCode.toUpperCase().trim()).isPresent();
    }

    public int getAirportCount() {
        return airportRepository.findAll().size();
    }

    // ============================================================================
    // SEARCH METHODS - OPTIMIZED to use database queries instead of in-memory filtering
    // ============================================================================

    /**
     * Find airports by partial name match - OPTIMIZED with database query
     */
    public List<Airport> findAirportByPartialName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            System.out.println("✗ Service Error: Partial name cannot be null or empty");
            return new ArrayList<>();
        }

        // Use database LIKE query instead of loading all airports into memory
        return airportRepository.findByPartialName(partialName.trim());
    }

    /**
     * Find airports by city location - OPTIMIZED with database query
     */
    public List<Airport> findAirportByCityLocation(String cityLocation) {
        if (cityLocation == null || cityLocation.trim().isEmpty()) {
            System.out.println("✗ Service Error: City location cannot be null or empty");
            return new ArrayList<>();
        }

        // Use database LIKE query instead of in-memory filtering
        return airportRepository.findByCityLocation(cityLocation.trim());
    }

    /**
     * Find airports by country location - OPTIMIZED with database query
     */
    public List<Airport> findAirportByCountryLocation(String countryLocation) {
        if (countryLocation == null || countryLocation.trim().isEmpty()) {
            System.out.println("✗ Service Error: Country location cannot be null or empty");
            return new ArrayList<>();
        }

        // Use database LIKE query instead of in-memory filtering
        return airportRepository.findByCountryLocation(countryLocation.trim());
    }

    /**
     * Find airports by code pattern - Uses in-memory filtering for complex patterns
     */
    public List<Airport> findAirportByCodePattern(String codePattern) {
        if (codePattern == null || codePattern.trim().isEmpty()) {
            System.out.println("✗ Service Error: Code pattern cannot be null or empty");
            return new ArrayList<>();
        }

        List<Airport> allAirports = getAllAirports();
        return allAirports.stream()
                .filter(airport -> airport.getAirportCode().toLowerCase()
                        .contains(codePattern.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    /**
     * Multi-criteria search - optimized to minimize database calls
     */
    public List<Airport> searchAirports(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String trimmedTerm = searchTerm.trim();

        // Try exact code match first (most specific)
        Optional<Airport> exactMatch = airportRepository.findByAirportCode(trimmedTerm.toUpperCase());
        if (exactMatch.isPresent()) {
            List<Airport> result = new ArrayList<>();
            result.add(exactMatch.get());
            return result;
        }

        // If not exact code match, do comprehensive search using in-memory filtering
        // This is acceptable for search functionality where we want comprehensive results
        String lowerSearchTerm = trimmedTerm.toLowerCase();
        List<Airport> allAirports = getAllAirports();

        return allAirports.stream()
                .filter(airport ->
                        airport.getAirportFullName().toLowerCase().contains(lowerSearchTerm) ||
                                airport.getAirportCode().toLowerCase().contains(lowerSearchTerm) ||
                                airport.getAirportCityLocation().toLowerCase().contains(lowerSearchTerm) ||
                                airport.getAirportCountryLocation().toLowerCase().contains(lowerSearchTerm) ||
                                airport.getAirportTimezone().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    // ============================================================================
    // BUSINESS VALIDATION
    // ============================================================================

    private boolean isValidForService(Airport airport) {
        if (airport == null) {
            System.out.println("✗ Service Error: Airport cannot be null");
            return false;
        }

        // Basic field validation
        if (airport.getAirportFullName() == null || airport.getAirportFullName().trim().isEmpty()) {
            System.out.println("✗ Service Error: Airport name is required");
            return false;
        }

        if (airport.getAirportCode() == null || airport.getAirportCode().trim().isEmpty()) {
            System.out.println("✗ Service Error: Airport code is required");
            return false;
        }

        // Business validation - airport code format
        if (!isValidAirportCodeFormat(airport.getAirportCode())) {
            System.out.println("✗ Service Error: Invalid airport code format. Must be 3-4 uppercase letters");
            return false;
        }

        if (airport.getAirportCityLocation() == null || airport.getAirportCityLocation().trim().isEmpty()) {
            System.out.println("✗ Service Error: Airport city location is required");
            return false;
        }

        if (airport.getAirportCountryLocation() == null || airport.getAirportCountryLocation().trim().isEmpty()) {
            System.out.println("✗ Service Error: Airport country location is required");
            return false;
        }

        if (airport.getAirportTimezone() == null || airport.getAirportTimezone().trim().isEmpty()) {
            System.out.println("✗ Service Error: Airport timezone is required");
            return false;
        }

        return true;
    }

    private boolean isValidAirportCodeFormat(String airportCode) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            return false;
        }

        String trimmed = airportCode.trim();
        // Airport codes are typically 3-4 uppercase letters (IATA/ICAO standards)
        return trimmed.length() >= 3 && trimmed.length() <= 4 && trimmed.matches("[A-Z]{3,4}");
    }
}