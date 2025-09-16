package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.TrainDetails;
import com.example.travelbuddybackend.models.TrainStation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Train Details Repository - Pure Data Access Layer
 *
 * Fixed Architecture:
 * - No service dependencies (only repository dependencies)
 * - Optimized SQL with JOINs to eliminate N+1 queries
 * - Matches actual database column names
 * - Single responsibility: data access only
 */
@Repository
public class TrainDetailsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TrainStationRepository trainStationRepository;

    public TrainDetailsRepository(JdbcTemplate jdbcTemplate, TrainStationRepository trainStationRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.trainStationRepository = trainStationRepository;
    }

    private static class TrainDetailsRowMapper implements RowMapper<TrainDetails> {
        @Override
        public TrainDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            TrainDetails trainDetails = new TrainDetails();
            trainDetails.setId(rs.getInt("td_id"));
            trainDetails.setTrainNumber(rs.getString("td_trainNumber"));
            trainDetails.setTrainLine(rs.getString("td_trainLine"));
            trainDetails.setTrainDepartureDate(rs.getString("td_trainDepartureDate"));
            trainDetails.setTrainDepartureTime(rs.getString("td_trainDepartureTime"));
            trainDetails.setTrainArrivalDate(rs.getString("td_trainArrivalDate"));
            trainDetails.setTrainArrivalTime(rs.getString("td_trainArrivalTime"));
            trainDetails.setTrainRideDuration(rs.getString("td_trainRideDuration"));
            trainDetails.setTrainRidePrice(rs.getString("td_trainRidePrice"));

            // Create TrainStation objects from JOIN data - no additional queries needed
            TrainStation departureStation = new TrainStation();
            departureStation.setId(rs.getInt("dep_id"));
            departureStation.setTrainStationFullName(rs.getString("dep_full_name"));
            departureStation.setTrainStationCode(rs.getString("dep_code"));
            departureStation.setTrainStationCityLocation(rs.getString("dep_city"));
            trainDetails.setTrainDepartureStation(departureStation);

            TrainStation arrivalStation = new TrainStation();
            arrivalStation.setId(rs.getInt("arr_id"));
            arrivalStation.setTrainStationFullName(rs.getString("arr_full_name"));
            arrivalStation.setTrainStationCode(rs.getString("arr_code"));
            arrivalStation.setTrainStationCityLocation(rs.getString("arr_city"));
            trainDetails.setTrainArrivalStation(arrivalStation);

            return trainDetails;
        }
    }

    private static class SimpleTrainDetailsRowMapper implements RowMapper<TrainDetails> {
        @Override
        public TrainDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            TrainDetails trainDetails = new TrainDetails();
            trainDetails.setId(rs.getInt("id"));
            trainDetails.setTrainNumber(rs.getString("trainNumber"));
            trainDetails.setTrainLine(rs.getString("trainLine"));
            trainDetails.setTrainDepartureDate(rs.getString("trainDepartureDate"));
            trainDetails.setTrainDepartureTime(rs.getString("trainDepartureTime"));
            trainDetails.setTrainArrivalDate(rs.getString("trainArrivalDate"));
            trainDetails.setTrainArrivalTime(rs.getString("trainArrivalTime"));
            trainDetails.setTrainRideDuration(rs.getString("trainRideDuration"));
            trainDetails.setTrainRidePrice(rs.getString("trainRidePrice"));

            // Set station IDs only - stations loaded by service if needed
            Integer depId = rs.getInt("trainDepartureStation");
            Integer arrId = rs.getInt("trainArrivalStation");

            if (depId != 0) {
                TrainStation dep = new TrainStation();
                dep.setId(depId);
                trainDetails.setTrainDepartureStation(dep);
            }

            if (arrId != 0) {
                TrainStation arr = new TrainStation();
                arr.setId(arrId);
                trainDetails.setTrainArrivalStation(arr);
            }

            return trainDetails;
        }
    }

    public List<TrainDetails> findAll() {
        try {
            String sql = """
                SELECT 
                    td.id as td_id, 
                    td.trainNumber as td_trainNumber, 
                    td.trainLine as td_trainLine,
                    td.trainDepartureDate as td_trainDepartureDate,
                    td.trainDepartureTime as td_trainDepartureTime,
                    td.trainArrivalDate as td_trainArrivalDate,
                    td.trainArrivalTime as td_trainArrivalTime,
                    td.trainRideDuration as td_trainRideDuration,
                    td.trainRidePrice as td_trainRidePrice,
                    dep.id as dep_id, 
                    dep.trainStationFullName as dep_full_name,
                    dep.trainStationCode as dep_code, 
                    dep.trainStationCityLocation as dep_city,
                    arr.id as arr_id, 
                    arr.trainStationFullName as arr_full_name,
                    arr.trainStationCode as arr_code, 
                    arr.trainStationCityLocation as arr_city
                FROM train_details td
                LEFT JOIN train_stations dep ON td.trainDepartureStation = dep.trainStationCode
                LEFT JOIN train_stations arr ON td.trainArrivalStation = arr.trainStationCode
                """;

            List<TrainDetails> trains = jdbcTemplate.query(sql, new TrainDetailsRowMapper());
            System.out.println("✓ Repository: Retrieved " + trains.size() + " train details");
            return trains;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving train details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<TrainDetails> findById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid train ID: " + id);
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT 
                    td.id as td_id, 
                    td.trainNumber as td_trainNumber, 
                    td.trainLine as td_trainLine,
                    td.trainDepartureDate as td_trainDepartureDate,
                    td.trainDepartureTime as td_trainDepartureTime,
                    td.trainArrivalDate as td_trainArrivalDate,
                    td.trainArrivalTime as td_trainArrivalTime,
                    td.trainRideDuration as td_trainRideDuration,
                    td.trainRidePrice as td_trainRidePrice,
                    dep.id as dep_id, 
                    dep.trainStationFullName as dep_full_name,
                    dep.trainStationCode as dep_code, 
                    dep.trainStationCityLocation as dep_city,
                    arr.id as arr_id, 
                    arr.trainStationFullName as arr_full_name,
                    arr.trainStationCode as arr_code, 
                    arr.trainStationCityLocation as arr_city
                FROM train_details td
                LEFT JOIN train_stations dep ON td.trainDepartureStation = dep.trainStationCode
                LEFT JOIN train_stations arr ON td.trainArrivalStation = arr.trainStationCode
                WHERE td.id = ?
                """;

            List<TrainDetails> trains = jdbcTemplate.query(sql, new TrainDetailsRowMapper(), id);

            if (trains.isEmpty()) {
                System.out.println("✗ Repository: Train with ID " + id + " not found");
                return Optional.empty();
            }

            System.out.println("✓ Repository: Found train with ID " + id);
            return Optional.of(trains.get(0));
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding train by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<TrainDetails> findByTrainNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid train number");
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT 
                    td.id as td_id, 
                    td.trainNumber as td_trainNumber, 
                    td.trainLine as td_trainLine,
                    td.trainDepartureDate as td_trainDepartureDate,
                    td.trainDepartureTime as td_trainDepartureTime,
                    td.trainArrivalDate as td_trainArrivalDate,
                    td.trainArrivalTime as td_trainArrivalTime,
                    td.trainRideDuration as td_trainRideDuration,
                    td.trainRidePrice as td_trainRidePrice,
                    dep.id as dep_id, 
                    dep.trainStationFullName as dep_full_name,
                    dep.trainStationCode as dep_code, 
                    dep.trainStationCityLocation as dep_city,
                    arr.id as arr_id, 
                    arr.trainStationFullName as arr_full_name,
                    arr.trainStationCode as arr_code, 
                    arr.trainStationCityLocation as arr_city
                FROM train_details td
                LEFT JOIN train_stations dep ON td.trainDepartureStation = dep.trainStationCode
                LEFT JOIN train_stations arr ON td.trainArrivalStation = arr.trainStationCode
                WHERE td.trainNumber = ?
                """;

            List<TrainDetails> trains = jdbcTemplate.query(sql, new TrainDetailsRowMapper(), trainNumber);

            if (trains.isEmpty()) {
                System.out.println("✗ Repository: Train with number " + trainNumber + " not found");
                return Optional.empty();
            }

            System.out.println("✓ Repository: Found train with number " + trainNumber);
            return Optional.of(trains.get(0));
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding train by number: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<TrainDetails> findByRoute(Integer departureStationId, Integer arrivalStationId) {
        if (departureStationId == null || arrivalStationId == null ||
                departureStationId <= 0 || arrivalStationId <= 0) {
            System.out.println("✗ Repository: Invalid station IDs");
            return new ArrayList<>();
        }

        try {
            String sql = """
                SELECT 
                    td.id as td_id, 
                    td.trainNumber as td_trainNumber, 
                    td.trainLine as td_trainLine,
                    td.trainDepartureDate as td_trainDepartureDate,
                    td.trainDepartureTime as td_trainDepartureTime,
                    td.trainArrivalDate as td_trainArrivalDate,
                    td.trainArrivalTime as td_trainArrivalTime,
                    td.trainRideDuration as td_trainRideDuration,
                    td.trainRidePrice as td_trainRidePrice,
                    dep.id as dep_id, 
                    dep.trainStationFullName as dep_full_name,
                    dep.trainStationCode as dep_code, 
                    dep.trainStationCityLocation as dep_city,
                    arr.id as arr_id, 
                    arr.trainStationFullName as arr_full_name,
                    arr.trainStationCode as arr_code, 
                    arr.trainStationCityLocation as arr_city
                FROM train_details td
                LEFT JOIN train_stations dep ON td.trainDepartureStation = dep.trainStationCode
                LEFT JOIN train_stations arr ON td.trainArrivalStation = arr.trainStationCode
                WHERE td.trainDepartureStation = ? AND td.trainArrivalStation = ?
                """;

            List<TrainDetails> trains = jdbcTemplate.query(sql, new TrainDetailsRowMapper(),
                    departureStationId, arrivalStationId);

            System.out.println("✓ Repository: Found " + trains.size() + " trains for route");
            return trains;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding trains by route: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<TrainDetails> findByDepartureDate(String departureDate) {
        if (departureDate == null || departureDate.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid departure date");
            return new ArrayList<>();
        }

        try {
            List<TrainDetails> trains = jdbcTemplate.query(
                    "SELECT id, trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice FROM train_details WHERE trainDepartureDate = ?",
                    new SimpleTrainDetailsRowMapper(), departureDate);

            System.out.println("✓ Repository: Found " + trains.size() + " trains for date " + departureDate);
            return trains;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding trains by date: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean createTrainDetails(TrainDetails trainDetails) {
        if (!isValidForRepository(trainDetails)) {
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO train_details (trainNumber, trainLine, trainDepartureStation, trainArrivalStation, " +
                            "trainDepartureDate, trainDepartureTime, trainArrivalDate, trainArrivalTime, " +
                            "trainRideDuration, trainRidePrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    trainDetails.getTrainNumber(),
                    trainDetails.getTrainLine(),
                    trainDetails.getTrainDepartureStation() != null ? trainDetails.getTrainDepartureStation().getTrainStationCode() : null,
                    trainDetails.getTrainArrivalStation() != null ? trainDetails.getTrainArrivalStation().getTrainStationCode() : null,
                    trainDetails.getTrainDepartureDate(),
                    trainDetails.getTrainDepartureTime(),
                    trainDetails.getTrainArrivalDate(),
                    trainDetails.getTrainArrivalTime(),
                    trainDetails.getTrainRideDuration(),
                    trainDetails.getTrainRidePrice());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Train created: " + trainDetails.getTrainNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create train");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating train: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTrainDetails(TrainDetails trainDetails) {
        if (!isValidForRepository(trainDetails) || trainDetails.getId() == null) {
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE train_details SET trainNumber = ?, trainLine = ?, trainDepartureStation = ?, " +
                            "trainArrivalStation = ?, trainDepartureDate = ?, trainDepartureTime = ?, " +
                            "trainArrivalDate = ?, trainArrivalTime = ?, trainRideDuration = ?, trainRidePrice = ? " +
                            "WHERE id = ?",
                    trainDetails.getTrainNumber(),
                    trainDetails.getTrainLine(),
                    trainDetails.getTrainDepartureStation() != null ? trainDetails.getTrainDepartureStation().getId() : null,
                    trainDetails.getTrainArrivalStation() != null ? trainDetails.getTrainArrivalStation().getId() : null,
                    trainDetails.getTrainDepartureDate(),
                    trainDetails.getTrainDepartureTime(),
                    trainDetails.getTrainArrivalDate(),
                    trainDetails.getTrainArrivalTime(),
                    trainDetails.getTrainRideDuration(),
                    trainDetails.getTrainRidePrice(),
                    trainDetails.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Train updated: " + trainDetails.getTrainNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Train not found for update");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating train: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTrainDetails(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid train ID for deletion: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM train_details WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Train deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Train not found for deletion");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting train: " + e.getMessage());
            return false;
        }
    }

    private boolean isValidForRepository(TrainDetails trainDetails) {
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

        if (trainDetails.getTrainDepartureStation() == null || trainDetails.getTrainDepartureStation().getId() == null) {
            System.out.println("✗ Repository: Departure station is required");
            return false;
        }

        if (trainDetails.getTrainArrivalStation() == null || trainDetails.getTrainArrivalStation().getId() == null) {
            System.out.println("✗ Repository: Arrival station is required");
            return false;
        }

        // Verify stations exist in database
        Optional<TrainStation> depStation = trainStationRepository.findById(trainDetails.getTrainDepartureStation().getId());
        Optional<TrainStation> arrStation = trainStationRepository.findById(trainDetails.getTrainArrivalStation().getId());

        if (depStation.isEmpty()) {
            System.out.println("✗ Repository: Departure station not found in database");
            return false;
        }

        if (arrStation.isEmpty()) {
            System.out.println("✗ Repository: Arrival station not found in database");
            return false;
        }

        return true;
    }
}