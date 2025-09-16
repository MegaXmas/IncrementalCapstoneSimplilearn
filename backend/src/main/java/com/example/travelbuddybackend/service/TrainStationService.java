package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.TrainStation;
import com.example.travelbuddybackend.repository.TrainStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainStationService {

    private final TrainStationRepository trainStationRepository;

    @Autowired
    public TrainStationService(TrainStationRepository trainStationRepository) {
        this.trainStationRepository = trainStationRepository;
    }

    /**
     * Get all train stations from the database
     * @return List of all train stations (empty list if none found or error occurs)
     */
    public List<TrainStation> getAllTrainStations() {
        return trainStationRepository.findAll();
    }

    /**
     * Get train station by its ID
     * @param id The train station ID to search for
     * @return Optional containing the train station if found, empty otherwise
     */
    public Optional<TrainStation> getTrainStationById(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Train station ID cannot be null");
            return Optional.empty();
        }
        return trainStationRepository.findById(id);
    }

    /**
     * Get train station by its station code
     * @param stationCode The station code to search for
     * @return Optional containing the train station if found, empty otherwise
     */
    public Optional<TrainStation> getTrainStationByCode(String stationCode) {
        if (stationCode == null || stationCode.trim().isEmpty()) {
            System.out.println("✗ Service Error: Station code cannot be null or empty");
            return Optional.empty();
        }
        return trainStationRepository.findByStationCode(stationCode);
    }

    /**
     * Get train stations by city (uses repository method)
     * @param city The city to search for
     * @return List of train stations in the specified city
     */
    public List<TrainStation> getTrainStationsByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            System.out.println("✗ Service Error: City cannot be null or empty");
            return new ArrayList<>();
        }
        return trainStationRepository.findByCity(city);
    }

    /**
     * Add new train station to the database
     * @param trainStation The train station to add
     * @return true if train station was successfully added, false otherwise
     */
    public boolean addTrainStation(TrainStation trainStation) {
        if (trainStation == null) {
            System.out.println("✗ Service Error: Cannot add null train station");
            return false;
        }

        boolean success = trainStationRepository.createTrainStation(trainStation);
        if (success) {
            System.out.println("✓ Service: Train station successfully added through service layer");
        } else {
            System.out.println("✗ Service: Failed to add train station through service layer");
        }
        return success;
    }

    /**
     * Update existing train station in the database
     * @param trainStation The train station with updated information
     * @return true if train station was successfully updated, false otherwise
     */
    public boolean updateTrainStation(TrainStation trainStation) {
        if (trainStation == null) {
            System.out.println("✗ Service Error: Cannot update null train station");
            return false;
        }

        if (trainStation.getId() == null || trainStation.getId() <= 0) {
            System.out.println("✗ Service Error: Train station must have a valid ID for update");
            return false;
        }

        boolean success = trainStationRepository.updateTrainStation(trainStation);
        if (success) {
            System.out.println("✓ Service: Train station successfully updated through service layer");
        } else {
            System.out.println("✗ Service: Failed to update train station through service layer");
        }
        return success;
    }

    /**
     * Delete train station from the database
     * @param id The ID of the train station to delete
     * @return true if train station was successfully deleted, false otherwise
     */
    public boolean deleteTrainStation(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Train station ID cannot be null");
            return false;
        }

        if (id <= 0) {
            System.out.println("✗ Service Error: Invalid train station ID: " + id);
            return false;
        }

        boolean success = trainStationRepository.deleteTrainStation(id);
        if (success) {
            System.out.println("✓ Service: Train station successfully deleted through service layer");
        } else {
            System.out.println("✗ Service: Failed to delete train station through service layer");
        }
        return success;
    }

    /**
     * Check if train station exists in the database by ID
     * @param id The train station ID to check
     * @return true if train station exists, false otherwise
     */
    public boolean trainStationExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return getTrainStationById(id).isPresent();
    }

    /**
     * Check if train station exists in the database by station code
     * @param stationCode The station code to check
     * @return true if train station exists, false otherwise
     */
    public boolean trainStationExistsByCode(String stationCode) {
        if (stationCode == null || stationCode.trim().isEmpty()) {
            return false;
        }
        return getTrainStationByCode(stationCode).isPresent();
    }

    /**
     * Get the total number of train stations in the database
     * @return The count of all train stations
     */
    public int getTrainStationCount() {
        List<TrainStation> trainStationList = getAllTrainStations();
        return trainStationList.size();
    }

    /**
     * Find train stations by partial name match (useful for search functionality)
     * @param partialName The partial name to search for
     * @return List of train stations whose names contain the partial name
     */
    public List<TrainStation> findTrainStationsByPartialName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            System.out.println("✗ Service Error: Partial name cannot be null or empty");
            return new ArrayList<>();
        }

        List<TrainStation> allTrainStations = getAllTrainStations();
        return allTrainStations.stream()
                .filter(station -> station.getTrainStationFullName().toLowerCase()
                        .contains(partialName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<TrainStation> findTrainStationsByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            System.out.println("✗ Service Error: Partial name cannot be null or empty");
            return new ArrayList<>();
        }

        List<TrainStation> allTrainStations = getAllTrainStations();
        return allTrainStations.stream()
                .filter(station -> station.getTrainStationCode().toLowerCase()
                        .contains(code.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Find train stations by partial city match (useful for autocomplete)
     * @param partialCity The partial city name to search for
     * @return List of train stations in cities containing the partial name
     */
    public List<TrainStation> findTrainStationsByPartialCity(String partialCity) {
        if (partialCity == null || partialCity.trim().isEmpty()) {
            System.out.println("✗ Service Error: Partial city name cannot be null or empty");
            return new ArrayList<>();
        }

        List<TrainStation> allTrainStations = getAllTrainStations();
        return allTrainStations.stream()
                .filter(station -> station.getTrainStationCityLocation().toLowerCase()
                        .contains(partialCity.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Search train stations by multiple criteria (for advanced search functionality)
     * @param searchTerm The search term to match against station name, code, or city
     * @return List of train stations matching the search term
     */
    public List<TrainStation> searchTrainStations(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        List<TrainStation> allTrainStations = getAllTrainStations();

        return allTrainStations.stream()
                .filter(station ->
                        station.getTrainStationFullName().toLowerCase().contains(lowerSearchTerm) ||
                                station.getTrainStationCode().toLowerCase().contains(lowerSearchTerm) ||
                                station.getTrainStationCityLocation().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Get unique cities that have train stations
     * @return List of unique city names that have train stations
     */
    public List<String> getUniqueCities() {
        List<TrainStation> allTrainStations = getAllTrainStations();
        return allTrainStations.stream()
                .map(TrainStation::getTrainStationCityLocation)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Validate train station code format (basic validation)
     * @param stationCode The station code to validate
     * @return true if valid format, false otherwise
     */
    public boolean isValidStationCode(String stationCode) {
        if (stationCode == null || stationCode.trim().isEmpty()) {
            return false;
        }
        // Basic validation: not empty and reasonable length (2-10 characters)
        return stationCode.trim().length() >= 2 && stationCode.trim().length() <= 10;
    }

    /**
     * Validate train station data before creating/updating
     * @param trainStation The train station to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidTrainStation(TrainStation trainStation) {
        if (trainStation == null) {
            System.out.println("✗ Service Error: Train station cannot be null");
            return false;
        }

        if (trainStation.getTrainStationFullName() == null || trainStation.getTrainStationFullName().trim().isEmpty()) {
            System.out.println("✗ Service Error: Train station full name is required");
            return false;
        }

        if (!isValidStationCode(trainStation.getTrainStationCode())) {
            System.out.println("✗ Service Error: Invalid train station code format");
            return false;
        }

        if (trainStation.getTrainStationCityLocation() == null || trainStation.getTrainStationCityLocation().trim().isEmpty()) {
            System.out.println("✗ Service Error: Train station city location is required");
            return false;
        }

        return true;
    }
}