package com.example.travelbuddybackend.controller;

import com.example.travelbuddybackend.models.BusDetails;
import com.example.travelbuddybackend.service.BusDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bus-details")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend to connect
public class BusDetailsController {

    private final BusDetailsService busDetailsService;

    @Autowired
    public BusDetailsController(BusDetailsService busDetailsService) {
        this.busDetailsService = busDetailsService;
    }

    /**
     * GET /api/bus-details
     * Get all bus details
     */
    @GetMapping
    public ResponseEntity<List<BusDetails>> getAllBusDetails() {
        try {
            List<BusDetails> busDetailsList = busDetailsService.getAllBusDetails();
            return ResponseEntity.ok(busDetailsList);
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/bus-details/{id}
     * Get bus details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BusDetails> getBusDetailsById(@PathVariable Integer id) {
        try {
            Optional<BusDetails> busDetails = busDetailsService.getBusDetailsById(id);
            return busDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /api/bus-details
     * Add new bus details
     */
    @PostMapping
    public ResponseEntity<String> addBusDetails(@RequestBody BusDetails busDetails) {
        try {
            boolean success = busDetailsService.addBusDetails(busDetails);
            if (success) {
                return ResponseEntity.ok("Bus details added successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to add bus details");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }

    /**
     * PUT /api/bus-details
     * Update existing bus details
     */
    @PutMapping
    public ResponseEntity<String> updateBusDetails(@RequestBody BusDetails busDetails) {
        try {
            boolean success = busDetailsService.updateBusDetails(busDetails);
            if (success) {
                return ResponseEntity.ok("Bus details updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to update bus details");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/bus-details/{id}
     * Delete bus details by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBusDetails(@PathVariable Integer id) {
        try {
            boolean success = busDetailsService.deleteBusDetails(id);
            if (success) {
                return ResponseEntity.ok("Bus details deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete bus details");
            }
        } catch (Exception e) {
            System.out.println("✗ Controller Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }
}