package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.Booking;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//UNFINISHED IMPLEMENTATION
@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class BookingRowMapper implements RowMapper<Booking> {
        @Override
        public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
            Booking booking = new Booking();
            booking.setId(rs.getInt("id"));
            booking.setBookingId(rs.getString("bookingId"));
            booking.setTransportDetailsJson(rs.getString("transportDetailsJson"));
            booking.setClientName(rs.getString("clientName"));
            booking.setClientEmail(rs.getString("clientEmail"));
            booking.setClientPhone(rs.getString("clientPhone"));
            return booking;
        }
    }

    public List<Booking> findAll() {
        try {
            List<Booking> bookings = jdbcTemplate.query(
                    "SELECT id, bookingId, transportDetailsJson, clientName, clientEmail, clientPhone FROM bookings",
                    new BookingRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + bookings.size() + " bookings");
            return bookings;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving bookings: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<Booking> findById(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid booking ID: " + id);
            return Optional.empty();
        }

        try {
            List<Booking> bookings = jdbcTemplate.query(
                    "SELECT id, bookingId, transportDetailsJson, clientName, clientEmail, clientPhone FROM bookings WHERE id = ?",
                    new BookingRowMapper(), id);

            if (bookings.isEmpty()) {
                System.out.println("✗ Repository: Booking with ID " + id + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found booking with ID " + id);
                return Optional.of(bookings.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding booking with ID: " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Booking> findByBookingId(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Invalid booking ID: " + bookingId);
            return Optional.empty();
        }

        try {
            List<Booking> bookings = jdbcTemplate.query(
                    "SELECT id, bookingId, transportDetailsJson, clientName, clientEmail, clientPhone FROM bookings WHERE bookingId = ?",
                    new BookingRowMapper(), bookingId);

            if (bookings.isEmpty()) {
                System.out.println("✗ Repository: Booking with booking ID " + bookingId + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found booking: " + bookingId + " for client " + bookings.get(0).getClientName());
                return Optional.of(bookings.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding booking with booking ID: " + bookingId + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Booking> findByClientEmail(String clientEmail) {
        if (clientEmail == null || clientEmail.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Invalid client email: " + clientEmail);
            return new ArrayList<>();
        }

        try {
            List<Booking> bookings = jdbcTemplate.query(
                    "SELECT id, bookingId, transportDetailsJson, clientName, clientEmail, clientPhone FROM bookings WHERE clientEmail = ?",
                    new BookingRowMapper(), clientEmail);

            System.out.println("✓ Repository: Found " + bookings.size() + " bookings for email: " + clientEmail);
            return bookings;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding bookings for email " + clientEmail + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean createBooking(Booking booking) {
        if (booking == null) {
            System.out.println("✗ Repository: Error: Cannot create null booking");
            return false;
        }

        // Comprehensive validation - all fields are required for a complete booking
        if (booking.getBookingId() == null || booking.getBookingId().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Booking ID is required");
            return false;
        }

        if (booking.getTransportDetailsJson() == null || booking.getTransportDetailsJson().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Transport details JSON is required");
            return false;
        }

        if (booking.getClientName() == null || booking.getClientName().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Client name is required");
            return false;
        }

        if (booking.getClientEmail() == null || booking.getClientEmail().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Client email is required");
            return false;
        }

        if (booking.getClientPhone() == null || booking.getClientPhone().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Client phone is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO bookings (bookingId, transportDetailsJson, clientName, clientEmail, clientPhone) VALUES (?, ?, ?, ?, ?)",
                    booking.getBookingId(), booking.getTransportDetailsJson(), booking.getClientName(),
                    booking.getClientEmail(), booking.getClientPhone());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: New booking created: " + booking.getBookingId() + " for " + booking.getClientName());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create booking");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating booking: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBooking(Booking booking) {
        if (booking == null) {
            System.out.println("✗ Repository: Error: Cannot update null booking");
            return false;
        }

        if (booking.getId() <= 0) {
            System.out.println("✗ Repository: Error: Invalid booking ID " + booking.getId());
            return false;
        }

        // Same comprehensive validation for updates
        if (booking.getBookingId() == null || booking.getBookingId().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Booking ID is required");
            return false;
        }

        if (booking.getTransportDetailsJson() == null || booking.getTransportDetailsJson().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Transport details JSON is required");
            return false;
        }

        if (booking.getClientName() == null || booking.getClientName().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Client name is required");
            return false;
        }

        if (booking.getClientEmail() == null || booking.getClientEmail().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Client email is required");
            return false;
        }

        if (booking.getClientPhone() == null || booking.getClientPhone().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Client phone is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE bookings SET bookingId = ?, transportDetailsJson = ?, clientName = ?, clientEmail = ?, clientPhone = ? WHERE id = ?",
                    booking.getBookingId(), booking.getTransportDetailsJson(), booking.getClientName(),
                    booking.getClientEmail(), booking.getClientPhone(), booking.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Booking updated successfully: " + booking.getBookingId());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to update booking " + booking.getBookingId());
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating booking: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBooking(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid booking ID: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM bookings WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Booking with ID " + id + " deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Booking with ID " + id + " not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting booking: " + e.getMessage());
            return false;
        }
    }

    // Additional method: Cancel booking by bookingId (soft delete approach)
    public boolean cancelBooking(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Invalid booking ID: " + bookingId);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE bookings SET status = 'CANCELLED' WHERE bookingId = ?",
                    bookingId);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Booking " + bookingId + " cancelled successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Booking " + bookingId + " not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error cancelling booking: " + e.getMessage());
            return false;
        }
    }
}