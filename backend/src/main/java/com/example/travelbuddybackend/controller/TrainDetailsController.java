package com.example.travelbuddybackend.controller;

import com.example.travelbuddybackend.models.TrainDetails;
import com.example.travelbuddybackend.service.TrainDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/train-details")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend to connect
public class TrainDetailsController {

    private final TrainDetailsService trainDetailsService;

    @Autowired
    public TrainDetailsController(TrainDetailsService TrainDetailsService) {
        this.trainDetailsService = TrainDetailsService;
    }

    /**
     * GET /api/Train-details
     * Get all Train details
     */
    @GetMapping
    public ResponseEntity<List<TrainDetails>> getAllTrainDetails() {
        try {
            List<TrainDetails> trainDetailsList = trainDetailsService.getAllTrainDetails();
            return ResponseEntity.ok(trainDetailsList);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/Train-details/{id}
     * Get Train details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrainDetails> getTrainDetailsById(@PathVariable Integer id) {
        try {
            Optional<TrainDetails> trainDetails = trainDetailsService.getTrainDetailsById(id);
            return trainDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /api/Train-details
     * Add new Train details
     */
    @PostMapping
    public ResponseEntity<String> addTrainDetails(@RequestBody TrainDetails trainDetails) {
        try {
            boolean success = trainDetailsService.addTrainDetails(trainDetails);
            if (success) {
                return ResponseEntity.ok("Train details added successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to add train details");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }

    /**
     * PUT /api/Train-details
     * Update existing Train details
     */
    @PutMapping
    public ResponseEntity<String> updateTrainDetails(@RequestBody TrainDetails trainDetails) {
        try {
            boolean success = trainDetailsService.updateTrainDetails(trainDetails);
            if (success) {
                return ResponseEntity.ok("Train details updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to update train details");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/Train-details/{id}
     * Delete Train details by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTrainDetails(@PathVariable Integer id) {
        try {
            boolean success = trainDetailsService.deleteTrainDetails(id);
            if (success) {
                return ResponseEntity.ok("Train details deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete train details");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }
}