package com.example.travelbuddybackend.controller;

import com.example.travelbuddybackend.models.TrainStation;
import com.example.travelbuddybackend.service.TrainStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/train-stations")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend to connect
public class TrainStationController {

    private final TrainStationService trainStationService;

    @Autowired
    public TrainStationController(TrainStationService trainStationService) {
        this.trainStationService = trainStationService;
    }

    /**
     * GET /api/train-stations
     * Get all train stations
     */
    @GetMapping
    public ResponseEntity<List<TrainStation>> getAllTrainStations() {
        try {
            List<TrainStation> trainStations = trainStationService.getAllTrainStations();
            return ResponseEntity.ok(trainStations);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/train-stations/{id}
     * Get train station by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrainStation> getTrainStationById(@PathVariable Integer id) {
        try {
            Optional<TrainStation> trainStation = trainStationService.getTrainStationById(id);
            return trainStation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/train-stations/code/{stationCode}
     * Get train station by station code
     */
    @GetMapping("/code/{stationCode}")
    public ResponseEntity<TrainStation> getTrainStationByCode(@PathVariable String stationCode) {
        try {
            Optional<TrainStation> trainStation = trainStationService.getTrainStationByCode(stationCode);
            return trainStation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/train-stations/count
     * Get total number of train stations
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getTrainStationCount() {
        try {
            int count = trainStationService.getTrainStationCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/train-stations/exists/{id}
     * Check if train station exists by ID
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> trainStationExists(@PathVariable Integer id) {
        try {
            boolean exists = trainStationService.trainStationExists(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /api/train-stations
     * Add a new train station
     */
    @PostMapping
    public ResponseEntity<String> addTrainStation(@RequestBody TrainStation trainStation) {
        try {
            boolean success = trainStationService.addTrainStation(trainStation);
            if (success) {
                return ResponseEntity.ok("Train station added successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to add train station");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    /**
     * PUT /api/train-stations
     * Update an existing train station
     */
    @PutMapping
    public ResponseEntity<String> updateTrainStation(@RequestBody TrainStation trainStation) {
        try {
            boolean success = trainStationService.updateTrainStation(trainStation);
            if (success) {
                return ResponseEntity.ok("Train station updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to update train station");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    /**
     * DELETE /api/train-stations
     * Delete a train station
     */
    @DeleteMapping
    public ResponseEntity<String> deleteTrainStation(@RequestBody Integer id) {
        try {
            boolean success = trainStationService.deleteTrainStation(id);
            if (success) {
                return ResponseEntity.ok("Train station deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete train station");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    // ========== SEARCH ENDPOINTS ==========

    /**
     * GET /api/train-stations/search?searchTerm={searchTerm}
     * Search train stations by name, code, or city
     */
    @GetMapping("/search")
    public ResponseEntity<List<TrainStation>> searchTrainStations(@RequestParam String searchTerm) {
        try {
            var searchResults = trainStationService.searchTrainStations(searchTerm);

            System.out.println("✓ Controller: Train station search for '" + searchTerm + "' returned " + searchResults.size() + " results");
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}