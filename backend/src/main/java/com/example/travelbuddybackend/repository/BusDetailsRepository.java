package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.BusDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BusDetailsRepository {

    private final JdbcTemplate jdbcTemplate;

    public BusDetailsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class BusDetailsRowMapper implements RowMapper<BusDetails> {
        @Override
        public BusDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            BusDetails busDetails = new BusDetails();
            busDetails.setId(rs.getInt("id"));
            busDetails.setBusNumber(rs.getString("busNumber"));
            busDetails.setBusLine(rs.getString("busLine"));
            busDetails.setBusDepartureStation(rs.getString("busDepartureStation"));
            busDetails.setBusArrivalStation(rs.getString("busArrivalStation"));
            busDetails.setBusDepartureDate(rs.getString("busDepartureDate"));
            busDetails.setBusDepartureTime(rs.getString("busDepartureTime"));
            busDetails.setBusArrivalDate(rs.getString("busArrivalDate"));
            busDetails.setBusArrivalTime(rs.getString("busArrivalTime"));
            busDetails.setBusRideDuration(rs.getString("busRideDuration"));
            busDetails.setBusRidePrice(rs.getString("busRidePrice"));
            return busDetails;
        }
    }

    public List<BusDetails> findAll() {
        try {
            List<BusDetails> busDetails = jdbcTemplate.query(
                    "SELECT id, busNumber, busLine, busDepartureStation, busArrivalStation, " +
                            "busDepartureDate, busDepartureTime, busArrivalDate, busArrivalTime, " +
                            "busRideDuration, busRidePrice FROM bus_details",
                    new BusDetailsRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + busDetails.size() + " bus details");
            return busDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving bus details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<BusDetails> findById(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid bus details ID: " + id);
            return Optional.empty();
        }

        try {
            List<BusDetails> busDetails = jdbcTemplate.query(
                    "SELECT id, busNumber, busLine, busDepartureStation, busArrivalStation, " +
                            "busDepartureDate, busDepartureTime, busArrivalDate, busArrivalTime, " +
                            "busRideDuration, busRidePrice FROM bus_details WHERE id = ?",
                    new BusDetailsRowMapper(), id);

            if (busDetails.isEmpty()) {
                System.out.println("✗ Repository: Bus details with ID " + id + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found bus details with ID " + id);
                return Optional.of(busDetails.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding bus details with ID: " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<BusDetails> findByBusNumber(String busNumber) {
        if (busNumber == null || busNumber.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Invalid bus number: " + busNumber);
            return Optional.empty();
        }

        try {
            List<BusDetails> busDetails = jdbcTemplate.query(
                    "SELECT id, busNumber, busLine, busDepartureStation, busArrivalStation, " +
                            "busDepartureDate, busDepartureTime, busArrivalDate, busArrivalTime, " +
                            "busRideDuration, busRidePrice FROM bus_details WHERE busNumber = ?",
                    new BusDetailsRowMapper(), busNumber);

            if (busDetails.isEmpty()) {
                System.out.println("✗ Repository: Bus details with number " + busNumber + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found bus details with number " + busNumber);
                return Optional.of(busDetails.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding bus details with number: " + busNumber + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean createBusDetails(BusDetails busDetails) {
        if (busDetails == null) {
            System.out.println("✗ Repository: Error: Cannot create null bus details");
            return false;
        }

        // Validate all required fields
        if (busDetails.getBusNumber() == null || busDetails.getBusNumber().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus number is required");
            return false;
        }

        if (busDetails.getBusLine() == null || busDetails.getBusLine().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus line is required");
            return false;
        }

        if (busDetails.getBusDepartureStation() == null || busDetails.getBusDepartureStation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus departure station is required");
            return false;
        }

        if (busDetails.getBusArrivalStation() == null || busDetails.getBusArrivalStation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus arrival station is required");
            return false;
        }

        if (busDetails.getBusDepartureDate() == null || busDetails.getBusDepartureDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus departure date is required");
            return false;
        }

        if (busDetails.getBusDepartureTime() == null || busDetails.getBusDepartureTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus departure time is required");
            return false;
        }

        if (busDetails.getBusArrivalDate() == null || busDetails.getBusArrivalDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus arrival date is required");
            return false;
        }

        if (busDetails.getBusArrivalTime() == null || busDetails.getBusArrivalTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus arrival time is required");
            return false;
        }

        if (busDetails.getBusRideDuration() == null || busDetails.getBusRideDuration().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus ride duration is required");
            return false;
        }

        if (busDetails.getBusRidePrice() == null || busDetails.getBusRidePrice().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus ride price is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO bus_details (busNumber, busLine, busDepartureStation, busArrivalStation, " +
                            "busDepartureDate, busDepartureTime, busArrivalDate, busArrivalTime, " +
                            "busRideDuration, busRidePrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    busDetails.getBusNumber(), busDetails.getBusLine(), busDetails.getBusDepartureStation(),
                    busDetails.getBusArrivalStation(), busDetails.getBusDepartureDate(), busDetails.getBusDepartureTime(),
                    busDetails.getBusArrivalDate(), busDetails.getBusArrivalTime(), busDetails.getBusRideDuration(),
                    busDetails.getBusRidePrice());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: New bus details created: " + busDetails.getBusNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create bus details");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating bus details: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBusDetails(BusDetails busDetails) {
        if (busDetails == null) {
            System.out.println("✗ Repository: Error: Cannot update null bus details");
            return false;
        }

        if (busDetails.getId() <= 0) {
            System.out.println("✗ Repository: Error: Invalid bus details ID " + busDetails.getId());
            return false;
        }

        // Validate all required fields
        if (busDetails.getBusNumber() == null || busDetails.getBusNumber().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus number is required");
            return false;
        }

        if (busDetails.getBusLine() == null || busDetails.getBusLine().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus line is required");
            return false;
        }

        if (busDetails.getBusDepartureStation() == null || busDetails.getBusDepartureStation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus departure station is required");
            return false;
        }

        if (busDetails.getBusArrivalStation() == null || busDetails.getBusArrivalStation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus arrival station is required");
            return false;
        }

        if (busDetails.getBusDepartureDate() == null || busDetails.getBusDepartureDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus departure date is required");
            return false;
        }

        if (busDetails.getBusDepartureTime() == null || busDetails.getBusDepartureTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus departure time is required");
            return false;
        }

        if (busDetails.getBusArrivalDate() == null || busDetails.getBusArrivalDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus arrival date is required");
            return false;
        }

        if (busDetails.getBusArrivalTime() == null || busDetails.getBusArrivalTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus arrival time is required");
            return false;
        }

        if (busDetails.getBusRideDuration() == null || busDetails.getBusRideDuration().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus ride duration is required");
            return false;
        }

        if (busDetails.getBusRidePrice() == null || busDetails.getBusRidePrice().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus ride price is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE bus_details SET busNumber = ?, busLine = ?, busDepartureStation = ?, " +
                            "busArrivalStation = ?, busDepartureDate = ?, busDepartureTime = ?, " +
                            "busArrivalDate = ?, busArrivalTime = ?, busRideDuration = ?, busRidePrice = ? " +
                            "WHERE id = ?",
                    busDetails.getBusNumber(), busDetails.getBusLine(), busDetails.getBusDepartureStation(),
                    busDetails.getBusArrivalStation(), busDetails.getBusDepartureDate(), busDetails.getBusDepartureTime(),
                    busDetails.getBusArrivalDate(), busDetails.getBusArrivalTime(), busDetails.getBusRideDuration(),
                    busDetails.getBusRidePrice(), busDetails.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Bus details updated successfully: " + busDetails.getBusNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to update bus details " + busDetails.getBusNumber());
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating bus details: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBusDetails(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid bus details ID: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM bus_details WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Bus details with ID " + id + " deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Bus details with ID " + id + " not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting bus details: " + e.getMessage());
            return false;
        }
    }
}