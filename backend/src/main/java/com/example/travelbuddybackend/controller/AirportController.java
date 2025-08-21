package com.example.travelbuddybackend.controller;

import com.example.travelbuddybackend.models.Airport;
import com.example.travelbuddybackend.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/airports")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend to connect
public class AirportController {

    private final AirportService airportService;

    @Autowired
    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    /**
     * GET /api/airports
     * Get all airports
     */
    @GetMapping
    public ResponseEntity<List<Airport>> getAllAirports() {
        try {
            List<Airport> airports = airportService.getAllAirports();
            return ResponseEntity.ok(airports);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/airports/{id}
     * Get airport by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Airport> getAirportById(@PathVariable Integer id) {
        try {
            Optional<Airport> airport = airportService.getAirportById(id);
            if (airport.isPresent()) {
                return ResponseEntity.ok(airport.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/airports/code/{airportCode}
     * Get airport by airport code (e.g., LAX, JFK)
     */
    @GetMapping("/code/{airportCode}")
    public ResponseEntity<Airport> getAirportByCode(@PathVariable String airportCode) {
        try {
            Optional<Airport> airport = airportService.getAirportByCode(airportCode);
            if (airport.isPresent()) {
                return ResponseEntity.ok(airport.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/airports/count
     * Get total number of airports
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getAirportCount() {
        try {
            int count = airportService.getAirportCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/airports/exists/{id}
     * Check if airport exists by ID
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> airportExists(@PathVariable Integer id) {
        try {
            boolean exists = airportService.airportExists(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /api/airports
     * Add a new airport
     */
    @PostMapping
    public ResponseEntity<String> addAirport(@RequestBody Airport airport) {
        try {
            boolean success = airportService.addAirport(airport);
            if (success) {
                return ResponseEntity.ok("Airport added successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to add airport");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    /**
     * PUT /api/airports
     * Update an existing airport
     */
    @PutMapping
    public ResponseEntity<String> updateAirport(@RequestBody Airport airport) {
        try {
            boolean success = airportService.updateAirport(airport);
            if (success) {
                return ResponseEntity.ok("Airport updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to update airport");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    /**
     * DELETE /api/airports
     * Delete an airport
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAirport(@RequestBody Airport airport) {
        try {
            boolean success = airportService.deleteAirport(airport);
            if (success) {
                return ResponseEntity.ok("Airport deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete airport");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    // ========== SEARCH ENDPOINTS ==========
    // These work with your existing service methods and repository

    /**
     * GET /api/airports/search?searchTerm={searchTerm}
     * Search airports by name, code, city, or country using existing getAllAirports() method
     */
    @GetMapping("/search")
    public ResponseEntity<List<Airport>> searchAirports(@RequestParam String searchTerm) {
        try {
            List<Airport> allAirports = airportService.getAllAirports();

            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            String lowerSearchTerm = searchTerm.toLowerCase().trim();

            // Filter airports based on search term
            List<Airport> searchResults = allAirports.stream()
                    .filter(airport ->
                            airport.getAirportFullName().toLowerCase().contains(lowerSearchTerm) ||
                                    airport.getAirportCode().toLowerCase().contains(lowerSearchTerm) ||
                                    airport.getAirportCityLocation().toLowerCase().contains(lowerSearchTerm) ||
                                    airport.getAirportCountryLocation().toLowerCase().contains(lowerSearchTerm))
                    .collect(Collectors.toList());

            System.out.println("✓ Controller: Airport search for '" + searchTerm + "' returned " + searchResults.size() + " results");
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/airports/city/{cityName}
     * Find airports by exact city name using existing getAllAirports() method
     */
    @GetMapping("/city/{cityName}")
    public ResponseEntity<List<Airport>> getAirportsByCity(@PathVariable String cityName) {
        try {
            List<Airport> allAirports = airportService.getAllAirports();

            List<Airport> cityAirports = allAirports.stream()
                    .filter(airport -> airport.getAirportCityLocation().equalsIgnoreCase(cityName))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(cityAirports);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}