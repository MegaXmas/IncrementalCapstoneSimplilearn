package com.example.travelbuddybackend.controller;

import com.example.travelbuddybackend.models.AvailableTicket;
import com.example.travelbuddybackend.models.Booking;
import com.example.travelbuddybackend.models.BusDetails;
import com.example.travelbuddybackend.models.Client;
import com.example.travelbuddybackend.service.BookingSearchService;
import com.example.travelbuddybackend.service.BookingService;
import com.example.travelbuddybackend.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    private BookingSearchService bookingSearchService;

    @Autowired
    private ClientService clientService; // Assuming you have this service

    @PostMapping("/bus")
    public ResponseEntity<?> createBusBooking(@RequestBody AvailableTicket bookingRequest, HttpServletRequest httpRequest) {
        try {
            Client client = clientService.getClientFromToken(extractTokenFromRequest(httpRequest));

            if (client == null) {
                return ResponseEntity.status(401).body("Client not logged in");
            }



            if (bookingRequest != null) {
                return ResponseEntity.ok(bookingRequest);
            } else {
                return ResponseEntity.status(500).body("Booking failed");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating booking: " + e.getMessage());
        }
    }

    // Helper method to extract JWT token from Authorization header
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
