package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.TrainDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainDetailsRepository {

    private final JdbcTemplate jdbcTemplate;

    public TrainDetailsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class TrainDetailsRowMapper implements RowMapper<TrainDetails> {
        @Override
        public TrainDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            TrainDetails trainDetails = new TrainDetails();
            trainDetails.setId(rs.getInt("id"));
            trainDetails.setTrainNumber(rs.getString("trainNumber"));
            trainDetails.setTrainLine(rs.getString("trainLine"));
            trainDetails.setTrainDepartureStation(rs.getString("trainDepartureStation"));
            trainDetails.setTrainArrivalStation(rs.getString("trainArrivalStation"));
            trainDetails.setTrainDepartureDate(rs.getString("trainDepartureDate"));
            trainDetails.setTrainDepartureTime(rs.getString("trainDepartureTime"));
            trainDetails.setTrainArrivalDate(rs.getString("trainArrivalDate"));
            trainDetails.setTrainArrivalTime(rs.getString("trainArrivalTime"));
            trainDetails.setTrainRideDuration(rs.getString("trainRideDuration"));
            trainDetails.setTrainRidePrice(rs.getString("trainRidePrice"));
            return trainDetails;
        }
    }

    public List<TrainDetails> findAll() {
        try {
            List<TrainDetails> trainDetails = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details",
                    new TrainDetailsRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + trainDetails.size() + " train details");
            return trainDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving train details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<TrainDetails> findById(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid train details ID: " + id);
            return Optional.empty();
        }

        try {
            List<TrainDetails> trainDetails = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details WHERE id = ?",
                    new TrainDetailsRowMapper(), id);

            if (trainDetails.isEmpty()) {
                System.out.println("✗ Repository: Train details with ID " + id + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found train details with ID " + id);
                return Optional.of(trainDetails.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding train details with ID: " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<TrainDetails> findByTrainNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Invalid train number: " + trainNumber);
            return Optional.empty();
        }

        try {
            List<TrainDetails> trainDetails = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details WHERE trainNumber = ?",
                    new TrainDetailsRowMapper(), trainNumber);

            if (trainDetails.isEmpty()) {
                System.out.println("✗ Repository: Train details with number " + trainNumber + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found train details with number " + trainNumber);
                return Optional.of(trainDetails.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding train details with number: " + trainNumber + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<TrainDetails> findByStations(String departureStation, String arrivalStation) {
        if (departureStation == null || departureStation.trim().isEmpty() ||
                arrivalStation == null || arrivalStation.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Both departure and arrival stations are required");
            return new ArrayList<>();
        }

        try {
            List<TrainDetails> trainDetails = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details WHERE trainDepartureStation = ? AND trainArrivalStation = ?",
                    new TrainDetailsRowMapper(), departureStation, arrivalStation);

            System.out.println("✓ Repository: Found " + trainDetails.size() + " trains from " + departureStation + " to " + arrivalStation);
            return trainDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding trains from " + departureStation + " to " + arrivalStation + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean createTrainDetails(TrainDetails trainDetails) {
        if (trainDetails == null) {
            System.out.println("✗ Repository: Error: Cannot create null train details");
            return false;
        }

        // Validate all required fields
        if (trainDetails.getTrainNumber() == null || trainDetails.getTrainNumber().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train number is required");
            return false;
        }

        if (trainDetails.getTrainLine() == null || trainDetails.getTrainLine().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train line is required");
            return false;
        }

        if (trainDetails.getTrainDepartureStation() == null || trainDetails.getTrainDepartureStation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train departure station is required");
            return false;
        }

        if (trainDetails.getTrainArrivalStation() == null || trainDetails.getTrainArrivalStation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train arrival station is required");
            return false;
        }

        if (trainDetails.getTrainDepartureDate() == null || trainDetails.getTrainDepartureDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train departure date is required");
            return false;
        }

        if (trainDetails.getTrainDepartureTime() == null || trainDetails.getTrainDepartureTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train departure time is required");
            return false;
        }

        if (trainDetails.getTrainArrivalDate() == null || trainDetails.getTrainArrivalDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train arrival date is required");
            return false;
        }

        if (trainDetails.getTrainArrivalTime() == null || trainDetails.getTrainArrivalTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train arrival time is required");
            return false;
        }

        if (trainDetails.getTrainRideDuration() == null || trainDetails.getTrainRideDuration().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train ride duration is required");
            return false;
        }

        if (trainDetails.getTrainRidePrice() == null || trainDetails.getTrainRidePrice().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train ride price is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO train_details (trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    trainDetails.getTrainNumber(), trainDetails.getTrainLine(), trainDetails.getTrainDepartureStation(),
                    trainDetails.getTrainArrivalStation(), trainDetails.getTrainDepartureDate(), trainDetails.getTrainDepartureTime(),
                    trainDetails.getTrainArrivalDate(), trainDetails.getTrainArrivalTime(), trainDetails.getTrainRideDuration(),
                    trainDetails.getTrainRidePrice());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: New train details created: " + trainDetails.getTrainNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create train details");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating train details: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTrainDetails(TrainDetails trainDetails) {
        if (trainDetails == null) {
            System.out.println("✗ Repository: Error: Cannot update null train details");
            return false;
        }

        if (trainDetails.getId() <= 0) {
            System.out.println("✗ Repository: Error: Invalid train details ID " + trainDetails.getId());
            return false;
        }

        // Validate all required fields
        if (trainDetails.getTrainNumber() == null || trainDetails.getTrainNumber().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train number is required");
            return false;
        }

        if (trainDetails.getTrainLine() == null || trainDetails.getTrainLine().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train line is required");
            return false;
        }

        if (trainDetails.getTrainDepartureStation() == null || trainDetails.getTrainDepartureStation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train departure station is required");
            return false;
        }

        if (trainDetails.getTrainArrivalStation() == null || trainDetails.getTrainArrivalStation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train arrival station is required");
            return false;
        }

        if (trainDetails.getTrainDepartureDate() == null || trainDetails.getTrainDepartureDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train departure date is required");
            return false;
        }

        if (trainDetails.getTrainDepartureTime() == null || trainDetails.getTrainDepartureTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train departure time is required");
            return false;
        }

        if (trainDetails.getTrainArrivalDate() == null || trainDetails.getTrainArrivalDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train arrival date is required");
            return false;
        }

        if (trainDetails.getTrainArrivalTime() == null || trainDetails.getTrainArrivalTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train arrival time is required");
            return false;
        }

        if (trainDetails.getTrainRideDuration() == null || trainDetails.getTrainRideDuration().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train ride duration is required");
            return false;
        }

        if (trainDetails.getTrainRidePrice() == null || trainDetails.getTrainRidePrice().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train ride price is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE train_details SET trainNumber = ?, trainLine = ?, trainDepartureStation = ?, " +
                            "trainArrivalStation = ?, trainDepartureDate = ?, trainDepartureTime = ?, " +
                            "trainArrivalDate = ?, trainArrivalTime = ?, trainRideDuration = ?, trainRidePrice = ? " +
                            "WHERE  id = ?",
                    trainDetails.getTrainNumber(), trainDetails.getTrainLine(), trainDetails.getTrainDepartureStation(),
                    trainDetails.getTrainArrivalStation(), trainDetails.getTrainDepartureDate(), trainDetails.getTrainDepartureTime(),
                    trainDetails.getTrainArrivalDate(), trainDetails.getTrainArrivalTime(), trainDetails.getTrainRideDuration(),
                    trainDetails.getTrainRidePrice());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Train details updated successfully: " + trainDetails.getTrainNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to update train details " + trainDetails.getTrainNumber());
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating train details: " + e.getMessage());
            return false;
        }
    }



    public boolean deleteTrainDetails(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid train details ID: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM train_details WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Train details with ID " + id + " deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Train details with ID " + id + " not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting train details: " + e.getMessage());
            return false;
        }
    }
}