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
            Airport airport = new Airport();
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
            List<Airport> airports = jdbcTemplate.query(
                    "SELECT id, airportFullName, airportCode, airportCityLocation, airportCountryLocation, airportTimezone FROM airports",
                    new AirportRowMapper());
            System.out.println("✓ Repository: Successfully retrieved " + airports.size() + " airports");
            return airports;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving airports: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<Airport> findById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid airport ID: " + id);
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
                System.out.println("✓ Repository: Found airport: " + airports.get(0).getAirportFullName() + " with ID " + id);
                return Optional.of(airports.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding airport with ID " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Airport> findByAirportCode(String airportCode) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid airport code: " + airportCode);
            return Optional.empty();
        }

        try {
            List<Airport> airports = jdbcTemplate.query(
                    "SELECT id, airportFullName, airportCode, airportCityLocation, airportCountryLocation, airportTimezone FROM airports WHERE airportCode = ?",
                    new AirportRowMapper(), airportCode.toUpperCase().trim());

            if (airports.isEmpty()) {
                System.out.println("✗ Repository: Airport with code " + airportCode + " not found");
                return Optional.empty();
            } else {
                System.out.println("✓ Repository: Found airport: " + airports.get(0).getAirportFullName() + " with code " + airportCode);
                return Optional.of(airports.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding airport with code " + airportCode + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    // Added method for partial name search (used by service)
    public List<Airport> findByPartialName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid partial name: " + partialName);
            return new ArrayList<>();
        }

        try {
            String searchTerm = "%" + partialName.trim().toLowerCase() + "%";
            List<Airport> airports = jdbcTemplate.query(
                    "SELECT id, airportFullName, airportCode, airportCityLocation, airportCountryLocation, airportTimezone " +
                            "FROM airports WHERE LOWER(airportFullName) LIKE ?",
                    new AirportRowMapper(), searchTerm);

            System.out.println("✓ Repository: Found " + airports.size() + " airports matching '" + partialName + "'");
            return airports;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error searching airports by name: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Added method for city search (used by service)
    public List<Airport> findByCityLocation(String cityLocation) {
        if (cityLocation == null || cityLocation.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid city location: " + cityLocation);
            return new ArrayList<>();
        }

        try {
            String searchTerm = "%" + cityLocation.trim().toLowerCase() + "%";
            List<Airport> airports = jdbcTemplate.query(
                    "SELECT id, airportFullName, airportCode, airportCityLocation, airportCountryLocation, airportTimezone " +
                            "FROM airports WHERE LOWER(airportCityLocation) LIKE ?",
                    new AirportRowMapper(), searchTerm);

            System.out.println("✓ Repository: Found " + airports.size() + " airports in city '" + cityLocation + "'");
            return airports;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error searching airports by city: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Added method for country search (used by service)
    public List<Airport> findByCountryLocation(String countryLocation) {
        if (countryLocation == null || countryLocation.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid country location: " + countryLocation);
            return new ArrayList<>();
        }

        try {
            String searchTerm = "%" + countryLocation.trim().toLowerCase() + "%";
            List<Airport> airports = jdbcTemplate.query(
                    "SELECT id, airportFullName, airportCode, airportCityLocation, airportCountryLocation, airportTimezone " +
                            "FROM airports WHERE LOWER(airportCountryLocation) LIKE ?",
                    new AirportRowMapper(), searchTerm);

            System.out.println("✓ Repository: Found " + airports.size() + " airports in country '" + countryLocation + "'");
            return airports;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error searching airports by country: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean createAirport(Airport airport) {
        if (!isValidForRepository(airport)) {
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO airports (airportFullName, airportCode, airportCityLocation, airportCountryLocation, airportTimezone) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    airport.getAirportFullName(),
                    airport.getAirportCode().toUpperCase(), // Normalize airport codes to uppercase
                    airport.getAirportCityLocation(),
                    airport.getAirportCountryLocation(),
                    airport.getAirportTimezone());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Airport created: " + airport.getAirportFullName() + " (" + airport.getAirportCode() + ")");
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
        if (!isValidForRepository(airport) || airport.getId() == null) {
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE airports SET airportFullName = ?, airportCode = ?, airportCityLocation = ?, airportCountryLocation = ?, airportTimezone = ? WHERE id = ?",
                    airport.getAirportFullName(),
                    airport.getAirportCode().toUpperCase(), // Normalize airport codes to uppercase
                    airport.getAirportCityLocation(),
                    airport.getAirportCountryLocation(),
                    airport.getAirportTimezone(),
                    airport.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Airport updated: " + airport.getAirportFullName() + " (" + airport.getAirportCode() + ")");
                return true;
            } else {
                System.out.println("✗ Repository: Airport not found for update");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating airport: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAirport(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid airport ID for deletion: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM airports WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Airport deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Airport not found for deletion");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting airport: " + e.getMessage());
            return false;
        }
    }

    // Repository-level validation (data constraints only)
    private boolean isValidForRepository(Airport airport) {
        if (airport == null) {
            System.out.println("✗ Repository: Airport cannot be null");
            return false;
        }

        if (airport.getAirportFullName() == null || airport.getAirportFullName().trim().isEmpty()) {
            System.out.println("✗ Repository: Airport name is required");
            return false;
        }

        if (airport.getAirportCode() == null || airport.getAirportCode().trim().isEmpty()) {
            System.out.println("✗ Repository: Airport code is required");
            return false;
        }

        if (airport.getAirportCityLocation() == null || airport.getAirportCityLocation().trim().isEmpty()) {
            System.out.println("✗ Repository: Airport city location is required");
            return false;
        }

        if (airport.getAirportCountryLocation() == null || airport.getAirportCountryLocation().trim().isEmpty()) {
            System.out.println("✗ Repository: Airport country location is required");
            return false;
        }

        if (airport.getAirportTimezone() == null || airport.getAirportTimezone().trim().isEmpty()) {
            System.out.println("✗ Repository: Airport timezone is required");
            return false;
        }

        return true;
    }
}