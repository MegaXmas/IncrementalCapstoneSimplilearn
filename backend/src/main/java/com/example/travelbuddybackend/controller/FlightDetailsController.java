package com.example.travelbuddybackend.controller;

import com.example.travelbuddybackend.models.FlightDetails;
import com.example.travelbuddybackend.service.FlightDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/Flight-details")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend to connect
public class FlightDetailsController {

    private final FlightDetailsService flightDetailsService;

    @Autowired
    public FlightDetailsController(FlightDetailsService FlightDetailsService) {
        this.flightDetailsService = FlightDetailsService;
    }

    /**
     * GET /api/Flight-details
     * Get all Flight details
     */
    @GetMapping
    public ResponseEntity<List<FlightDetails>> getAllFlightDetails() {
        try {
            List<FlightDetails> flightDetailsList = flightDetailsService.getAllFlightDetails();
            return ResponseEntity.ok(flightDetailsList);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/Flight-details/{id}
     * Get Flight details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<FlightDetails> getFlightDetailsById(@PathVariable Integer id) {
        try {
            Optional<FlightDetails> flightDetails = flightDetailsService.getFlightDetailsById(id);
            return flightDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /api/Flight-details
     * Add new Flight details
     */
    @PostMapping
    public ResponseEntity<String> addFlightDetails(@RequestBody FlightDetails flightDetails) {
        try {
            boolean success = flightDetailsService.addFlightDetails(flightDetails);
            if (success) {
                return ResponseEntity.ok("Flight details added successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to add flight details");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }

    /**
     * PUT /api/Flight-details
     * Update existing Flight details
     */
    @PutMapping
    public ResponseEntity<String> updateFlightDetails(@RequestBody FlightDetails flightDetails) {
        try {
            boolean success = flightDetailsService.updateFlightDetails(flightDetails);
            if (success) {
                return ResponseEntity.ok("Flight details updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to update flight details");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/Flight-details/{id}
     * Delete Flight details by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFlightDetails(@PathVariable Integer id) {
        try {
            boolean success = flightDetailsService.deleteFlightDetails(id);
            if (success) {
                return ResponseEntity.ok("Flight details deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete flight details");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }
}