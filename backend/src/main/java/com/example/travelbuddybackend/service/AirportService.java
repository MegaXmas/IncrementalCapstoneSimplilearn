package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.Airport;
import com.example.travelbuddybackend.models.BusStation;
import com.example.travelbuddybackend.models.TrainStation;
import com.example.travelbuddybackend.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    @Autowired
    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    /**
     * Get all airports from the database
     * @return List of all airports (empty list if none found or error occurs)
     */
    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    /**
     * Get an airport by its ID
     * @param id The airport ID to search for
     * @return Optional containing the airport if found, empty otherwise
     */
    public Optional<Airport> getAirportById(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Airport ID cannot be null");
            return Optional.empty();
        }
        return airportRepository.findById(id);
    }

    /**
     * Get an airport by its airport code (e.g., "LAX", "JFK")
     * @param airportCode The airport code to search for
     * @return Optional containing the airport if found, empty otherwise
     */
    public Optional<Airport> getAirportByCode(String airportCode) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            System.out.println("✗ Service Error: Airport code cannot be null or empty");
            return Optional.empty();
        }
        return airportRepository.findByAirportCode(airportCode);
    }

    /**
     * Add a new airport to the database
     * @param airport The airport to add
     * @return true if airport was successfully added, false otherwise
     */
    public boolean addAirport(Airport airport) {
        if (airport == null) {
            System.out.println("✗ Service Error: Cannot add null airport");
            return false;
        }

        boolean success = airportRepository.newAirport(airport);
        if (success) {
            System.out.println("✓ Service: Airport successfully added through service layer");
        } else {
            System.out.println("✗ Service: Failed to add airport through service layer");
        }
        return success;
    }

    /**
     * Update an existing airport in the database
     * @param airport The airport with updated information
     * @return true if airport was successfully updated, false otherwise
     */
    public boolean updateAirport(Airport airport) {
        if (airport == null) {
            System.out.println("✗ Service Error: Cannot update null airport");
            return false;
        }

        if (airport.getId() == null || airport.getId() <= 0) {
            System.out.println("✗ Service Error: Airport must have a valid ID for update");
            return false;
        }

        boolean success = airportRepository.updateAirport(airport);
        if (success) {
            System.out.println("✓ Service: Airport successfully updated through service layer");
        } else {
            System.out.println("✗ Service: Failed to update airport through service layer");
        }
        return success;
    }

    /**
     * Delete an airport from the database
     * @param airport The airport object to delete
     * @return true if airport was successfully deleted, false otherwise
     */
    public boolean deleteAirport(Airport airport) {
        if (airport == null) {
            System.out.println("✗ Service Error: Cannot delete null airport");
            return false;
        }

        boolean success = airportRepository.deleteAirport(airport);
        if (success) {
            System.out.println("✓ Service: Airport successfully deleted through service layer");
        } else {
            System.out.println("✗ Service: Failed to delete airport through service layer");
        }
        return success;
    }

    /**
     * Check if an airport exists in the database by ID
     * @param id The airport ID to check
     * @return true if airport exists, false otherwise
     */
    public boolean airportExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return getAirportById(id).isPresent();
    }

    /**
     * Check if an airport exists in the database by airport code
     * @param airportCode The airport code to check
     * @return true if airport exists, false otherwise
     */
    public boolean airportExistsByCode(String airportCode) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            return false;
        }
        return getAirportByCode(airportCode).isPresent();
    }

    /**
     * Get the total number of airports in the database
     * @return The count of all airports
     */
    public int getAirportCount() {
        List<Airport> airports = getAllAirports();
        return airports.size();
    }

    public List<Airport> findAirportByPartialName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            System.out.println("✗ Service Error: Partial name cannot be null or empty");
            return new ArrayList<>();
        }

        List<Airport> allAirports = getAllAirports();
        return allAirports.stream()
                .filter(station -> station.getAirportFullName().toLowerCase()
                        .contains(partialName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Airport> findAirportByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            System.out.println("✗ Service Error: Code cannot be null or empty");
            return new ArrayList<>();
        }

        List<Airport> allAirports = getAllAirports();
        return allAirports.stream()
                .filter(station -> station.getAirportCode().toLowerCase()
                        .contains(code.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Airport> findAirportByCityLocation(String cityLocation) {
        if (cityLocation == null || cityLocation.trim().isEmpty()) {
            System.out.println("✗ Service Error: City location cannot be null or empty");
            return new ArrayList<>();
        }

        List<Airport> allAirports = getAllAirports();
        return allAirports.stream()
                .filter(station -> station.getAirportCityLocation().toLowerCase()
                        .contains(cityLocation.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Airport> findAirportByCountryLocation(String countryLocation) {
        if (countryLocation == null || countryLocation.trim().isEmpty()) {
            System.out.println("✗ Service Error: Country location cannot be null or empty");
            return new ArrayList<>();
        }

        List<Airport> allAirports = getAllAirports();
        return allAirports.stream()
                .filter(station -> station.getAirportCountryLocation().toLowerCase()
                        .contains(countryLocation.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Search train stations by multiple criteria (for advanced search functionality)
     * @param searchTerm The search term to match against station name, code, or city
     * @return List of train stations matching the search term
     */
    public List<Airport> searchAirports(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
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
}