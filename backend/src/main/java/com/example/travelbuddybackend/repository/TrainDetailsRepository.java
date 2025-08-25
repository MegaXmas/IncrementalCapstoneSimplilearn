package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.TrainDetails;
import com.example.travelbuddybackend.models.TrainStation;
import com.example.travelbuddybackend.service.TrainStationService;
import com.example.travelbuddybackend.service.ValidatorService;
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
    private final TrainStationService trainStationService;
    private final ValidatorService validatorService;

    public TrainDetailsRepository(JdbcTemplate jdbcTemplate,
                                  TrainStationService trainStationService,
                                  ValidatorService validatorService) {
        this.jdbcTemplate = jdbcTemplate;
        this.trainStationService = trainStationService;
        this.validatorService = validatorService;
    }

    private static class TrainDetailsRowMapper implements RowMapper<TrainDetails> {
        private final TrainStationService trainStationService;

        public TrainDetailsRowMapper(TrainStationService trainStationService) {
            this.trainStationService = trainStationService;
        }

        @Override
        public TrainDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            TrainDetails trainDetails = new TrainDetails();
            trainDetails.setId(rs.getInt("id"));
            trainDetails.setTrainNumber(rs.getString("trainNumber"));
            trainDetails.setTrainLine(rs.getString("trainLine"));

            Integer departureStationId = rs.getInt("trainDepartureStation");
            Integer arrivalStationId = rs.getInt("trainArrivalStation");

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
                    new TrainDetailsRowMapper(trainStationService));
            System.out.println("✓ Repository: Successfully retrieved " + trainDetails.size() + " train details");
            return trainDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving train details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<TrainDetails> findById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid train details ID: " + id);
            return Optional.empty();
        }

        try {
            List<TrainDetails> trainDetails = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details WHERE id = ?",
                    new TrainDetailsRowMapper(trainStationService), id);

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
            System.out.println("✗ Repository: Invalid train number: " + trainNumber);
            return Optional.empty();
        }

        try {
            List<TrainDetails> trainDetails = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details WHERE trainNumber = ?",
                    new TrainDetailsRowMapper(trainStationService), trainNumber);

            if (trainDetails.isEmpty()) {
                System.out.println("✗ Repository: Train details with trainNumber " + trainNumber + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found train details with trainNumber " + trainNumber);
                return Optional.of(trainDetails.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding train details with trainNumber: " + trainNumber + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<TrainDetails> findByStations(String departureStation, String arrivalStation) {
        if (departureStation == null || departureStation.trim().isEmpty() ||
                arrivalStation == null || arrivalStation.trim().isEmpty()) {
            System.out.println("✗ Repository: Both departure and arrival stations are required");
            return new ArrayList<>();
        }

        try {
            List<TrainStation> departureStations = trainStationService.findTrainStationsByPartialName(departureStation);
            List<TrainStation> arrivalStations = trainStationService.findTrainStationsByPartialName(arrivalStation);

            if (departureStations.isEmpty() || arrivalStations.isEmpty()) {
                System.out.println("✗ Repository: No matching stations found");
                return new ArrayList<>();
            }

            Integer departureStationId = departureStations.get(0).getId();
            Integer arrivalStationId = arrivalStations.get(0).getId();

            List<TrainDetails> trainDetails = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details " +
                            "WHERE trainDepartureStation = ? AND trainArrivalStation = ?",
                    new TrainDetailsRowMapper(trainStationService),
                    departureStationId, arrivalStationId);

            System.out.println("✓ Repository: Found " + trainDetails.size() + " trains for route");
            return trainDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding trains: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<TrainDetails> findByStations(TrainStation departureStation, TrainStation arrivalStation) {
        if (!trainStationService.isValidTrainStation(departureStation) ||
                !trainStationService.isValidTrainStation(arrivalStation)) {
            System.out.println("✗ Repository: Invalid station objects");
            return new ArrayList<>();
        }

        try {
            List<TrainDetails> trainDetails = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details " +
                            "WHERE trainDepartureStation = ? AND trainArrivalStation = ?",
                    new TrainDetailsRowMapper(trainStationService),
                    departureStation.getId(), arrivalStation.getId());

            System.out.println("✓ Repository: Found " + trainDetails.size() + " trains between stations");
            return trainDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding trains between stations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<TrainDetails> findByDepartureDate(String departureDate) {
        if (!validatorService.isValidDate(departureDate)) {
            System.out.println("✗ Repository: Invalid departure date");
            return new ArrayList<>();
        }

        try {
            List<TrainDetails> trainDetails = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details WHERE trainDepartureDate = ?",
                    new TrainDetailsRowMapper(trainStationService), departureDate);

            System.out.println("✓ Repository: Found " + trainDetails.size() + " trains for departure date " + departureDate);
            return trainDetails;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding trains by departure date: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean createTrainDetails(TrainDetails trainDetails) {
        if (!isValidTrainDetailsForRepository(trainDetails)) {
            return false;
        }

        try {
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
                    departureStationId,
                    arrivalStationId,
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
        if (!isValidTrainDetailsForRepository(trainDetails)) {
            return false;
        }

        try {
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
                    departureStationId,
                    arrivalStationId,
                    trainDetails.getTrainDepartureDate(),
                    trainDetails.getTrainDepartureTime(),
                    trainDetails.getTrainArrivalDate(),
                    trainDetails.getTrainArrivalTime(),
                    trainDetails.getTrainRideDuration(),
                    trainDetails.getTrainRidePrice(),
                    trainDetails.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Train details updated: " + trainDetails.getTrainNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to update train details");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating train details: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTrainDetails(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Invalid train details ID: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM train_details WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Train details deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Train details not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting train details: " + e.getMessage());
            return false;
        }
    }

    // Repository-level validation (only checks data constraints, not business rules)
    private boolean isValidTrainDetailsForRepository(TrainDetails trainDetails) {
        if (trainDetails == null) {
            System.out.println("✗ Repository: Train details cannot be null");
            return false;
        }

        if (trainDetails.getTrainNumber() == null || trainDetails.getTrainNumber().trim().isEmpty()) {
            System.out.println("✗ Repository: Train number is required");
            return false;
        }

        if (trainDetails.getTrainLine() == null || trainDetails.getTrainLine().trim().isEmpty()) {
            System.out.println("✗ Repository: Train line is required");
            return false;
        }

        if (trainDetails.getTrainDepartureStation() == null ||
                !trainStationService.isValidTrainStation(trainDetails.getTrainDepartureStation())) {
            System.out.println("✗ Repository: Valid departure station is required");
            return false;
        }

        if (trainDetails.getTrainArrivalStation() == null ||
                !trainStationService.isValidTrainStation(trainDetails.getTrainArrivalStation())) {
            System.out.println("✗ Repository: Valid arrival station is required");
            return false;
        }

        if (trainDetails.getTrainDepartureDate() == null || trainDetails.getTrainDepartureDate().trim().isEmpty()) {
            System.out.println("✗ Repository: Departure date is required");
            return false;
        }

        if (trainDetails.getTrainDepartureTime() == null || trainDetails.getTrainDepartureTime().trim().isEmpty()) {
            System.out.println("✗ Repository: Departure time is required");
            return false;
        }

        if (trainDetails.getTrainRidePrice() == null || trainDetails.getTrainRidePrice().trim().isEmpty()) {
            System.out.println("✗ Repository: Price is required");
            return false;
        }

        return true;
    }
}