package com.example.travelbuddybackend.repository;

import com.example.travelbuddybackend.models.BusDetails;
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
public class BusDetailsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BusStationRepository busStationRepository;

    public BusDetailsRepository(JdbcTemplate jdbcTemplate, BusStationRepository busStationRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.busStationRepository = busStationRepository;
    }

    private static class BusDetailsRowMapper implements RowMapper<BusDetails> {
        @Override
        public BusDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            BusDetails busDetails = new BusDetails();
            busDetails.setId(rs.getInt("bd_id"));
            busDetails.setBusNumber(rs.getString("bd_busNumber"));
            busDetails.setBusLine(rs.getString("bd_busLine"));
            busDetails.setBusDepartureDate(rs.getString("bd_busDepartureDate"));
            busDetails.setBusDepartureTime(rs.getString("bd_busDepartureTime"));
            busDetails.setBusArrivalDate(rs.getString("bd_busArrivalDate"));
            busDetails.setBusArrivalTime(rs.getString("bd_busArrivalTime"));
            busDetails.setBusRideDuration(rs.getString("bd_busRideDuration"));
            busDetails.setBusRidePrice(rs.getString("bd_busRidePrice"));

            // Create BusStation objects from JOIN data
            BusStation departureStation = new BusStation();
            departureStation.setId(rs.getInt("dep_id"));
            departureStation.setBusStationFullName(rs.getString("dep_full_name"));
            departureStation.setBusStationCode(rs.getString("dep_code"));
            departureStation.setBusStationCityLocation(rs.getString("dep_city"));
            busDetails.setBusDepartureStation(departureStation);

            BusStation arrivalStation = new BusStation();
            arrivalStation.setId(rs.getInt("arr_id"));
            arrivalStation.setBusStationFullName(rs.getString("arr_full_name"));
            arrivalStation.setBusStationCode(rs.getString("arr_code"));
            arrivalStation.setBusStationCityLocation(rs.getString("arr_city"));
            busDetails.setBusArrivalStation(arrivalStation);

            return busDetails;
        }
    }

    public List<BusDetails> findAll() {
        try {
            String sql = """
            SELECT 
                bd.id as bd_id, 
                bd.busNumber as bd_busNumber, 
                bd.busLine as bd_busLine,
                bd.busDepartureDate as bd_busDepartureDate,
                bd.busDepartureTime as bd_busDepartureTime,
                bd.busArrivalDate as bd_busArrivalDate,
                bd.busArrivalTime as bd_busArrivalTime,
                bd.busRideDuration as bd_busRideDuration,
                bd.busRidePrice as bd_busRidePrice,
                dep.id as dep_id, 
                dep.busStationFullName as dep_full_name,
                dep.busStationCode as dep_code, 
                dep.busStationCityLocation as dep_city,
                arr.id as arr_id, 
                arr.busStationFullName as arr_full_name,
                arr.busStationCode as arr_code, 
                arr.busStationCityLocation as arr_city
            FROM bus_details bd
            LEFT JOIN bus_stations dep ON bd.busDepartureStation = dep.busStationCode
            LEFT JOIN bus_stations arr ON bd.busArrivalStation = arr.busStationCode
            """;

            List<BusDetails> buses = jdbcTemplate.query(sql, new BusDetailsRowMapper());
            System.out.println("✓ Repository: Retrieved " + buses.size() + " bus details");
            return buses;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error retrieving bus details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<BusDetails> findById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid bus ID: " + id);
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT 
                    bd.id as bd_id, 
                    bd.busNumber as bd_busNumber, 
                    bd.busLine as bd_busLine,
                    bd.busDepartureDate as bd_busDepartureDate,
                    bd.busDepartureTime as bd_busDepartureTime,
                    bd.busArrivalDate as bd_busArrivalDate,
                    bd.busArrivalTime as bd_busArrivalTime,
                    bd.busRideDuration as bd_busRideDuration,
                    bd.busRidePrice as bd_busRidePrice,
                    dep.id as dep_id, 
                    dep.busStationFullName as dep_full_name,
                    dep.busStationCode as dep_code, 
                    dep.busStationCityLocation as dep_city,
                    arr.id as arr_id, 
                    arr.busStationFullName as arr_full_name,
                    arr.busStationCode as arr_code, 
                    arr.busStationCityLocation as arr_city
                FROM bus_details bd
                LEFT JOIN bus_stations dep ON bd.busDepartureStation = dep.busStationCode
                LEFT JOIN bus_stations arr ON bd.busArrivalStation = arr.busStationCode
                WHERE bd.id = ?
                """;

            List<BusDetails> buses = jdbcTemplate.query(sql, new BusDetailsRowMapper(), id);

            if (buses.isEmpty()) {
                System.out.println("✗ Repository: Bus with ID " + id + " not found");
                return Optional.empty();
            }

            System.out.println("✓ Repository: Found bus with ID " + id);
            return Optional.of(buses.get(0));
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding bus by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<BusDetails> findByBusNumber(String busNumber) {
        if (busNumber == null || busNumber.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid bus number");
            return Optional.empty();
        }

        try {
            String sql = """
                SELECT 
                    bd.id as bd_id, 
                    bd.busNumber as bd_busNumber, 
                    bd.busLine as bd_busLine,
                    bd.busDepartureDate as bd_busDepartureDate,
                    bd.busDepartureTime as bd_busDepartureTime,
                    bd.busArrivalDate as bd_busArrivalDate,
                    bd.busArrivalTime as bd_busArrivalTime,
                    bd.busRideDuration as bd_busRideDuration,
                    bd.busRidePrice as bd_busRidePrice,
                    dep.id as dep_id, 
                    dep.busStationFullName as dep_full_name,
                    dep.busStationCode as dep_code, 
                    dep.busStationCityLocation as dep_city,
                    arr.id as arr_id, 
                    arr.busStationFullName as arr_full_name,
                    arr.busStationCode as arr_code, 
                    arr.busStationCityLocation as arr_city
                FROM bus_details bd
                LEFT JOIN bus_stations dep ON bd.busDepartureStation = dep.busStationCode
                LEFT JOIN bus_stations arr ON bd.busArrivalStation = arr.busStationCode
                WHERE bd.busNumber = ?
                """;

            List<BusDetails> buses = jdbcTemplate.query(sql, new BusDetailsRowMapper(), busNumber);

            if (buses.isEmpty()) {
                System.out.println("✗ Repository: Bus with number " + busNumber + " not found");
                return Optional.empty();
            }

            System.out.println("✓ Repository: Found bus with number " + busNumber);
            return Optional.of(buses.get(0));
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding bus by number: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<BusDetails> findByRouteStationCodes(String departureStationCode, String arrivalStationCode) {
        if (departureStationCode == null || departureStationCode.trim().isEmpty() ||
                arrivalStationCode == null || arrivalStationCode.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid station codes");
            return new ArrayList<>();
        }

        try {
            String sql = """
            SELECT 
                bd.id as bd_id, 
                bd.busNumber as bd_busNumber, 
                bd.busLine as bd_busLine,
                bd.busDepartureDate as bd_busDepartureDate,
                bd.busDepartureTime as bd_busDepartureTime,
                bd.busArrivalDate as bd_busArrivalDate,
                bd.busArrivalTime as bd_busArrivalTime,
                bd.busRideDuration as bd_busRideDuration,
                bd.busRidePrice as bd_busRidePrice,
                dep.id as dep_id, 
                dep.busStationFullName as dep_full_name,
                dep.busStationCode as dep_code, 
                dep.busStationCityLocation as dep_city,
                arr.id as arr_id, 
                arr.busStationFullName as arr_full_name,
                arr.busStationCode as arr_code, 
                arr.busStationCityLocation as arr_city
            FROM bus_details bd
            LEFT JOIN bus_stations dep ON bd.busDepartureStation = dep.busStationCode
            LEFT JOIN bus_stations arr ON bd.busArrivalStation = arr.busStationCode
            WHERE bd.busDepartureStation = ? AND bd.busArrivalStation = ?
            """;

            List<BusDetails> buses = jdbcTemplate.query(sql, new BusDetailsRowMapper(),
                    departureStationCode, arrivalStationCode);

            System.out.println("✓ Repository: Found " + buses.size() + " buses for route");
            return buses;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding buses by route: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<BusDetails> findByDepartureDate(String departureDate) {
        if (departureDate == null || departureDate.trim().isEmpty()) {
            System.out.println("✗ Repository: Invalid departure date");
            return new ArrayList<>();
        }

        try {
            String sql = """
            SELECT 
                bd.id as bd_id, 
                bd.busNumber as bd_busNumber, 
                bd.busLine as bd_busLine,
                bd.busDepartureDate as bd_busDepartureDate,
                bd.busDepartureTime as bd_busDepartureTime,
                bd.busArrivalDate as bd_busArrivalDate,
                bd.busArrivalTime as bd_busArrivalTime,
                bd.busRideDuration as bd_busRideDuration,
                bd.busRidePrice as bd_busRidePrice,
                dep.id as dep_id, 
                dep.busStationFullName as dep_full_name,
                dep.busStationCode as dep_code, 
                dep.busStationCityLocation as dep_city,
                arr.id as arr_id, 
                arr.busStationFullName as arr_full_name,
                arr.busStationCode as arr_code, 
                arr.busStationCityLocation as arr_city
            FROM bus_details bd
            LEFT JOIN bus_stations dep ON bd.busDepartureStation = dep.busStationCode
            LEFT JOIN bus_stations arr ON bd.busArrivalStation = arr.busStationCode
            WHERE bd.busDepartureDate = ?
            """;

            List<BusDetails> buses = jdbcTemplate.query(sql, new BusDetailsRowMapper(), departureDate);
            System.out.println("✓ Repository: Found " + buses.size() + " buses for date " + departureDate);
            return buses;
        } catch (Exception e) {
            System.out.println("✗ Repository: Error finding buses by date: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean createBusDetails(BusDetails busDetails) {
        if (!isValidForRepository(busDetails)) {
            return false;
        }

        try {
            // Insert STATION CODES (not IDs) to match your database foreign key constraint
            int rowsAffected = jdbcTemplate.update(
                    "INSERT INTO bus_details (busNumber, busLine, busDepartureStation, busArrivalStation, " +
                            "busDepartureDate, busDepartureTime, busArrivalDate, busArrivalTime, " +
                            "busRideDuration, busRidePrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    busDetails.getBusNumber(),
                    busDetails.getBusLine(),
                    // ✅ Use station CODES, not IDs
                    busDetails.getBusDepartureStation() != null ? busDetails.getBusDepartureStation().getBusStationCode() : null,
                    busDetails.getBusArrivalStation() != null ? busDetails.getBusArrivalStation().getBusStationCode() : null,
                    busDetails.getBusDepartureDate(),
                    busDetails.getBusDepartureTime(),
                    busDetails.getBusArrivalDate(),
                    busDetails.getBusArrivalTime(),
                    busDetails.getBusRideDuration(),
                    busDetails.getBusRidePrice());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Bus created: " + busDetails.getBusNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Failed to create bus");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error creating bus: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBusDetails(BusDetails busDetails) {
        if (!isValidForRepository(busDetails) || busDetails.getId() == null) {
            return false;
        }

        try {
            // Using your exact database column names
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE bus_details SET busNumber = ?, busLine = ?, busDepartureStation = ?, " +
                            "busArrivalStation = ?, busDepartureDate = ?, busDepartureTime = ?, " +
                            "busArrivalDate = ?, busArrivalTime = ?, busRideDuration = ?, busRidePrice = ? " +
                            "WHERE id = ?",
                    busDetails.getBusNumber(),
                    busDetails.getBusLine(),
                    busDetails.getBusDepartureStation() != null ? busDetails.getBusDepartureStation().getBusStationCode() : null,
                    busDetails.getBusArrivalStation() != null ? busDetails.getBusArrivalStation().getBusStationCode() : null,
                    busDetails.getBusDepartureDate(),
                    busDetails.getBusDepartureTime(),
                    busDetails.getBusArrivalDate(),
                    busDetails.getBusArrivalTime(),
                    busDetails.getBusRideDuration(),
                    busDetails.getBusRidePrice(),
                    busDetails.getId());

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Bus details updated: " + busDetails.getBusNumber());
                return true;
            } else {
                System.out.println("✗ Repository: Bus details not found for update");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error updating bus details: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBusDetails(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Repository: Invalid bus ID for deletion: " + id);
            return false;
        }

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM bus_details WHERE id = ?", id);

            if (rowsAffected > 0) {
                System.out.println("✓ Repository: Bus deleted successfully");
                return true;
            } else {
                System.out.println("✗ Repository: Bus not found for deletion");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Repository: Error deleting bus: " + e.getMessage());
            return false;
        }
    }

    // Repository-level validation (data constraints only)
    private boolean isValidForRepository(BusDetails busDetails) {
        if (busDetails == null) {
            System.out.println("✗ Repository: Bus details cannot be null");
            return false;
        }

        if (busDetails.getBusNumber() == null || busDetails.getBusNumber().trim().isEmpty()) {
            System.out.println("✗ Repository: Bus number is required");
            return false;
        }

        if (busDetails.getBusLine() == null || busDetails.getBusLine().trim().isEmpty()) {
            System.out.println("✗ Repository: Bus line is required");
            return false;
        }

        if (busDetails.getBusDepartureStation() == null ||
                busDetails.getBusDepartureStation().getBusStationCode() == null ||
                busDetails.getBusDepartureStation().getBusStationCode().trim().isEmpty()) {
            System.out.println("✗ Repository: Departure station code is required");
            return false;
        }

        if (busDetails.getBusArrivalStation() == null ||
                busDetails.getBusArrivalStation().getBusStationCode() == null ||
                busDetails.getBusArrivalStation().getBusStationCode().trim().isEmpty()) {
            System.out.println("✗ Repository: Arrival station code is required");
            return false;
        }

        // Verify stations exist in database by their codes
        Optional<BusStation> depStation = busStationRepository.findByStationCode(
                busDetails.getBusDepartureStation().getBusStationCode());
        Optional<BusStation> arrStation = busStationRepository.findByStationCode(
                busDetails.getBusArrivalStation().getBusStationCode());

        if (depStation.isEmpty()) {
            System.out.println("✗ Repository: Departure station code not found in database: " +
                    busDetails.getBusDepartureStation().getBusStationCode());
            return false;
        }

        if (arrStation.isEmpty()) {
            System.out.println("✗ Repository: Arrival station code not found in database: " +
                    busDetails.getBusArrivalStation().getBusStationCode());
            return false;
        }

        return true;
    }
}