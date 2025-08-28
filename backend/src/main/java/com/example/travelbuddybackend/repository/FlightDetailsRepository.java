package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.Airport;
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
    private final AirportRepository airportRepository;

    public FlightDetailsRepository(JdbcTemplate jdbcTemplate, AirportRepository airportRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.airportRepository = airportRepository;
    }

    // Optimized RowMapper using JOINs - CORRECTED column names
    private static class FlightDetailsRowMapper implements RowMapper<FlightDetails> {
        @Override
        public FlightDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            FlightDetails flightDetails = new FlightDetails();
            flightDetails.setId(rs.getInt("fd_id"));
            flightDetails.setFlightNumber(rs.getString("fd_flightNumber"));
            flightDetails.setFlightAirline(rs.getString("fd_flightAirline"));
            flightDetails.setFlightDepartureDate(rs.getString("fd_flightDepartureDate"));
            flightDetails.setFlightArrivalDate(rs.getString("fd_flightArrivalDate"));
            flightDetails.setFlightDepartureTime(rs.getString("fd_flightDepartureTime"));
            flightDetails.setFlightArrivalTime(rs.getString("fd_flightArrivalTime"));
            flightDetails.setFlightTravelTime(rs.getString("fd_flightTravelTime"));
            flightDetails.setFlightPrice(rs.getString("fd_flightPrice"));

            // Create Airport objects from JOIN data - no additional queries needed
            Airport originAirport = new Airport();
            originAirport.setId(rs.getInt("origin_id"));
            originAirport.setAirportFullName(rs.getString("origin_full_name"));
            originAirport.setAirportCode(rs.getString("origin_code"));
            originAirport.setAirportCityLocation(rs.getString("origin_city"));
            originAirport.setAirportCountryLocation(rs.getString("origin_country"));
            originAirport.setAirportTimezone(rs.getString("origin_timezone"));
            flightDetails.setFlightOrigin(originAirport);

            Airport destinationAirport = new Airport();
            destinationAirport.setId(rs.getInt("destination_id"));
            destinationAirport.setAirportFullName(rs.getString("destination_full_name"));
            destinationAirport.setAirportCode(rs.getString("destination_code"));
            destinationAirport.setAirportCityLocation(rs.getString("destination_city"));
            destinationAirport.setAirportCountryLocation(rs.getString("destination_country"));
            destinationAirport.setAirportTimezone(rs.getString("destination_timezone"));
            flightDetails.setFlightDestination(destinationAirport);

            return flightDetails;
        }
    }

    public List<FlightDetails> findAll() {
        try {
            // CORRECTED SQL using your actual database column names
            String sql = """
                SELECT 
                    fd.id as fd_id, 
                    fd.flightNumber as fd_flightNumber, 
                    fd.flightAirline as fd_flightAirline,
                    fd.flightDepartureDate as fd_flightDepartureDate, 
                    fd.flightArrivalDate as fd_flightArrivalDate,
                    fd.flightDepartureTime as fd_flightDepartureTime, 
                    fd.flightArrivalTime as fd_flightArrivalTime,
                    fd.flightTravelTime as fd_flightTravelTime, 
                    fd.flightPrice as fd_flightPrice,
                    origin.id as origin_id, 
                    origin.airportFullName as origin_full_name, 
                    origin.airportCode as origin_code,
                    origin.airportCityLocation as origin_city, 
                    origin.airportCountryLocation as origin_country, 
                    origin.airportTimezone as origin_timezone,
                    dest.id as destination_id, 
                    dest.airportFullName as destination_full_name, 
                    dest.airportCode as destination_code,
                    dest.airportCityLocation as destination_city, 
                    dest.airportCountryLocation as destination_country,
                    dest.airportTimezone as destination_timezone
                FROM flight_details fd
                LEFT JOIN airports origin ON fd.flightOrigin = origin.airportCode
                LEFT JOIN airports dest ON fd.flightDestination = dest.airportCode
                """;

            List<FlightDetails> flights = jdbcTemplate.query(sql, new FlightDetailsRowMapper());
            System.out.println("✓ Repository: Retrieved " + flights.size() + " flight details with airports");
            return flights;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving flight details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<FlightDetails> findById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid flight ID: " + id);
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT 
                    fd.id as fd_id, 
                    fd.flightNumber as fd_flightNumber, 
                    fd.flightAirline as fd_flightAirline,
                    fd.flightDepartureDate as fd_flightDepartureDate, 
                    fd.flightArrivalDate as fd_flightArrivalDate,
                    fd.flightDepartureTime as fd_flightDepartureTime, 
                    fd.flightArrivalTime as fd_flightArrivalTime,
                    fd.flightTravelTime as fd_flightTravelTime, 
                    fd.flightPrice as fd_flightPrice,
                    origin.id as origin_id, 
                    origin.airportFullName as origin_full_name, 
                    origin.airportCode as origin_code,
                    origin.airportCityLocation as origin_city, 
                    origin.airportCountryLocation as origin_country, 
                    origin.airportTimezone as origin_timezone,
                    dest.id as destination_id, 
                    dest.airportFullName as destination_full_name, 
                    dest.airportCode as destination_code,
                    dest.airportCityLocation as destination_city, 
                    dest.airportCountryLocation as destination_country,
                    dest.airportTimezone as destination_timezone
                FROM flight_details fd
                LEFT JOIN airports origin ON fd.flightOrigin = origin.airportCode
                LEFT JOIN airports dest ON fd.flightDestination = dest.airportCode
                WHERE fd.id = ?
                """;

            List<FlightDetails> flights = jdbcTemplate.query(sql, new FlightDetailsRowMapper(), id);

            if (flights.isEmpty()) {
                System.out.println("✗ Repository: Flight with ID " + id + " not found");
                return Optional.empty();
            }

            System.out.println("✓ Repository: Found flight with ID " + id);
            return Optional.of(flights.get(0));
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding flight by ID " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<FlightDetails> findByFlightNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid flight number: " + flightNumber);
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT 
                    fd.id as fd_id, 
                    fd.flightNumber as fd_flightNumber, 
                    fd.flightAirline as fd_flightAirline,
                    fd.flightDepartureDate as fd_flightDepartureDate, 
                    fd.flightArrivalDate as fd_flightArrivalDate,
                    fd.flightDepartureTime as fd_flightDepartureTime, 
                    fd.flightArrivalTime as fd_flightArrivalTime,
                    fd.flightTravelTime as fd_flightTravelTime, 
                    fd.flightPrice as fd_flightPrice,
                    origin.id as origin_id, 
                    origin.airportFullName as origin_full_name, 
                    origin.airportCode as origin_code,
                    origin.airportCityLocation as origin_city, 
                    origin.airportCountryLocation as origin_country, 
                    origin.airportTimezone as origin_timezone,
                    dest.id as destination_id, 
                    dest.airportFullName as destination_full_name, 
                    dest.airportCode as destination_code,
                    dest.airportCityLocation as destination_city, 
                    dest.airportCountryLocation as destination_country,
                    dest.airportTimezone as destination_timezone
                FROM flight_details fd
                LEFT JOIN airports origin ON fd.flightOrigin = origin.airportCode
                LEFT JOIN airports dest ON fd.flightDestination = dest.airportCode
                WHERE fd.flightNumber = ?
                """;

            List<FlightDetails> flights = jdbcTemplate.query(sql, new FlightDetailsRowMapper(), flightNumber);

            if (flights.isEmpty()) {
                System.out.println("✗ Repository: Flight with number " + flightNumber + " not found");
                return Optional.empty();
            }

            System.out.println("✓ Repository: Found flight with number " + flightNumber);
            return Optional.of(flights.get(0));
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding flight by number: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<FlightDetails> findByRoute(String originAirportCode, String destinationAirportCode) {
        if (originAirportCode == null || originAirportCode.trim().isEmpty() ||
                destinationAirportCode == null || destinationAirportCode.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid airport IDs");
            return new ArrayList<>();
        }

        try {
            String sql = """
                SELECT 
                    fd.id as fd_id, 
                    fd.flightNumber as fd_flightNumber, 
                    fd.flightAirline as fd_flightAirline,
                    fd.flightDepartureDate as fd_flightDepartureDate, 
                    fd.flightArrivalDate as fd_flightArrivalDate,
                    fd.flightDepartureTime as fd_flightDepartureTime, 
                    fd.flightArrivalTime as fd_flightArrivalTime,
                    fd.flightTravelTime as fd_flightTravelTime, 
                    fd.flightPrice as fd_flightPrice,
                    origin.id as origin_id, 
                    origin.airportFullName as origin_full_name, 
                    origin.airportCode as origin_code,
                    origin.airportCityLocation as origin_city, 
                    origin.airportCountryLocation as origin_country, 
                    origin.airportTimezone as origin_timezone,
                    dest.id as destination_id, 
                    dest.airportFullName as destination_full_name, 
                    dest.airportCode as destination_code,
                    dest.airportCityLocation as destination_city, 
                    dest.airportCountryLocation as destination_country,
                    dest.airportTimezone as destination_timezone
                FROM flight_details fd
                LEFT JOIN airports origin ON fd.flightOrigin = origin.airportCode
                LEFT JOIN airports dest ON fd.flightDestination = dest.airportCode
                WHERE fd.flightOrigin = ? AND fd.flightDestination = ?
                """;

            List<FlightDetails> flights = jdbcTemplate.query(sql, new FlightDetailsRowMapper(),
                    originAirportCode, destinationAirportCode);

            System.out.println("✓ Repository: Found " + flights.size() + " flights for route");
            return flights;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding flights by route: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<FlightDetails> findByDepartureDate(String departureDate) {
        if (departureDate == null || departureDate.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid departure date");
            return new ArrayList<>();
        }

        try {
            String sql = """
                SELECT 
                    fd.id as fd_id, 
                    fd.flightNumber as fd_flightNumber, 
                    fd.flightAirline as fd_flightAirline,
                    fd.flightDepartureDate as fd_flightDepartureDate, 
                    fd.flightArrivalDate as fd_flightArrivalDate,
                    fd.flightDepartureTime as fd_flightDepartureTime, 
                    fd.flightArrivalTime as fd_flightArrivalTime,
                    fd.flightTravelTime as fd_flightTravelTime, 
                    fd.flightPrice as fd_flightPrice,
                    origin.id as origin_id, 
                    origin.airportFullName as origin_full_name, 
                    origin.airportCode as origin_code,
                    origin.airportCityLocation as origin_city, 
                    origin.airportCountryLocation as origin_country, 
                    origin.airportTimezone as origin_timezone,
                    dest.id as destination_id, 
                    dest.airportFullName as destination_full_name, 
                    dest.airportCode as destination_code,
                    dest.airportCityLocation as destination_city, 
                    dest.airportCountryLocation as destination_country,
                    dest.airportTimezone as destination_timezone
                FROM flight_details fd
                LEFT JOIN airports origin ON fd.flightOrigin = origin.airportCode
                LEFT JOIN airports dest ON fd.flightDestination = dest.airportCode
                WHERE fd.flightDepartureDate = ?
            """;

            List<FlightDetails> Flightes = jdbcTemplate.query(sql, new FlightDetailsRepository.FlightDetailsRowMapper(), departureDate);
            System.out.println("✓ Repository: Found " + Flightes.size() + " Flightes for date " + departureDate);
            return Flightes;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding Flightes by date: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public boolean createFlightDetails(FlightDetails flightDetails) {
        if (!isValidForRepository(flightDetails)) {
            return false;
        }

        try {
            // Using your exact database column names
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO flight_details (flightNumber, flightAirline, flightOrigin, flightDestination, " +
                            "flightDepartureDate, flightArrivalDate, flightDepartureTime, flightArrivalTime, " +
                            "flightTravelTime, flightPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    flightDetails.getFlightNumber(),
                    flightDetails.getFlightAirline(),
                    flightDetails.getFlightOrigin() != null ? flightDetails.getFlightOrigin().getAirportCode() : null,
                    flightDetails.getFlightDestination() != null ? flightDetails.getFlightDestination().getAirportCode() : null,
                    flightDetails.getFlightDepartureDate(),
                    flightDetails.getFlightArrivalDate(),
                    flightDetails.getFlightDepartureTime(),
                    flightDetails.getFlightArrivalTime(),
                    flightDetails.getFlightTravelTime(),
                    flightDetails.getFlightPrice());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Flight created: " + flightDetails.getFlightNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create flight");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating flight: " + e.getMessage());
            return false;
        }
    }

    public boolean updateFlightDetails(FlightDetails flightDetails) {
        if (!isValidForRepository(flightDetails) || flightDetails.getId() == null) {
            return false;
        }

        try {
            // Using your exact database column names
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE flight_details SET flightNumber = ?, flightAirline = ?, flightOrigin = ?, " +
                            "flightDestination = ?, flightDepartureDate = ?, flightArrivalDate = ?, " +
                            "flightDepartureTime = ?, flightArrivalTime = ?, flightTravelTime = ?, flightPrice = ? " +
                            "WHERE id = ?",
                    flightDetails.getFlightNumber(),
                    flightDetails.getFlightAirline(),
                    flightDetails.getFlightOrigin() != null ? flightDetails.getFlightOrigin().getAirportCode() : null,
                    flightDetails.getFlightDestination() != null ? flightDetails.getFlightDestination().getAirportCode() : null,
                    flightDetails.getFlightDepartureDate(),
                    flightDetails.getFlightArrivalDate(),
                    flightDetails.getFlightDepartureTime(),
                    flightDetails.getFlightArrivalTime(),
                    flightDetails.getFlightTravelTime(),
                    flightDetails.getFlightPrice(),
                    flightDetails.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Flight updated: " + flightDetails.getFlightNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Flight not found for update");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating flight: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteFlightDetails(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid flight ID for deletion: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM flight_details WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Flight deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Flight not found for deletion");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting flight: " + e.getMessage());
            return false;
        }
    }

    // Repository-level validation (data constraints only)
    private boolean isValidForRepository(FlightDetails flightDetails) {
        if (flightDetails == null) {
            System.out.println("✗ Repository: Flight details cannot be null");
            return false;
        }

        if (flightDetails.getFlightNumber() == null || flightDetails.getFlightNumber().trim().isEmpty()) {
            System.out.println("✗ Repository: Flight number is required");
            return false;
        }

        if (flightDetails.getFlightAirline() == null || flightDetails.getFlightAirline().trim().isEmpty()) {
            System.out.println("✗ Repository: Flight airline is required");
            return false;
        }

        if (flightDetails.getFlightOrigin() == null || flightDetails.getFlightOrigin().getId() == null) {
            System.out.println("✗ Repository: Origin airport is required");
            return false;
        }

        if (flightDetails.getFlightDestination() == null || flightDetails.getFlightDestination().getId() == null) {
            System.out.println("✗ Repository: Destination airport is required");
            return false;
        }

        // Verify airports exist in database
        Optional<Airport> origin = airportRepository.findByAirportCode(flightDetails.getFlightOrigin().getAirportCode());
        Optional<Airport> destination = airportRepository.findByAirportCode(flightDetails.getFlightDestination().getAirportCode());

        if (origin.isEmpty()) {
            System.out.println("✗ Repository: Origin airport not found in database");
            return false;
        }

        if (destination.isEmpty()) {
            System.out.println("✗ Repository: Destination airport not found in database");
            return false;
        }

        return true;
    }
}