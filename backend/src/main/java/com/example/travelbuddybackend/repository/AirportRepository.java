package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.Airport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AirportRepository {

    private final JdbcTemplate jdbcTemplate;

    public AirportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class AirportRowMapper implements RowMapper<Airport> {
        @Override
        public Airport mapRow(ResultSet rs, int rowNum) throws SQLException {
            com.example.travelbuddybackend.models.Airport airport = new Airport();
            airport.setId(rs.getInt("id"));
            airport.setAirportFullName(rs.getString("airportFullName"));
            airport.setAirportCode(rs.getString("airportCode"));
            airport.setAirportCityLocation(rs.getString("airportCityLocation"));
            airport.setAirportCountryLocation(rs.getString("airportCountryLocation"));
            airport.setAirportTimezone(rs.getString("airportTimezone"));
            return airport;
        }
    }

    public List<Airport> findAll() {
        try {
            List<Airport> airport = jdbcTemplate.query(
                    "SELECT id, airportFullName, airportCode, airportCityLocation, airportCountryLocation, airportTimezone FROM airports",
                    new AirportRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + airport.size() + " airports");
            return airport;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving airports: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<Airport> findById(int id) {
        if (id <= 0) {
            System.out.println("✗ Repository: Error: Invalid airport ID: " + id);
            return Optional.empty();
        }

        try {
            List<Airport> airports = jdbcTemplate.query(
                    "SELECT id, airportFullName, airportCode, airportCityLocation, airportCountryLocation, airportTimezone FROM airports WHERE id = ?",
                    new AirportRowMapper(), id);

            if (airports.isEmpty()) {
                System.out.println("✗ Repository: Airport with ID " + id + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found airport: " + airports.get(0).getAirportFullName() + " with ID " + id + " found");
                return Optional.of(airports.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding airport with ID: " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Airport> findByAirportCode(String airportCode) {
        if (airportCode == null || airportCode.isEmpty()) {
            System.out.println("✗ Repository: Error: Invalid airportCode: " + airportCode);
            return Optional.empty();
        }

        try {
            List<Airport> airports = jdbcTemplate.query(
                    "SELECT id, airportFullName, airportCode, airportCityLocation, airportCountryLocation, airportTimezone FROM airports WHERE airportCode = ?",
                    new AirportRowMapper(), airportCode);

            if (airports.isEmpty()) {
                System.out.println("✗ Repository: Airport with airportCode " + airportCode + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found airport: " + airports.get(0).getAirportFullName() + " with airportCode " + airportCode + " found");
                return Optional.of(airports.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding airport with airportCode: " + airportCode + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean newAirport(Airport airport) {
        if (airport == null) {
            System.out.println("✗ Repository: Error: Cannot create null airport");
            return false;
        }

        if (airport.getAirportFullName() == null || airport.getAirportFullName().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: airportName is required");
            return false;
        }

        if (airport.getAirportCode() == null || airport.getAirportCode().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: airportCode is required");
            return false;
        }

        if (airport.getAirportCityLocation() == null || airport.getAirportCityLocation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: airportCityLocation is required");
            return false;
        }

        if (airport.getAirportCountryLocation() == null || airport.getAirportCountryLocation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: airportCountryLocation is required");
            return false;
        }

        if (airport.getAirportTimezone() == null || airport.getAirportTimezone().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: airportTimezone is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO airports (airportFullName, airportCode, airportCityLocation, airportCountryLocation, airportTimezone) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    airport.getAirportFullName(),
                    airport.getAirportCode(),
                    airport.getAirportCityLocation(),
                    airport.getAirportCountryLocation(),
                    airport.getAirportTimezone());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: New airport created: " + airport.getAirportFullName() + " (" + airport.getAirportCode() + ")");
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create airport");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating airport: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAirport(Airport airport) {
        if (airport == null) {
            System.out.println("✗ Repository: Error: Cannot update null airport");
            return false;
        }

        if (airport.getId() <= 0) {
            System.out.println("✗ Repository: Error: Invalid airport ID " + airport.getId());
            return false;
        }

        if (airport.getAirportFullName() == null || airport.getAirportFullName().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: airportName is required");
            return false;
        }

        if (airport.getAirportCode() == null || airport.getAirportCode().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: airportCode is required");
            return false;
        }

        if (airport.getAirportCityLocation() == null || airport.getAirportCityLocation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: airportCityLocation is required");
            return false;
        }

        if (airport.getAirportCountryLocation() == null || airport.getAirportCountryLocation().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: airportCountryLocation is required");
            return false;
        }

        if (airport.getAirportTimezone() == null || airport.getAirportTimezone().trim().isEmpty()) {
            System.out.println("✗ Repository: Error: airportTimezone is required");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE airports SET airportFullName = ?, airportCode = ?, airportCityLocation = ?, airportCountryLocation = ?, airportTimezone = ? WHERE id = ?",
                    airport.getAirportFullName(),
                    airport.getAirportCode(),
                    airport.getAirportCityLocation(),
                    airport.getAirportCountryLocation(),
                    airport.getAirportTimezone(),
                    airport.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Airport: " + airport.getAirportFullName() + " (" + airport.getAirportCode() + ") updated successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Failed to update airport " + airport.getAirportFullName() + " (" + airport.getAirportCode() + ") - airport not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating airport: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAirport(Airport airport) {
        if (airport == null) {
            System.out.println("✗ Repository: Error: Cannot delete null airport");
            return false;
        }

        if (airport.getId() == null || airport.getId() <= 0) {
            System.out.println("✗ Repository: Error: Airport must have valid ID for deletion");
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "DELETE FROM airports WHERE id = ?",
                    airport.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Airport " + airport.getAirportFullName() + " (" + airport.getAirportCode() + ") deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Airport with ID " + airport.getId() + " not found for deletion");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting airport: " + e.getMessage());
            return false;
        }
    }
}