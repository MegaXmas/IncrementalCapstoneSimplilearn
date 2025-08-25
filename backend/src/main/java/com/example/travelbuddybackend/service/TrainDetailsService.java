package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.TrainDetails;
import com.example.travelbuddybackend.models.TrainStation;
import com.example.travelbuddybackend.repository.TrainDetailsRepository;
import com.example.travelbuddybackend.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainDetailsService {

    private final TrainDetailsRepository trainDetailsRepository;
    private final ValidatorService validatorService;

    @Autowired
    public TrainDetailsService(TrainDetailsRepository trainDetailsRepository,
                               ValidatorService validatorService) {
        this.trainDetailsRepository = trainDetailsRepository;
        this.validatorService = validatorService;
    }

    public List<TrainDetails> getAllTrainDetails() {
        return trainDetailsRepository.findAll();
    }

    public Optional<TrainDetails> getTrainDetailsById(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Service Error: Train details ID cannot be null or less than 1");
            return Optional.empty();
        }
        return trainDetailsRepository.findById(id);
    }

    public Optional<TrainDetails> getTrainDetailsByNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: Train number cannot be null or empty");
            return Optional.empty();
        }
        return trainDetailsRepository.findByTrainNumber(trainNumber);
    }

    public List<TrainDetails> getTrainsByRoute(String departureStation, String arrivalStation) {
        if (departureStation == null || departureStation.trim().isEmpty() ||
                arrivalStation == null || arrivalStation.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both departure and arrival stations are required");
            return new ArrayList<>();
        }
        return trainDetailsRepository.findByStations(departureStation, arrivalStation);
    }

    public List<TrainDetails> getTrainsByRoute(TrainStation departureStation, TrainStation arrivalStation) {
        if (departureStation == null || arrivalStation == null) {
            System.out.println("✗ Service Error: Both stations are required");
            return new ArrayList<>();
        }
        return trainDetailsRepository.findByStations(departureStation, arrivalStation);
    }

    public List<TrainDetails> getTrainsByDepartureDate(String departureDate) {
        if (departureDate == null || departureDate.trim().isEmpty()) {
            System.out.println("✗ Service Error: Departure date cannot be null or empty");
            return new ArrayList<>();
        }
        return trainDetailsRepository.findByDepartureDate(departureDate);
    }

    public boolean addTrainDetails(TrainDetails trainDetails) {
        if (!isValidTrainDetailsForService(trainDetails)) {
            return false;
        }

        boolean success = trainDetailsRepository.createTrainDetails(trainDetails);
        if (success) {
            System.out.println("✓ Service: Train details added successfully");
        } else {
            System.out.println("✗ Service: Failed to add train details");
        }
        return success;
    }

    public boolean updateTrainDetails(TrainDetails trainDetails) {
        if (!isValidTrainDetailsForService(trainDetails)) {
            return false;
        }

        if (trainDetails.getId() == null || trainDetails.getId() <= 0) {
            System.out.println("✗ Service Error: Valid ID required for update");
            return false;
        }

        // Check if train details exist before updating
        if (!trainDetailsExists(trainDetails.getId())) {
            System.out.println("✗ Service Error: Train details not found for update");
            return false;
        }

        boolean success = trainDetailsRepository.updateTrainDetails(trainDetails);
        if (success) {
            System.out.println("✓ Service: Train details updated successfully");
        } else {
            System.out.println("✗ Service: Failed to update train details");
        }
        return success;
    }

    public boolean deleteTrainDetails(Integer id) {
        if (id == null || id <= 0) {
            System.out.println("✗ Service Error: Invalid train details ID");
            return false;
        }

        // Check if train details exist before deleting
        if (!trainDetailsExists(id)) {
            System.out.println("✗ Service Error: Train details not found for deletion");
            return false;
        }

        boolean success = trainDetailsRepository.deleteTrainDetails(id);
        if (success) {
            System.out.println("✓ Service: Train details deleted successfully");
        } else {
            System.out.println("✗ Service: Failed to delete train details");
        }
        return success;
    }

    // Business logic validation at service level
    public boolean trainDetailsExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return getTrainDetailsById(id).isPresent();
    }

    public boolean trainDetailsExistsByNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            return false;
        }
        return getTrainDetailsByNumber(trainNumber).isPresent();
    }

    public int getTrainDetailsCount() {
        return getAllTrainDetails().size();
    }

    public List<TrainDetails> findTrainsByTrainNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: Train number cannot be null or empty");
            return new ArrayList<>();
        }

        List<TrainDetails> allTrains = getAllTrainDetails();
        return allTrains.stream()
                .filter(train -> train.getTrainNumber().equalsIgnoreCase(trainNumber))
                .collect(Collectors.toList());
    }

    public List<TrainDetails> findTrainsByLine(String trainLine) {
        if (trainLine == null || trainLine.trim().isEmpty()) {
            System.out.println("✗ Service Error: Train line cannot be null or empty");
            return new ArrayList<>();
        }

        List<TrainDetails> allTrains = getAllTrainDetails();
        return allTrains.stream()
                .filter(train -> train.getTrainLine().equalsIgnoreCase(trainLine))
                .collect(Collectors.toList());
    }

    public List<TrainDetails> searchTrains(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        List<TrainDetails> allTrains = getAllTrainDetails();

        return allTrains.stream()
                .filter(train -> {
                    // Safe null checking for train station names
                    String departureName = train.getTrainDepartureStation() != null ?
                            train.getTrainDepartureStation().getTrainStationFullName() : "";
                    String arrivalName = train.getTrainArrivalStation() != null ?
                            train.getTrainArrivalStation().getTrainStationFullName() : "";

                    return train.getTrainNumber().toLowerCase().contains(lowerSearchTerm) ||
                            train.getTrainLine().toLowerCase().contains(lowerSearchTerm) ||
                            departureName.toLowerCase().contains(lowerSearchTerm) ||
                            arrivalName.toLowerCase().contains(lowerSearchTerm) ||
                            train.getTrainDepartureDate().toLowerCase().contains(lowerSearchTerm) ||
                            train.getTrainRidePrice().toLowerCase().contains(lowerSearchTerm);
                })
                .collect(Collectors.toList());
    }

    public List<TrainDetails> findTrainsByPriceRange(String minPrice, String maxPrice) {
        if (minPrice == null || minPrice.trim().isEmpty() ||
                maxPrice == null || maxPrice.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both minimum and maximum prices are required");
            return new ArrayList<>();
        }

        try {
            double min = Double.parseDouble(minPrice);
            double max = Double.parseDouble(maxPrice);

            if (min < 0 || max < 0 || min > max) {
                System.out.println("✗ Service Error: Invalid price range");
                return new ArrayList<>();
            }

            List<TrainDetails> allTrains = getAllTrainDetails();
            return allTrains.stream()
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

    // Service-level validation (includes business rules)
    private boolean isValidTrainDetailsForService(TrainDetails trainDetails) {
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

        // Date validation
        if (!validatorService.isValidDate(trainDetails.getTrainDepartureDate()) ||
                !validatorService.isValidDate(trainDetails.getTrainArrivalDate())) {
            System.out.println("✗ Service Error: Invalid date format");
            return false;
        }

        // Time validation
        if (!validatorService.isValidTime(trainDetails.getTrainDepartureTime()) ||
                !validatorService.isValidTime(trainDetails.getTrainArrivalTime())) {
            System.out.println("✗ Service Error: Invalid time format");
            return false;
        }

        // Price validation
        if (trainDetails.getTrainRidePrice() == null || trainDetails.getTrainRidePrice().trim().isEmpty()) {
            System.out.println("✗ Service Error: Price is required");
            return false;
        }

        try {
            double price = Double.parseDouble(trainDetails.getTrainRidePrice());
            if (price < 0) {
                System.out.println("✗ Service Error: Price cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Service Error: Price must be a valid number");
            return false;
        }

        return true;
    }
}