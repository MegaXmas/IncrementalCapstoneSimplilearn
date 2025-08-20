package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.Airport;
import com.example.travelbuddybackend.models.BusStation;
import com.example.travelbuddybackend.repository.BusStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusStationService {

    private final BusStationRepository busStationRepository;

    @Autowired
    public BusStationService(BusStationRepository busStationRepository) {
        this.busStationRepository = busStationRepository;
    }

    /**
     * Get all bus stations from the database
     * @return List of all bus stations (empty list if none found or error occurs)
     */
    public List<BusStation> getAllBusStations() {
        return busStationRepository.findAll();
    }

    /**
     * Get bus station by its ID
     * @param id The bus station ID to search for
     * @return Optional containing the bus station if found, empty otherwise
     */
    public Optional<BusStation> getBusStationById(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Bus station ID cannot be null");
            return Optional.empty();
        }
        return busStationRepository.findById(id);
    }

    /**
     * Get bus station by its station code
     * @param stationCode The station code to search for
     * @return Optional containing the bus station if found, empty otherwise
     */
    public Optional<BusStation> getBusStationByCode(String stationCode) {
        if (stationCode == null || stationCode.trim().isEmpty()) {
            System.out.println("✗ Service Error: Station code cannot be null or empty");
            return Optional.empty();
        }
        return busStationRepository.findByStationCode(stationCode);
    }

    /**
     * Add new bus station to the database
     * @param busStation The bus station to add
     * @return true if bus station was successfully added, false otherwise
     */
    public boolean addBusStation(BusStation busStation) {
        if (busStation == null) {
            System.out.println("✗ Service Error: Cannot add null bus station");
            return false;
        }

        boolean success = busStationRepository.createBusStation(busStation);
        if (success) {
            System.out.println("✓ Service: Bus station successfully added through service layer");
        } else {
            System.out.println("✗ Service: Failed to add bus station through service layer");
        }
        return success;
    }

    /**
     * Update existing bus station in the database
     * @param busStation The bus station with updated information
     * @return true if bus station was successfully updated, false otherwise
     */
    public boolean updateBusStation(BusStation busStation) {
        if (busStation == null) {
            System.out.println("✗ Service Error: Cannot update null bus station");
            return false;
        }

        if (busStation.getId() == null || busStation.getId() <= 0) {
            System.out.println("✗ Service Error: Bus station must have a valid ID for update");
            return false;
        }

        boolean success = busStationRepository.updateBusStation(busStation);
        if (success) {
            System.out.println("✓ Service: Bus station successfully updated through service layer");
        } else {
            System.out.println("✗ Service: Failed to update bus station through service layer");
        }
        return success;
    }

    /**
     * Delete bus station from the database
     * @param id The ID of the bus station to delete
     * @return true if bus station was successfully deleted, false otherwise
     */
    public boolean deleteBusStation(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Bus station ID cannot be null");
            return false;
        }

        if (id <= 0) {
            System.out.println("✗ Service Error: Invalid bus station ID: " + id);
            return false;
        }

        boolean success = busStationRepository.deleteBusStation(id);
        if (success) {
            System.out.println("✓ Service: Bus station successfully deleted through service layer");
        } else {
            System.out.println("✗ Service: Failed to delete bus station through service layer");
        }
        return success;
    }

    /**
     * Check if bus station exists in the database by ID
     * @param id The bus station ID to check
     * @return true if bus station exists, false otherwise
     */
    public boolean busStationExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return getBusStationById(id).isPresent();
    }

    /**
     * Check if bus station exists in the database by station code
     * @param stationCode The station code to check
     * @return true if bus station exists, false otherwise
     */
    public boolean busStationExistsByCode(String stationCode) {
        if (stationCode == null || stationCode.trim().isEmpty()) {
            return false;
        }
        return getBusStationByCode(stationCode).isPresent();
    }

    /**
     * Get the total number of bus stations in the database
     * @return The count of all bus stations
     */
    public int getBusStationCount() {
        List<BusStation> busStationList = getAllBusStations();
        return busStationList.size();
    }

    /**
     * Find bus stations by city location
     * @param code The code to search for
     * @return bus station with specified code
     */
    public List<BusStation> findBusStationsByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            System.out.println("✗ Service Error: City location cannot be null or empty");
            return new ArrayList<>();
        }

        List<BusStation> allBusStations = getAllBusStations();
        return allBusStations.stream()
                .filter(station -> station.getBusStationCode().equalsIgnoreCase(code))
                .collect(Collectors.toList());
    }

    /**
     * Find bus stations by partial name match (useful for search functionality)
     * @param partialName The partial name to search for
     * @return List of bus stations whose names contain the partial name
     */
    public List<BusStation> findBusStationsByPartialName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            System.out.println("✗ Service Error: Partial name cannot be null or empty");
            return new ArrayList<>();
        }

        List<BusStation> allBusStations = getAllBusStations();
        return allBusStations.stream()
                .filter(station -> station.getBusStationFullName().toLowerCase()
                        .contains(partialName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<BusStation> findBusStationsByCityLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            System.out.println("✗ Service Error: location cannot be null or empty");
            return new ArrayList<>();
        }

        List<BusStation> allBusStations = getAllBusStations();
        return allBusStations.stream()
                .filter(station -> station.getBusStationCityLocation().toLowerCase()
                        .contains(location.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<BusStation> searchBusStations(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        List<BusStation> allBusStations = getAllBusStations();

        return allBusStations.stream()
                .filter(busStation ->
                        busStation.getBusStationFullName().toLowerCase().contains(lowerSearchTerm) ||
                        busStation.getBusStationCode().toLowerCase().contains(lowerSearchTerm) ||
                        busStation.getBusStationCityLocation().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }
}