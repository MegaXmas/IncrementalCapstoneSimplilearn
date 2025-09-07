package com.example.travelbuddybackend.controller;

import com.example.travelbuddybackend.models.Booking;
import com.example.travelbuddybackend.models.BusDetails;
import com.example.travelbuddybackend.models.Client;
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

    @Autowired
    private ClientService clientService; // Assuming you have this service

    @PostMapping("/bus")
    public ResponseEntity<?> createBusBooking(@RequestBody BusDetails busDetails, HttpServletRequest httpRequest) {
        try {
            Client client = clientService.getClientFromToken(extractTokenFromRequest(httpRequest));

            if (client == null) {
                return ResponseEntity.status(401).body("Client not logged in");
            }

            // Create BusDetails object from request
            BusDetails busBooking = new BusDetails();
            busDetails.setBusNumber(busDetails.getBusNumber());
            busDetails.setBusLine(busDetails.getBusLine());
            busDetails.setBusDepartureStation(busDetails.getBusDepartureStation());
            busDetails.setBusArrivalStation(busDetails.getBusArrivalStation());
            busDetails.setBusDepartureDate(busDetails.getBusDepartureDate());
            busDetails.setBusDepartureTime(busDetails.getBusDepartureTime());
            busDetails.setBusArrivalDate(busDetails.getBusArrivalDate());
            busDetails.setBusArrivalTime(busDetails.getBusArrivalTime());
            busDetails.setBusRidePrice(busDetails.getBusRidePrice());
            busDetails.setBusRideDuration(busDetails.getBusRideDuration());

            // Create booking
            Booking booking = bookingService.bookBusTicket(client, busDetails);

            if (booking != null) {
                return ResponseEntity.ok(booking);
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
