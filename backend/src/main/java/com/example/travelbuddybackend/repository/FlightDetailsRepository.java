package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.FlightDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FlightDetailsRepository {

    private final JdbcTemplate jdbcTemplate;

    public FlightDetailsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class FlightDetailsRowMapper implements RowMapper<FlightDetails> {
        @Override
        public FlightDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            FlightDetails flightDetails = new FlightDetails();
            flightDetails.setId(rs.getInt("id"));
            flightDetails.setFlightNumber(rs.getString("flightNumber"));
            flightDetails.setFlightAirline(rs.getString("flightAirline"));
            flightDetails.setFlightOrigin(rs.getString("flightOrigin"));
            flightDetails.setFlightDestination(rs.getString("flightDestination"));
            flightDetails.setFlightDepartureDate(rs.getString("flightDepartureDate"));
            flightDetails.setFlightArrivalDate(rs.getString("flightArrivalDate"));
            flightDetails.setFlightDepartureTime(rs.getString("flightDepartureTime"));
            flightDetails.setFlightArrivalTime(rs.getString("flightArrivalTime"));
            flightDetails.setFlightTravelTime(rs.getString("flightTravelTime"));
            flightDetails.setFlightPrice(rs.getString("flightPrice"));
            return flightDetails;
        }
    }

    public List<FlightDetails> findAll() {
        try {
            List<FlightDetails> flightDetails = jdbcTemplate.query(
                    "SELECT id, flightNumber, flightAirline, flightOrigin, flightDestination, " +
                            "flightDepartureDate, flightArrivalDate, flightDepartureTime, flightArrivalTime, " +
                            "flightTravelTime, flightPrice FROM flight_details",
                    new FlightDetailsRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + flightDetails.size() + " flight details");
            return flightDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving flight details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<FlightDetails> findById(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid flight details ID: " + id);
            return Optional.empty();
        }

        try {
            List<FlightDetails> flightDetails = jdbcTemplate.query(
                    "SELECT id, flightNumber, flightAirline, flightOrigin, flightDestination, " +
                            "flightDepartureDate, flightArrivalDate, flightDepartureTime, flightArrivalTime, " +
                            "flightTravelTime, flightPrice FROM flight_details WHERE id = ?",
                    new FlightDetailsRowMapper(), id);

            if (flightDetails.isEmpty()) {
                System.out.println("✗ Repository: Flight details with ID " + id + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found flight details with ID " + id);
                return Optional.of(flightDetails.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding flight details with ID: " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<FlightDetails> findByFlightNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Invalid flight number: " + flightNumber);
            return Optional.empty();
        }

        try {
            List<FlightDetails> flightDetails = jdbcTemplate.query(
                    "SELECT id, flightNumber, flightAirline, flightOrigin, flightDestination, " +
                            "flightDepartureDate, flightArrivalDate, flightDepartureTime, flightArrivalTime, " +
                            "flightTravelTime, flightPrice FROM flight_details WHERE flightNumber = ?",
                    new FlightDetailsRowMapper(), flightNumber);

            if (flightDetails.isEmpty()) {
                System.out.println("✗ Repository: Flight details with number " + flightNumber + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found flight details with number " + flightNumber);
                return Optional.of(flightDetails.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding flight details with number: " + flightNumber + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<FlightDetails> findByOriginAndDestination(String origin, String destination) {
        if (origin == null || origin.trim().isEmpty() || destination == null || destination.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Both origin and destination are required");
            return new ArrayList<>();
        }

        try {
            List<FlightDetails> flightDetails = jdbcTemplate.query(
                    "SELECT id, flightNumber, flightAirline, flightOrigin, flightDestination, " +
                            "flightDepartureDate, flightArrivalDate, flightDepartureTime, flightArrivalTime, " +
                            "flightTravelTime, flightPrice FROM flight_details WHERE flightOrigin = ? AND flightDestination = ?",
                    new FlightDetailsRowMapper(), origin, destination);

            System.out.println("✓ Repository: Found " + flightDetails.size() + " flights from " + origin + " to " + destination);
            return flightDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding flights from " + origin + " to " + destination + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean createFlightDetails(FlightDetails flightDetails) {
        if (flightDetails == null) {
            System.out.println("✗ Repository: Error: Cannot create null flight details");
            return false;
        }

        // Validate required fields
        if (flightDetails.getFlightNumber() == null || flightDetails.getFlightNumber().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight number is required");
            return false;
        }

        if (flightDetails.getFlightAirline() == null || flightDetails.getFlightAirline().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight airline is required");
            return false;
        }

        if (flightDetails.getFlightOrigin() == null || flightDetails.getFlightOrigin().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight origin is required");
            return false;
        }

        if (flightDetails.getFlightDestination() == null || flightDetails.getFlightDestination().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight destination is required");
            return false;
        }

        if (flightDetails.getFlightDepartureDate() == null || flightDetails.getFlightDepartureDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight departure date is required");
            return false;
        }

        if (flightDetails.getFlightArrivalDate() == null || flightDetails.getFlightArrivalDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight arrival date is required");
            return false;
        }

        if (flightDetails.getFlightDepartureTime() == null || flightDetails.getFlightDepartureTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight departure time is required");
            return false;
        }

        if (flightDetails.getFlightArrivalTime() == null || flightDetails.getFlightArrivalTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight arrival time is required");
            return false;
        }

        if (flightDetails.getFlightPrice() == null || flightDetails.getFlightPrice().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight price is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO flight_details (flightNumber, flightAirline, flightOrigin, " +
                            "flightDestination, flightDepartureDate, flightArrivalDate, flightDepartureTime, " +
                            "flightArrivalTime, flightPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    flightDetails.getFlightNumber(), flightDetails.getFlightAirline(), flightDetails.getFlightOrigin(),
                    flightDetails.getFlightDestination(), flightDetails.getFlightDepartureDate(), flightDetails.getFlightArrivalDate(),
                    flightDetails.getFlightDepartureTime(), flightDetails.getFlightArrivalTime(), flightDetails.getFlightTravelTime(),
                    flightDetails.getFlightPrice(), flightDetails.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: New bus details created: " + flightDetails.getFlightNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create flight details");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating flight details: " + e.getMessage());
            return false;
        }
    }


    public boolean updateFlightDetails(FlightDetails flightDetails) {
        if (flightDetails == null) {
            System.out.println("✗ Repository: Error: Cannot update null flight details");
            return false;
        }

        if (flightDetails.getId() <= 0) {
            System.out.println("✗ Repository: Error: Invalid flight details ID " + flightDetails.getId());
            return false;
        }

        // Validate all required fields
        if (flightDetails.getFlightNumber() == null || flightDetails.getFlightNumber().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight number is required");
            return false;
        }

        if (flightDetails.getFlightAirline() == null || flightDetails.getFlightAirline().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight airline is required");
            return false;
        }

        if (flightDetails.getFlightOrigin() == null || flightDetails.getFlightOrigin().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight origin is required");
            return false;
        }

        if (flightDetails.getFlightDestination() == null || flightDetails.getFlightDestination().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight destination is required");
            return false;
        }

        if (flightDetails.getFlightDepartureDate() == null || flightDetails.getFlightDepartureDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight departure date is required");
            return false;
        }

        if (flightDetails.getFlightArrivalDate() == null || flightDetails.getFlightArrivalDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight arrival date is required");
            return false;
        }

        if (flightDetails.getFlightDepartureTime() == null || flightDetails.getFlightDepartureTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight departure time is required");
            return false;
        }

        if (flightDetails.getFlightArrivalTime() == null || flightDetails.getFlightArrivalTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight arrival time is required");
            return false;
        }

        if (flightDetails.getFlightTravelTime() == null || flightDetails.getFlightTravelTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight travel time is required");
            return false;
        }

        if (flightDetails.getFlightPrice() == null || flightDetails.getFlightPrice().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Flight price is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE flight_details SET flightNumber = ?, flightAirline = ?, flightOrigin = ?, " +
                            "flightDestination = ?, flightDepartureDate = ?, flightArrivalDate = ?, " +
                            "flightDepartureTime = ?, flightArrivalTime = ?, flightTravelTime = ?, flightPrice = ? " +
                            "WHERE id = ?",
                    flightDetails.getFlightNumber(), flightDetails.getFlightAirline(), flightDetails.getFlightOrigin(),
                    flightDetails.getFlightDestination(), flightDetails.getFlightDepartureDate(), flightDetails.getFlightArrivalDate(),
                    flightDetails.getFlightDepartureTime(), flightDetails.getFlightArrivalTime(), flightDetails.getFlightTravelTime(),
                    flightDetails.getFlightPrice(), flightDetails.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Flight details updated successfully: " + flightDetails.getFlightNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to update flight details " + flightDetails.getFlightNumber());
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating flight details: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteFlightDetails(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid flight details ID: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM flight_details WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Flight details with ID " + id + " deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Flight details with ID " + id + " not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting flight details: " + e.getMessage());
            return false;
        }
    }
}
