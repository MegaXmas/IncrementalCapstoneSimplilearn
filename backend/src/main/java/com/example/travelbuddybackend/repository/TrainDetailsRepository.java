package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.TrainDetails;
import com.example.travelbuddybackend.models.TrainStation;
import com.example.travelbuddybackend.service.TrainStationService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.travelbuddybackend.repository.TrainStationRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainDetailsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TrainStationService trainStationService;

    public TrainDetailsRepository(JdbcTemplate jdbcTemplate, TrainStationService trainStationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.trainStationService = trainStationService;
    }

    // Make RowMapper non-static so it can access trainStationService
    private class TrainDetailsRowMapper implements RowMapper<TrainDetails> {
        @Override
        public TrainDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            TrainDetails trainDetails = new TrainDetails();
            trainDetails.setId(rs.getInt("id"));
            trainDetails.setTrainNumber(rs.getString("trainNumber"));
            trainDetails.setTrainLine(rs.getString("trainLine"));

            // Get train station IDs from database
            Integer departureStationId = rs.getInt("trainDepartureStation");
            Integer arrivalStationId = rs.getInt("trainArrivalStation");

            // Fetch complete TrainStation objects using the service
            Optional<TrainStation> departureStation = trainStationService.getTrainStationById(departureStationId);
            Optional<TrainStation> arrivalStation = trainStationService.getTrainStationById(arrivalStationId);

            trainDetails.setTrainDepartureStation(departureStation.orElse(null));
            trainDetails.setTrainArrivalStation(arrivalStation.orElse(null));

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
                    new TrainDetailsRowMapper()); // No parameter needed now
            System.out.println("✓ Repository: Successfully retrieved " + trainDetails.size() + " train details");
            return trainDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving train details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<TrainDetails> findByStations(String departureStation, String arrivalStation) {
        if (departureStation == null || departureStation.trim().isEmpty() ||
                arrivalStation == null || arrivalStation.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Both departure and arrival stations are required");
            return new ArrayList<>();
        }

        try {
            // This method now needs to be updated to work with station names/codes
            // You could search for stations by name first, then use their IDs
            List<TrainStation> departureStations = trainStationService.findTrainStationsByPartialName(departureStation);
            List<TrainStation> arrivalStations = trainStationService.findTrainStationsByPartialName(arrivalStation);

            if (departureStations.isEmpty() || arrivalStations.isEmpty()) {
                System.out.println("✗ Repository: No matching stations found");
                return new ArrayList<>();
            }

            // For simplicity, let's take the first matching station for each
            Integer departureStationId = departureStations.get(0).getId();
            Integer arrivalStationId = arrivalStations.get(0).getId();

            List<TrainDetails> trainDetails = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details WHERE trainDepartureStation = ? AND trainArrivalStation = ?",
                    new TrainDetailsRowMapper(), departureStationId, arrivalStationId);

            System.out.println("✓ Repository: Found " + trainDetails.size() + " trains from " + departureStation + " to " + arrivalStation);
            return trainDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding trains from " + departureStation + " to " + arrivalStation + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean createTrainDetails(TrainDetails trainDetails) {
        if (!isValidTrainDetails(trainDetails)) {
            return false;
        }

        try {
            // Extract station IDs for database storage
            Integer departureStationId = trainDetails.getTrainDepartureStation() != null ?
                    trainDetails.getTrainDepartureStation().getId() : null;
            Integer arrivalStationId = trainDetails.getTrainArrivalStation() != null ?
                    trainDetails.getTrainArrivalStation().getId() : null;

            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO train_details (trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    trainDetails.getTrainNumber(),
                    trainDetails.getTrainLine(),
                    departureStationId,  // Use station ID instead of TrainStation object
                    arrivalStationId,    // Use station ID instead of TrainStation object
                    trainDetails.getTrainDepartureDate(),
                    trainDetails.getTrainDepartureTime(),
                    trainDetails.getTrainArrivalDate(),
                    trainDetails.getTrainArrivalTime(),
                    trainDetails.getTrainRideDuration(),
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
        if (!isValidTrainDetails(trainDetails)) {
            return false;
        }

        try {
            // Extract station IDs for database storage
            Integer departureStationId = trainDetails.getTrainDepartureStation() != null ?
                    trainDetails.getTrainDepartureStation().getId() : null;
            Integer arrivalStationId = trainDetails.getTrainArrivalStation() != null ?
                    trainDetails.getTrainArrivalStation().getId() : null;

            int rowsAffected = jdbcTemplate.update(
                    "UPDATE train_details SET trainNumber = ?, trainLine = ?, trainDepartureStation = ?, " +
                            "trainArrivalStation = ?, trainDepartureDate = ?, trainDepartureTime = ?, " +
                            "trainArrivalDate = ?, trainArrivalTime = ?, trainRideDuration = ?, trainRidePrice = ? " +
                            "WHERE id = ?",
                    trainDetails.getTrainNumber(),
                    trainDetails.getTrainLine(),
                    departureStationId,  // Use station ID instead of TrainStation object
                    arrivalStationId,    // Use station ID instead of TrainStation object
                    trainDetails.getTrainDepartureDate(),
                    trainDetails.getTrainDepartureTime(),
                    trainDetails.getTrainArrivalDate(),
                    trainDetails.getTrainArrivalTime(),
                    trainDetails.getTrainRideDuration(),
                    trainDetails.getTrainRidePrice(),
                    trainDetails.getId());  // ADD this missing parameter

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

    //==========================Validation=======================

    public boolean isValidTrainDetails(TrainDetails trainDetails) {
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

        if (trainDetails.getTrainDepartureStation() == null ||
                !trainStationService.isValidTrainStation(trainDetails.getTrainDepartureStation())) {
            System.out.println("✗ Repository: Error: Train departure station is required or invalid");
            return false;
        }

        if (trainDetails.getTrainArrivalStation() == null ||
                !trainStationService.isValidTrainStation(trainDetails.getTrainArrivalStation())) {
            System.out.println("✗ Repository: Error: Train arrival station is required or invalid");
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
        return true;
    }
}