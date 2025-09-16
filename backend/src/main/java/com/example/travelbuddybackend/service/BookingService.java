package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.*;
import com.example.travelbuddybackend.repository.BookingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

//UNFINISHED IMPLEMENTATION
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ObjectMapper objectMapper; // For JSON serialization/deserialization

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
        this.objectMapper = new ObjectMapper();
    }



    // =============================================================
    // POLYMORPHIC BOOKING METHODS - Core business functionality
    // =============================================================

    /**
     * Book a flight ticket - handles flight-specific booking logic
     * @param client The client making the booking
     * @param flightDetails The flight being booked
     * @return Booking object if successful, null if failed
     */
    public Booking bookTicket(Client client, FlightDetails flightDetails) {
        System.out.println("Booking flight for: " + client.getName());
        System.out.println("Flight: " + flightDetails.getFlightNumber() + " from " +
                flightDetails.getFlightOrigin() + " to " + flightDetails.getFlightDestination());

        try {
            // Generate a unique booking ID with flight prefix
            String bookingId = generateBookingId("FL");

            // Convert flight details to JSON for storage
            String transportJson = objectMapper.writeValueAsString(flightDetails);

            // Create the booking object
            Booking booking = new Booking();
            booking.setBookingId(bookingId);
            booking.setTransportDetailsJson(transportJson);
            booking.setClientName(client.getName());
            booking.setClientEmail(client.getEmail());
            booking.setClientPhone(client.getPhone());

            boolean success = bookingRepository.createBooking(booking);
            if (success) {
                System.out.println("✓ Flight booking successful! Booking ID: " + bookingId);
                return booking;
            } else {
                System.out.println("✗ Flight booking failed");
                return null;
            }
        } catch (Exception e) {
            System.out.println("✗ Error creating flight booking: " + e.getMessage());
            return null;
        }
    }

    /**
     * Book a train ticket - handles train-specific booking logic
     * @param client The client making the booking
     * @param trainDetails The train being booked
     * @return Booking object if successful, null if failed
     */
    public Booking bookTicket(Client client, TrainDetails trainDetails) {
        System.out.println("Booking train for: " + client.getName());
        System.out.println("Train: " + trainDetails.getTrainNumber() + " from " +
                trainDetails.getTrainDepartureStation() + " to " + trainDetails.getTrainArrivalStation());

        try {
            // Generate unique booking ID with train prefix
            String bookingId = generateBookingId("TR");

            // Convert train details to JSON
            String transportJson = objectMapper.writeValueAsString(trainDetails);

            // Create booking object
            Booking booking = new Booking();
            booking.setBookingId(bookingId);
            booking.setTransportDetailsJson(transportJson);
            booking.setClientName(client.getName());
            booking.setClientEmail(client.getEmail());
            booking.setClientPhone(client.getPhone());

            boolean success = bookingRepository.createBooking(booking);
            if (success) {
                System.out.println("✓ Train booking successful! Booking ID: " + bookingId);
                return booking;
            } else {
                System.out.println("✗ Train booking failed");
                return null;
            }
        } catch (Exception e) {
            System.out.println("✗ Error creating train booking: " + e.getMessage());
            return null;
        }
    }

    /**
     * Book a bus ticket - handles bus-specific booking logic
     * @param client The client making the booking
     * @param bookingRequest The bus being booked
     * @return Booking object if successful, null if failed
     */
    public Booking bookTicket(Client client, AvailableTicket bookingRequest) {
        System.out.println("Booking bus for: " + client.getName());
        System.out.println("Bus: " + bookingRequest.getNumber() + " from " +
                bookingRequest.getDepartureLocation() + " to " + bookingRequest.getArrivalLocation());

        try {
            // Generate unique booking ID with bus prefix
            String bookingId = generateBookingId("BS");

            // Convert bus details to JSON
            String transportJson = objectMapper.writeValueAsString(bookingRequest);

            // Create booking object
            Booking booking = new Booking();
            booking.setBookingId(bookingId);
            booking.setTransportDetailsJson(transportJson);
            booking.setClientName(client.getName());
            booking.setClientEmail(client.getEmail());
            booking.setClientPhone(client.getPhone());

            boolean success = bookingRepository.createBooking(booking);
            if (success) {
                System.out.println("✓ Bus booking successful! Booking ID: " + bookingId);
                return booking;
            } else {
                System.out.println("✗ Bus booking failed");
                return null;
            }
        } catch (Exception e) {
            System.out.println("✗ Error creating bus booking: " + e.getMessage());
            return null;
        }
    }

    // =============================================================
    // STANDARD CRUD OPERATIONS
    // =============================================================

    /**
     * Get all bookings from the database
     * @return List of all bookings (empty list if none found or error occurs)
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Get booking by internal database ID
     * @param id The internal booking ID to search for
     * @return Optional containing the booking if found, empty otherwise
     */
    public Optional<Booking> getBookingById(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Booking ID cannot be null");
            return Optional.empty();
        }
        return bookingRepository.findById(id);
    }

    /**
     * Get booking by customer-facing booking ID
     * @param bookingId The customer booking ID (like "FL123456")
     * @return Optional containing the booking if found, empty otherwise
     */
    public Optional<Booking> getBookingByBookingId(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            System.out.println("✗ Service Error: Booking ID cannot be null or empty");
            return Optional.empty();
        }
        return bookingRepository.findByBookingId(bookingId);
    }

    /**
     * Get all bookings for a specific customer email
     * @param clientEmail The customer's email address
     * @return List of bookings for that customer
     */
    public List<Booking> getBookingsByClientEmail(String clientEmail) {
        if (clientEmail == null || clientEmail.trim().isEmpty()) {
            System.out.println("✗ Service Error: Client email cannot be null or empty");
            return new ArrayList<>();
        }
        return bookingRepository.findByClientEmail(clientEmail);
    }

    /**
     * Update an existing booking
     * @param booking The booking with updated information
     * @return true if booking was successfully updated, false otherwise
     */
    public boolean updateBooking(Booking booking) {
        if (booking == null) {
            System.out.println("✗ Service Error: Cannot update null booking");
            return false;
        }

        if (booking.getId() == null || booking.getId() <= 0) {
            System.out.println("✗ Service Error: Booking must have a valid ID for update");
            return false;
        }

        boolean success = bookingRepository.updateBooking(booking);
        if (success) {
            System.out.println("✓ Service: Booking successfully updated through service layer");
        } else {
            System.out.println("✗ Service: Failed to update booking through service layer");
        }
        return success;
    }

    /**
     * Cancel a booking by booking ID
     * Uses soft delete approach - marks booking as cancelled rather than deleting
     * @param bookingId The customer booking ID to cancel
     * @return true if booking was successfully cancelled, false otherwise
     */
    public boolean cancelBooking(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            System.out.println("✗ Service Error: Booking ID cannot be null or empty");
            return false;
        }

        boolean success = bookingRepository.cancelBooking(bookingId);
        if (success) {
            System.out.println("✓ Service: Booking successfully cancelled through service layer");
        } else {
            System.out.println("✗ Service: Failed to cancel booking through service layer");
        }
        return success;
    }

    /**
     * Hard delete a booking by internal ID
     * @param id The internal booking ID to delete
     * @return true if booking was successfully deleted, false otherwise
     */
    public boolean deleteBooking(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Booking ID cannot be null");
            return false;
        }

        if (id <= 0) {
            System.out.println("✗ Service Error: Invalid booking ID: " + id);
            return false;
        }

        boolean success = bookingRepository.deleteBooking(id);
        if (success) {
            System.out.println("✓ Service: Booking successfully deleted through service layer");
        } else {
            System.out.println("✗ Service: Failed to delete booking through service layer");
        }
        return success;
    }

    // =============================================================
    // BUSINESS LOGIC AND UTILITY METHODS
    // =============================================================

    /**
     * Check if a booking exists by booking ID
     * @param bookingId The customer booking ID to check
     * @return true if booking exists, false otherwise
     */
    public boolean bookingExists(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            return false;
        }
        return getBookingByBookingId(bookingId).isPresent();
    }

    /**
     * Get the total number of bookings in the system
     * @return The count of all bookings
     */
    public int getBookingCount() {
        List<Booking> bookings = getAllBookings();
        return bookings.size();
    }

    /**
     * Find bookings by client name (partial match)
     * @param clientName The partial name to search for
     * @return List of bookings matching the name
     */
    public List<Booking> findBookingsByClientName(String clientName) {
        if (clientName == null || clientName.trim().isEmpty()) {
            System.out.println("✗ Service Error: Client name cannot be null or empty");
            return new ArrayList<>();
        }

        List<Booking> allBookings = getAllBookings();
        return allBookings.stream()
                .filter(booking -> booking.getClientName().toLowerCase()
                        .contains(clientName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Find bookings by transport type (flight, train, bus)
     * @param transportType "FL" for flights, "TR" for trains, "BS" for buses
     * @return List of bookings for that transport type
     */
    public List<Booking> findBookingsByTransportType(String transportType) {
        if (transportType == null || transportType.trim().isEmpty()) {
            System.out.println("✗ Service Error: Transport type cannot be null or empty");
            return new ArrayList<>();
        }

        List<Booking> allBookings = getAllBookings();
        return allBookings.stream()
                .filter(booking -> booking.getBookingId().startsWith(transportType.toUpperCase()))
                .collect(Collectors.toList());
    }

    public List<Booking> findBookingsByClientEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("✗ Service Error: Transport type cannot be null or empty");
            return new ArrayList<>();
        }

        List<Booking> allBookings = getAllBookings();
        return allBookings.stream()
                .filter(booking -> booking.getClientEmail().startsWith(email.toUpperCase()))
                .collect(Collectors.toList());
    }

    public List<Booking> findBookingsByClientPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: Transport type cannot be null or empty");
            return new ArrayList<>();
        }

        List<Booking> allBookings = getAllBookings();
        return allBookings.stream()
                .filter(booking -> booking.getClientPhone().startsWith(phoneNumber.toUpperCase()))
                .collect(Collectors.toList());
    }

    /**
     * Search train stations by multiple criteria (for advanced search functionality)
     * @param searchTerm The search term to match against station name, code, or city
     * @return List of train stations matching the search term
     */
    public List<Booking> searchTrainStations(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        List<Booking> allBookings = getAllBookings();

        return allBookings.stream()
                .filter(station ->
                        station.getBookingId().toLowerCase().contains(lowerSearchTerm) ||
                                station.getTransportDetailsJson().toLowerCase().contains(lowerSearchTerm) ||
                                station.getClientName().toLowerCase().contains(lowerSearchTerm) ||
                                station.getClientEmail().toLowerCase().contains(lowerSearchTerm) ||
                                station.getClientPhone().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Get booking statistics by transport type
     * @return String with booking statistics
     */
    public String getBookingStatistics() {
        List<Booking> flightBookings = findBookingsByTransportType("FL");
        List<Booking> trainBookings = findBookingsByTransportType("TR");
        List<Booking> busBookings = findBookingsByTransportType("BS");

        return String.format("Booking Statistics: Flights: %d, Trains: %d, Buses: %d, Total: %d",
                flightBookings.size(), trainBookings.size(), busBookings.size(), getBookingCount());
    }

    /**
     * Validate booking data before operations
     * @param booking The booking to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidBooking(Booking booking) {
        if (booking == null) {
            System.out.println("✗ Service Error: Booking cannot be null");
            return false;
        }

        if (booking.getBookingId() == null || booking.getBookingId().trim().isEmpty()) {
            System.out.println("✗ Service Error: Booking ID is required");
            return false;
        }

        if (booking.getClientName() == null || booking.getClientName().trim().isEmpty()) {
            System.out.println("✗ Service Error: Client name is required");
            return false;
        }

        if (booking.getClientEmail() == null || booking.getClientEmail().trim().isEmpty()) {
            System.out.println("✗ Service Error: Client email is required");
            return false;
        }

        // Basic email validation
        if (!booking.getClientEmail().contains("@") || !booking.getClientEmail().contains(".")) {
            System.out.println("✗ Service Error: Invalid email format");
            return false;
        }

        if (booking.getTransportDetailsJson() == null || booking.getTransportDetailsJson().trim().isEmpty()) {
            System.out.println("✗ Service Error: Transport details are required");
            return false;
        }

        return true;
    }

    // =============================================================
    // PRIVATE UTILITY METHODS
    // =============================================================

    /**
     * Generate a unique booking ID with the specified prefix
     * Format: PREFIX + YYYYMMDD + HHMMSS + 3-digit random number
     * @param prefix Transport type prefix ("FL", "TR", "BS")
     * @return Unique booking ID string
     */
    private String generateBookingId(String prefix) {
        // Get current timestamp for uniqueness
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // Add random number for extra uniqueness
        Random random = new Random();
        int randomNum = random.nextInt(900) + 100; // 3-digit number (100-999)

        return prefix + timestamp + randomNum;
    }
}