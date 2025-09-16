// BookingSearchController.java - Works with your existing services
package com.example.travelbuddybackend.controller;

import com.example.travelbuddybackend.models.AvailableTicket;
import com.example.travelbuddybackend.models.Booking;
import com.example.travelbuddybackend.service.BookingSearchService;
import com.example.travelbuddybackend.service.BookingSearchService.BookingSearchCriteria;
import com.example.travelbuddybackend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend
public class BookingSearchController {

    @Autowired
    private BookingSearchService bookingSearchService;

    @Autowired
    private BookingService bookingService;

    /**
     * Search for AVAILABLE tickets to book using your detail services
     * POST /api/search/available-tickets
     */
    @PostMapping("/available-tickets")
    public ResponseEntity<List<AvailableTicket>> searchAvailableTickets(@RequestBody BookingSearchCriteria criteria) {
        try {
            System.out.println("🔍 Searching available tickets:");
            System.out.println("Transport Type: " + criteria.getTransportType());

            // Fix these to show the actual fields being sent:
            System.out.println("Departure Station: " + criteria.getDepartureStation());
            System.out.println("Arrival Station: " + criteria.getArrivalStation());
            System.out.println("Airline/Line: " + criteria.getAirline() + "/" + criteria.getLine());
            System.out.println("Price between " + criteria.getMinPrice() + " and " + criteria.getMaxPrice());

            List<AvailableTicket> results = bookingSearchService.searchAvailableTickets(criteria);

            System.out.println("✅ Found " + results.size() + " available tickets");
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            System.out.println("❌ Error searching available tickets: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Search for EXISTING bookings using your BookingService
     * POST /api/search/existing-bookings
     */
    @PostMapping("/existing-bookings")
    public ResponseEntity<List<Booking>> searchExistingBookings(@RequestBody BookingSearchCriteria criteria) {

        try {
            System.out.println("🔍 Searching existing bookings:");

            List<Booking> results = bookingSearchService.searchExistingBookings(criteria);

            System.out.println("✅ Found " + results.size() + " existing bookings");
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            System.out.println("❌ Error searching existing bookings: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Quick search for available flights by airline
     * GET /api/search/flights?airline=Delta
     */
    @GetMapping("/flights")
    public ResponseEntity<List<AvailableTicket>> searchFlights(@RequestParam(required = false) String airline) {

        try {
            BookingSearchCriteria criteria = new BookingSearchCriteria();
            criteria.setTransportType("flight");
            if (airline != null) {
                criteria.setAirline(airline);
            }

            List<AvailableTicket> results = bookingSearchService.searchAvailableTickets(criteria);
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            System.out.println("❌ Error searching flights: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Quick search for available trains by line
     * GET /api/search/trains?line=Amtrak
     */
    @GetMapping("/trains")
    public ResponseEntity<List<AvailableTicket>> searchTrains(@RequestParam(required = false) String line) {

        try {
            BookingSearchCriteria criteria = new BookingSearchCriteria();
            criteria.setTransportType("train");
            if (line != null) {
                criteria.setLine(line);
            }

            List<AvailableTicket> results = bookingSearchService.searchAvailableTickets(criteria);
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            System.out.println("❌ Error searching trains: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Quick search for available buses by line
     * GET /api/search/buses?line=Greyhound
     */
    @GetMapping("/buses")
    public ResponseEntity<List<AvailableTicket>> searchBuses(@RequestParam(required = false) String line) {

        try {
            BookingSearchCriteria criteria = new BookingSearchCriteria();
            criteria.setTransportType("bus");
            if (line != null) {
                criteria.setLine(line);
            }

            List<AvailableTicket> results = bookingSearchService.searchAvailableTickets(criteria);
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            System.out.println("❌ Error searching buses: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Search existing bookings by client email using your existing service
     * GET /api/search/my-bookings?email=john@example.com
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(@RequestParam String email) {

        try {
            List<Booking> results = bookingService.getBookingsByClientEmail(email);
            System.out.println("✅ Found " + results.size() + " bookings for " + email);
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            System.out.println("❌ Error getting bookings by email: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}