package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.BusStation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BusStationRepository {

    private final JdbcTemplate jdbcTemplate;

    public BusStationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class BusStationRowMapper implements RowMapper<BusStation> {
        @Override
        public BusStation mapRow(ResultSet rs, int rowNum) throws SQLException {
            BusStation busStation = new BusStation();
            busStation.setId(rs.getInt("id"));
            busStation.setBusStationFullName(rs.getString("busStationFullName"));
            busStation.setBusStationCode(rs.getString("busStationCode"));
            busStation.setBusStationCityLocation(rs.getString("busStationCityLocation"));
            return busStation;
        }
    }

    public List<BusStation> findAll() {
        try {
            List<BusStation> busStations = jdbcTemplate.query(
                    "SELECT id, busStationFullName, busStationCode, busStationCityLocation FROM bus_stations",
                    new BusStationRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + busStations.size() + " bus stations");
            return busStations;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving bus stations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<BusStation> findById(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid bus station ID: " + id);
            return Optional.empty();
        }

        try {
            List<BusStation> busStations = jdbcTemplate.query(
                    "SELECT id, busStationFullName, busStationCode, busStationCityLocation FROM bus_stations WHERE id = ?",
                    new BusStationRowMapper(), id);

            if (busStations.isEmpty()) {
                System.out.println("✗ Repository: Bus station with ID " + id + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found bus station: " + busStations.get(0).getBusStationFullName() + " with ID " + id);
                return Optional.of(busStations.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding bus station with ID: " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<BusStation> findByStationCode(String stationCode) {
        if (stationCode == null || stationCode.trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Invalid station code: " + stationCode);
            return Optional.empty();
        }

        try {
            List<BusStation> busStations = jdbcTemplate.query(
                    "SELECT id, busStationFullName, busStationCode, busStationCityLocation FROM bus_stations WHERE busStationCode = ?",
                    new BusStationRowMapper(), stationCode.toUpperCase());

            if (busStations.isEmpty()) {
                System.out.println("✗ Repository: Bus station with code " + stationCode + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found bus station: " + busStations.get(0).getBusStationFullName() + " with code " + stationCode);
                return Optional.of(busStations.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding bus station with code: " + stationCode + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean createBusStation(BusStation busStation) {

        if (!isValidBusStation(busStation)) {
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO bus_stations (busStationFullName, busStationCode, busStationCityLocation) VALUES (?, ?, ?)",
                    busStation.getBusStationFullName(), busStation.getBusStationCode(), busStation.getBusStationCityLocation());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: New bus station created: " + busStation.getBusStationFullName() + " (" + busStation.getBusStationCode() + ")");
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create bus station");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating bus station: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBusStation(BusStation busStation) {
        // Validate first - return early if validation fails
        if (!isValidBusStation(busStation)) {
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE bus_stations SET busStationFullName = ?, busStationCode = ?, busStationCityLocation = ? WHERE id = ?",
                    busStation.getBusStationFullName(),
                    busStation.getBusStationCode(),
                    busStation.getBusStationCityLocation(),
                    busStation.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Bus station updated successfully: " +
                        busStation.getBusStationFullName() + " (" + busStation.getBusStationCode() + ")");
                return true;
            } else {
                System.out.println("✗ Repository: Failed to update bus station " + busStation.getBusStationFullName());
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating bus station: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBusStation(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid bus station ID: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM bus_stations WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Bus station with ID " + id + " deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Bus station with ID " + id + " not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting bus station: " + e.getMessage());
            return false;
        }
    }

    //=====================Validation====================

    public boolean isValidBusStation(BusStation busStation) {
        // Null check first
        if (busStation == null) {
            System.out.println("✗ Repository: Error: Cannot validate null bus station");
            return false;
        }

        if (busStation.getBusStationFullName() == null || busStation.getBusStationFullName().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus station full name is required");
            return false;
        }

        if (busStation.getBusStationCode() == null || busStation.getBusStationCode().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus station code is required");
            return false;
        }

        if (busStation.getBusStationCityLocation() == null || busStation.getBusStationCityLocation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: Bus station city location is required");
            return false;
        }

        // All validation passed
        System.out.println("✓ Repository: Bus station validation successful for ID " + busStation.getId());
        return true;
    }
}