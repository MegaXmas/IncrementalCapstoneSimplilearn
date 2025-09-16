package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.TrainStation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainStationRepository {

    private final JdbcTemplate jdbcTemplate;

    public TrainStationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class TrainStationRowMapper implements RowMapper<TrainStation> {
        @Override
        public TrainStation mapRow(ResultSet rs, int rowNum) throws SQLException {
            TrainStation trainStation = new TrainStation();
            trainStation.setId(rs.getInt("id"));
            trainStation.setTrainStationFullName(rs.getString("trainStationFullName"));
            trainStation.setTrainStationCode(rs.getString("trainStationCode"));
            trainStation.setTrainStationCityLocation(rs.getString("trainStationCityLocation"));
            return trainStation;
        }
    }

    public List<TrainStation> findAll() {
        try {
            List<TrainStation> trainStations = jdbcTemplate.query(
                    "SELECT id, trainStationFullName, trainStationCode, trainStationCityLocation FROM train_stations",
                    new TrainStationRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + trainStations.size() + " train stations");
            return trainStations;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving train stations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<TrainStation> findById(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid train station ID: " + id);
            return Optional.empty();
        }

        try {
            List<TrainStation> trainStations = jdbcTemplate.query(
                    "SELECT id, trainStationFullName, trainStationCode, trainStationCityLocation FROM train_stations WHERE id = ?",
                    new TrainStationRowMapper(), id);

            if (trainStations.isEmpty()) {
                System.out.println("✗ Repository: Train station with ID " + id + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found train station: " + trainStations.get(0).getTrainStationFullName() + " with ID " + id);
                return Optional.of(trainStations.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding train station with ID: " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<TrainStation> findByStationCode(String stationCode) {
        if (stationCode == null || stationCode.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Invalid station code: " + stationCode);
            return Optional.empty();
        }

        try {
            List<TrainStation> trainStations = jdbcTemplate.query(
                    "SELECT id, trainStationFullName, trainStationCode, trainStationCityLocation FROM train_stations WHERE trainStationCode = ?",
                    new TrainStationRowMapper(), stationCode.toUpperCase());

            if (trainStations.isEmpty()) {
                System.out.println("✗ Repository: Train station with code " + stationCode + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found train station: " + trainStations.get(0).getTrainStationFullName() + " with code " + stationCode);
                return Optional.of(trainStations.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding train station with code: " + stationCode + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<TrainStation> findByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Invalid city: " + city);
            return new ArrayList<>();
        }

        try {
            List<TrainStation> trainStations = jdbcTemplate.query(
                    "SELECT id, trainStationFullName, trainStationCode, trainStationCityLocation FROM train_stations WHERE trainStationCityLocation = ?",
                    new TrainStationRowMapper(), city);

            System.out.println("✓ Repository: Found " + trainStations.size() + " train stations in " + city);
            return trainStations;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding train stations in city " + city + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean createTrainStation(TrainStation trainStation) {

        if (!isValidTrainStation(trainStation)) {
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO train_stations (trainStationFullName, trainStationCode, trainStationCityLocation) VALUES (?, ?, ?)",
                    trainStation.getTrainStationFullName(), trainStation.getTrainStationCode(), trainStation.getTrainStationCityLocation());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: New train station created: " + trainStation.getTrainStationFullName() + " (" + trainStation.getTrainStationCode() + ")");
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create train station");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating train station: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTrainStation(TrainStation trainStation) {

        if (!isValidTrainStation(trainStation)) {
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE train_stations SET trainStationFullName = ?, trainStationCode = ?, trainStationCityLocation = ? WHERE id = ?",
                    trainStation.getTrainStationFullName(), trainStation.getTrainStationCode(), trainStation.getTrainStationCityLocation(), trainStation.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Train station updated successfully: " + trainStation.getTrainStationFullName() + " (" + trainStation.getTrainStationCode() + ")");
                return true;
            } else {
                System.out.println("✗ Repository: Failed to update train station " + trainStation.getTrainStationFullName());
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating train station: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTrainStation(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid train station ID: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM train_stations WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Train station with ID " + id + " deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Train station with ID " + id + " not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting train station: " + e.getMessage());
            return false;
        }
    }

    public boolean isValidTrainStation(TrainStation trainStation) {
        if (trainStation == null) {
            System.out.println("✗ Repository: Error: Cannot update null train station");
            return false;
        }


        if (trainStation.getTrainStationFullName() == null || trainStation.getTrainStationFullName().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train station name is required");
            return false;
        }

        if (trainStation.getTrainStationCode() == null || trainStation.getTrainStationCode().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train station code is required");
            return false;
        }

        if (trainStation.getTrainStationCityLocation() == null || trainStation.getTrainStationCityLocation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Train station city location is required");
            return false;
        }
        return true;
    }
}