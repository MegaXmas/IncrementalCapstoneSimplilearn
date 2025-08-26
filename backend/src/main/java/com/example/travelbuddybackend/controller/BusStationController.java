package com.example.travelbuddybackend.controller;

import com.example.travelbuddybackend.models.BusStation;
import com.example.travelbuddybackend.service.BusStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bus-stations")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend to connect
public class BusStationController {

    private final BusStationService busStationService;

    @Autowired
    public BusStationController(BusStationService busStationService) {
        this.busStationService = busStationService;
    }

    /**
     * GET /api/bus-stations
     * Get all bus stations
     */
    @GetMapping
    public ResponseEntity<List<BusStation>> getAllBusStations() {
        try {
            List<BusStation> busStations = busStationService.getAllBusStations();
            return ResponseEntity.ok(busStations);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/bus-stations/{id}
     * Get bus station by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BusStation> getBusStationById(@PathVariable Integer id) {
        try {
            Optional<BusStation> busStation = busStationService.getBusStationById(id);
            return busStation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/bus-stations/code/{stationCode}
     * Get bus station by station code (e.g., NYC, CHI)
     */
    @GetMapping("/code/{stationCode}")
    public ResponseEntity<BusStation> getBusStationByCode(@PathVariable String stationCode) {
        try {
            Optional<BusStation> busStation = busStationService.getBusStationByCode(stationCode);
            return busStation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/bus-stations/count
     * Get total number of bus stations
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getBusStationCount() {
        try {
            int count = busStationService.getBusStationCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/bus-stations/exists/{id}
     * Check if bus station exists by ID
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> busStationExists(@PathVariable Integer id) {
        try {
            boolean exists = busStationService.busStationExists(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /api/bus-stations
     * Add a new bus station
     */
    @PostMapping
    public ResponseEntity<String> addBusStation(@RequestBody BusStation busStation) {
        try {
            boolean success = busStationService.addBusStation(busStation);
            if (success) {
                return ResponseEntity.ok("Bus station added successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to add bus station");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    /**
     * PUT /api/bus-stations
     * Update an existing bus station
     */
    @PutMapping
    public ResponseEntity<String> updateBusStation(@RequestBody BusStation busStation) {
        try {
            boolean success = busStationService.updateBusStation(busStation);
            if (success) {
                return ResponseEntity.ok("Bus station updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to update bus station");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    /**
     * DELETE /api/bus-stations
     * Delete a bus station
     */
    @DeleteMapping
    public ResponseEntity<String> deleteBusStation(@RequestBody Integer id) {
        try {
            boolean success = busStationService.deleteBusStation(id);
            if (success) {
                return ResponseEntity.ok("Bus station deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete bus station");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    // ========== SEARCH ENDPOINTS ==========

    /**
     * GET /api/bus-stations/search?searchTerm={searchTerm}
     * Search bus stations by name, code, or city using existing getAllBusStations() method
     */
    @GetMapping("/search")
    public ResponseEntity<List<BusStation>> searchBusStations(@RequestParam String searchTerm) {
        try {
            var searchResults = busStationService.searchBusStations(searchTerm);

            System.out.println("✓ Controller: Bus station search for '" + searchTerm + "' returned " + searchResults.size() + " results");
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/name/{searchTerm}")
    public ResponseEntity<List<BusStation>> searchBusStationsByName(@PathVariable String searchTerm) {
        try {
            var searchResults = busStationService.findBusStationsByPartialName(searchTerm);

            System.out.println("✓ Controller: Bus station search for '" + searchTerm + "' returned " + searchResults.size() + " results");
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/code/{searchTerm}")
    public ResponseEntity<List<BusStation>> searchBusStationsByCode(@PathVariable String searchTerm) {
        try {
            var searchResults = busStationService.findBusStationsByCode(searchTerm);

            System.out.println("✓ Controller: Bus station search for '" + searchTerm + "' returned " + searchResults.size() + " results");
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/city/{searchTerm}")
    public ResponseEntity<List<BusStation>> getBusStationsByCity(@PathVariable String searchTerm) {
        try {
            var searchResults = busStationService.findBusStationsByCityLocation(searchTerm);

            System.out.println("✓ Controller: Bus station search for '" + searchTerm + "' returned " + searchResults.size() + " results");
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}