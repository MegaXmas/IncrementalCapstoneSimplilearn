package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.TrainDetails;
import com.example.travelbuddybackend.models.TrainStation;
import com.example.travelbuddybackend.repository.TrainDetailsRepository;
import com.example.travelbuddybackend.repository.TrainStationRepository;
import com.example.travelbuddybackend.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Train Details Service - Business Logic Layer
 *
 * Fixed Architecture:
 * - Only depends on repositories (no service-to-service dependencies)
 * - Handles business validation and logic
 * - Coordinates repository operations
 * - No circular dependencies
 */
@Service
public class TrainDetailsService {

    private final TrainDetailsRepository trainDetailsRepository;
    private final TrainStationRepository trainStationRepository;
    private final ValidatorService validatorService;

    @Autowired
    public TrainDetailsService(TrainDetailsRepository trainDetailsRepository,
                               TrainStationRepository trainStationRepository,
                               ValidatorService validatorService) {
        this.trainDetailsRepository = trainDetailsRepository;
        this.trainStationRepository = trainStationRepository;
        this.validatorService = validatorService;
    }

    // ============================================================================
    // CORE BUSINESS OPERATIONS
    // ============================================================================

    public List<TrainDetails> getAllTrainDetails() {
        return trainDetailsRepository.findAll();
    }

    public Optional<TrainDetails> getTrainDetailsById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Service Error: Train ID must be a positive integer");
            return Optional.empty();
        }
        return trainDetailsRepository.findById(id);
    }

    public Optional<TrainDetails> getTrainDetailsByNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: Train number cannot be null or empty");
            return Optional.empty();
        }
        return trainDetailsRepository.findByTrainNumber(trainNumber.trim());
    }

    public List<TrainDetails> getTrainsByRoute(String departureStationCode, String arrivalStationCode) {
        if (departureStationCode == null || departureStationCode.trim().isEmpty() ||
                arrivalStationCode == null || arrivalStationCode.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both departure and arrival station codes are required");
            return new ArrayList<>();
        }

        // Find stations by codes using repository (not service)
        Optional<TrainStation> departureStation = trainStationRepository.findByStationCode(departureStationCode.toUpperCase().trim());
        Optional<TrainStation> arrivalStation = trainStationRepository.findByStationCode(arrivalStationCode.toUpperCase().trim());

        if (departureStation.isEmpty()) {
            System.out.println("✗ Service Error: Departure station not found: " + departureStationCode);
            return new ArrayList<>();
        }

        if (arrivalStation.isEmpty()) {
            System.out.println("✗ Service Error: Arrival station not found: " + arrivalStationCode);
            return new ArrayList<>();
        }

        return trainDetailsRepository.findByRoute(departureStation.get().getId(), arrivalStation.get().getId());
    }

    public List<TrainDetails> getTrainsByRoute(TrainStation departureStation, TrainStation arrivalStation) {
        if (departureStation == null || arrivalStation == null) {
            System.out.println("✗ Service Error: Both stations are required");
            return new ArrayList<>();
        }

        if (departureStation.getId() == null || arrivalStation.getId() == null) {
            System.out.println("✗ Service Error: Station IDs are required");
            return new ArrayList<>();
        }

        return trainDetailsRepository.findByRoute(departureStation.getId(), arrivalStation.getId());
    }

    public List<TrainDetails> getTrainsByDepartureDate(String departureDate) {
        if (departureDate == null || departureDate.trim().isEmpty()) {
            System.out.println("✗ Service Error: Departure date cannot be null or empty");
            return new ArrayList<>();
        }

        if (!validatorService.isValidDate(departureDate)) {
            System.out.println("✗ Service Error: Invalid date format. Use YYYY-MM-DD");
            return new ArrayList<>();
        }

        return trainDetailsRepository.findByDepartureDate(departureDate);
    }

    public boolean addTrainDetails(TrainDetails trainDetails) {
        if (!isValidForService(trainDetails)) {
            return false;
        }

        // Check for duplicate train number
        if (trainDetailsRepository.findByTrainNumber(trainDetails.getTrainNumber()).isPresent()) {
            System.out.println("✗ Service Error: Train number already exists: " + trainDetails.getTrainNumber());
            return false;
        }

        boolean success = trainDetailsRepository.createTrainDetails(trainDetails);
        if (success) {
            System.out.println("✓ Service: Train added successfully");
        } else {
            System.out.println("✗ Service: Failed to add train");
        }
        return success;
    }

    public boolean updateTrainDetails(TrainDetails trainDetails) {
        if (!isValidForService(trainDetails)) {
            return false;
        }

        if (trainDetails.getId() == null || trainDetails.getId() <= 0) {
            System.out.println("✗ Service Error: Valid train ID required for update");
            return false;
        }

        // Check if train exists
        if (trainDetailsRepository.findById(trainDetails.getId()).isEmpty()) {
            System.out.println("✗ Service Error: Train not found for update");
            return false;
        }

        // Check for duplicate train number (excluding current train)
        Optional<TrainDetails> existingTrain = trainDetailsRepository.findByTrainNumber(trainDetails.getTrainNumber());
        if (existingTrain.isPresent() && !existingTrain.get().getId().equals(trainDetails.getId())) {
            System.out.println("✗ Service Error: Train number already exists: " + trainDetails.getTrainNumber());
            return false;
        }

        boolean success = trainDetailsRepository.updateTrainDetails(trainDetails);
        if (success) {
            System.out.println("✓ Service: Train updated successfully");
        } else {
            System.out.println("✗ Service: Failed to update train");
        }
        return success;
    }

    public boolean deleteTrainDetails(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Service Error: Valid train ID required for deletion");
            return false;
        }

        // Check if train exists before deletion
        if (trainDetailsRepository.findById(id).isEmpty()) {
            System.out.println("✗ Service Error: Train not found for deletion");
            return false;
        }

        boolean success = trainDetailsRepository.deleteTrainDetails(id);
        if (success) {
            System.out.println("✓ Service: Train deleted successfully");
        } else {
            System.out.println("✗ Service: Failed to delete train");
        }
        return success;
    }

    // ============================================================================
    // BUSINESS LOGIC METHODS
    // ============================================================================

    public boolean trainDetailsExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return trainDetailsRepository.findById(id).isPresent();
    }

    public boolean trainDetailsExistsByNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            return false;
        }
        return trainDetailsRepository.findByTrainNumber(trainNumber.trim()).isPresent();
    }

    public int getTrainDetailsCount() {
        return trainDetailsRepository.findAll().size();
    }

    // ============================================================================
    // SEARCH AND FILTER METHODS
    // ============================================================================

    public List<TrainDetails> findTrainsByLine(String trainLine) {
        if (trainLine == null || trainLine.trim().isEmpty()) {
            System.out.println("✗ Service Error: Train line cannot be null or empty");
            return new ArrayList<>();
        }

        return getAllTrainDetails().stream()
                .filter(train -> train.getTrainLine().toLowerCase().contains(trainLine.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    public List<TrainDetails> findTrainsByPriceRange(String minPrice, String maxPrice) {
        if (minPrice == null || minPrice.trim().isEmpty() ||
                maxPrice == null || maxPrice.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both minimum and maximum prices are required");
            return new ArrayList<>();
        }

        try {
            double min = Double.parseDouble(minPrice.trim());
            double max = Double.parseDouble(maxPrice.trim());

            if (min < 0 || max < 0 || min > max) {
                System.out.println("✗ Service Error: Invalid price range");
                return new ArrayList<>();
            }

            return getAllTrainDetails().stream()
                    .filter(train -> {
                        try {
                            double price = Double.parseDouble(train.getTrainRidePrice());
                            return price >= min && price <= max;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            System.out.println("✗ Service Error: Invalid price format");
            return new ArrayList<>();
        }
    }

    public List<TrainDetails> searchTrains(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        return getAllTrainDetails().stream()
                .filter(train -> {
                    String depName = train.getTrainDepartureStation() != null ?
                            train.getTrainDepartureStation().getTrainStationFullName().toLowerCase() : "";
                    String depCode = train.getTrainDepartureStation() != null ?
                            train.getTrainDepartureStation().getTrainStationCode().toLowerCase() : "";
                    String arrName = train.getTrainArrivalStation() != null ?
                            train.getTrainArrivalStation().getTrainStationFullName().toLowerCase() : "";
                    String arrCode = train.getTrainArrivalStation() != null ?
                            train.getTrainArrivalStation().getTrainStationCode().toLowerCase() : "";

                    return train.getTrainNumber().toLowerCase().contains(lowerSearchTerm) ||
                            train.getTrainLine().toLowerCase().contains(lowerSearchTerm) ||
                            depName.contains(lowerSearchTerm) ||
                            depCode.contains(lowerSearchTerm) ||
                            arrName.contains(lowerSearchTerm) ||
                            arrCode.contains(lowerSearchTerm) ||
                            train.getTrainDepartureDate().contains(lowerSearchTerm) ||
                            train.getTrainRidePrice().contains(lowerSearchTerm);
                })
                .collect(Collectors.toList());
    }

    // ============================================================================
    // BUSINESS VALIDATION
    // ============================================================================

    private boolean isValidForService(TrainDetails trainDetails) {
        if (trainDetails == null) {
            System.out.println("✗ Service Error: Train details cannot be null");
            return false;
        }

        // Basic field validation
        if (trainDetails.getTrainNumber() == null || trainDetails.getTrainNumber().trim().isEmpty()) {
            System.out.println("✗ Service Error: Train number is required");
            return false;
        }

        if (trainDetails.getTrainLine() == null || trainDetails.getTrainLine().trim().isEmpty()) {
            System.out.println("✗ Service Error: Train line is required");
            return false;
        }

        if (trainDetails.getTrainDepartureStation() == null || trainDetails.getTrainArrivalStation() == null) {
            System.out.println("✗ Service Error: Both departure and arrival stations are required");
            return false;
        }

        // Business logic validation
        if (trainDetails.getTrainDepartureStation().getId().equals(trainDetails.getTrainArrivalStation().getId())) {
            System.out.println("✗ Service Error: Departure and arrival stations cannot be the same");
            return false;
        }

        // Validate stations exist in database
        if (trainStationRepository.findById(trainDetails.getTrainDepartureStation().getId()).isEmpty()) {
            System.out.println("✗ Service Error: Departure station not found in database");
            return false;
        }

        if (trainStationRepository.findById(trainDetails.getTrainArrivalStation().getId()).isEmpty()) {
            System.out.println("✗ Service Error: Arrival station not found in database");
            return false;
        }

        // Date and time validation
        if (!validatorService.isValidDate(trainDetails.getTrainDepartureDate()) ||
                !validatorService.isValidDate(trainDetails.getTrainArrivalDate())) {
            System.out.println("✗ Service Error: Invalid date format. Use YYYY-MM-DD");
            return false;
        }

        if (!validatorService.isValidTime(trainDetails.getTrainDepartureTime()) ||
                !validatorService.isValidTime(trainDetails.getTrainArrivalTime())) {
            System.out.println("✗ Service Error: Invalid time format. Use HH:MM");
            return false;
        }

        // Price validation
        if (trainDetails.getTrainRidePrice() == null || trainDetails.getTrainRidePrice().trim().isEmpty()) {
            System.out.println("✗ Service Error: Train price is required");
            return false;
        }

        try {
            double price = Double.parseDouble(trainDetails.getTrainRidePrice());
            if (price <= 0) {
                System.out.println("✗ Service Error: Train price must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Service Error: Train price must be a valid number");
            return false;
        }

        return true;
    }
}