package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.TrainDetails;
import com.example.travelbuddybackend.repository.TrainDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainDetailsService {

    private final TrainDetailsRepository trainDetailsRepository;

    @Autowired
    public TrainDetailsService(TrainDetailsRepository trainDetailsRepository) {
        this.trainDetailsRepository = trainDetailsRepository;
    }

    /**
     * Get all train details from the database
     * @return List of all train details (empty list if none found or error occurs)
     */
    public List<TrainDetails> getAllTrainDetails() {
        return trainDetailsRepository.findAll();
    }

    /**
     * Get train details by their ID
     * @param id The train details ID to search for
     * @return Optional containing the train details if found, empty otherwise
     */
    public Optional<TrainDetails> getTrainDetailsById(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Train details ID cannot be null");
            return Optional.empty();
        }
        return trainDetailsRepository.findById(id);
    }

    /**
     * Get train details by train number
     * @param trainNumber The train number to search for
     * @return Optional containing the train details if found, empty otherwise
     */
    public Optional<TrainDetails> getTrainDetailsByNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: Train number cannot be null or empty");
            return Optional.empty();
        }
        return trainDetailsRepository.findByTrainNumber(trainNumber);
    }

    /**
     * Get train details by departure and arrival stations (uses repository method)
     * @param departureStation The departure station
     * @param arrivalStation The arrival station
     * @return List of trains matching the route
     */
    public List<TrainDetails> getTrainsByRoute(String departureStation, String arrivalStation) {
        if (departureStation == null || departureStation.trim().isEmpty() ||
                arrivalStation == null || arrivalStation.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both departure and arrival stations are required");
            return new ArrayList<>();
        }
        return trainDetailsRepository.findByStations(departureStation, arrivalStation);
    }

    /**
     * Add new train details to the database
     * @param trainDetails The train details to add
     * @return true if train details were successfully added, false otherwise
     */
    public boolean addTrainDetails(TrainDetails trainDetails) {
        if (trainDetails == null) {
            System.out.println("✗ Service Error: Cannot add null train details");
            return false;
        }

        boolean success = trainDetailsRepository.createTrainDetails(trainDetails);
        if (success) {
            System.out.println("✓ Service: Train details successfully added through service layer");
        } else {
            System.out.println("✗ Service: Failed to add train details through service layer");
        }
        return success;
    }

    /**
     * Update existing train details in the database
     * @param trainDetails The train details with updated information
     * @return true if train details were successfully updated, false otherwise
     */
    public boolean updateTrainDetails(TrainDetails trainDetails) {
        if (trainDetails == null) {
            System.out.println("✗ Service Error: Cannot update null train details");
            return false;
        }

        if (trainDetails.getId() == null || trainDetails.getId() <= 0) {
            System.out.println("✗ Service Error: Train details must have a valid ID for update");
            return false;
        }

        boolean success = trainDetailsRepository.updateTrainDetails(trainDetails);
        if (success) {
            System.out.println("✓ Service: Train details successfully updated through service layer");
        } else {
            System.out.println("✗ Service: Failed to update train details through service layer");
        }
        return success;
    }

    /**
     * Delete train details from the database
     * @param id The ID of the train details to delete
     * @return true if train details were successfully deleted, false otherwise
     */
    public boolean deleteTrainDetails(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Train details ID cannot be null");
            return false;
        }

        if (id <= 0) {
            System.out.println("✗ Service Error: Invalid train details ID: " + id);
            return false;
        }

        boolean success = trainDetailsRepository.deleteTrainDetails(id);
        if (success) {
            System.out.println("✓ Service: Train details successfully deleted through service layer");
        } else {
            System.out.println("✗ Service: Failed to delete train details through service layer");
        }
        return success;
    }

    /**
     * Check if train details exist in the database by ID
     * @param id The train details ID to check
     * @return true if train details exist, false otherwise
     */
    public boolean trainDetailsExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return getTrainDetailsById(id).isPresent();
    }

    /**
     * Check if train details exist in the database by train number
     * @param trainNumber The train number to check
     * @return true if train details exist, false otherwise
     */
    public boolean trainDetailsExistsByNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            return false;
        }
        return getTrainDetailsByNumber(trainNumber).isPresent();
    }

    /**
     * Get the total number of train details in the database
     * @return The count of all train details
     */
    public int getTrainDetailsCount() {
        List<TrainDetails> trainDetailsList = getAllTrainDetails();
        return trainDetailsList.size();
    }

    /**
     * Find trains by train line/company
     * @param trainLine The train line to search for
     * @return List of trains operated by the specified line
     */
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

    /**
     * Find trains by departure date
     * @param departureDate The departure date to search for (format should match database)
     * @return List of trains departing on the specified date
     */
    public List<TrainDetails> findTrainsByDepartureDate(String departureDate) {
        if (departureDate == null || departureDate.trim().isEmpty()) {
            System.out.println("✗ Service Error: Departure date cannot be null or empty");
            return new ArrayList<>();
        }

        List<TrainDetails> allTrains = getAllTrainDetails();
        return allTrains.stream()
                .filter(train -> train.getTrainDepartureDate().equals(departureDate))
                .collect(Collectors.toList());
    }

    /**
     * Find trains by departure station
     * @param departureStation The departure station to search for
     * @return List of trains departing from the specified station
     */
    public List<TrainDetails> findTrainsByDepartureStation(String departureStation) {
        if (departureStation == null || departureStation.trim().isEmpty()) {
            System.out.println("✗ Service Error: Departure station cannot be null or empty");
            return new ArrayList<>();
        }

        List<TrainDetails> allTrains = getAllTrainDetails();
        return allTrains.stream()
                .filter(train -> train.getTrainDepartureStation().equalsIgnoreCase(departureStation))
                .collect(Collectors.toList());
    }

    /**
     * Find trains by arrival station
     * @param arrivalStation The arrival station to search for
     * @return List of trains arriving at the specified station
     */
    public List<TrainDetails> findTrainsByArrivalStation(String arrivalStation) {
        if (arrivalStation == null || arrivalStation.trim().isEmpty()) {
            System.out.println("✗ Service Error: Arrival station cannot be null or empty");
            return new ArrayList<>();
        }

        List<TrainDetails> allTrains = getAllTrainDetails();
        return allTrains.stream()
                .filter(train -> train.getTrainArrivalStation().equalsIgnoreCase(arrivalStation))
                .collect(Collectors.toList());
    }

    /**
     * Find trains within a price range
     * @param minPrice The minimum price (as string to match model)
     * @param maxPrice The maximum price (as string to match model)
     * @return List of trains within the specified price range
     */
    public List<TrainDetails> findTrainsByPriceRange(String minPrice, String maxPrice) {
        if (minPrice == null || minPrice.trim().isEmpty() ||
                maxPrice == null || maxPrice.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both minimum and maximum prices are required");
            return new ArrayList<>();
        }

        try {
            double min = Double.parseDouble(minPrice);
            double max = Double.parseDouble(maxPrice);

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

    /**
     * Find trains by ride duration range (useful for time-conscious travelers)
     * @param maxDurationHours Maximum duration in hours
     * @return List of trains with duration less than or equal to specified hours
     */
    public List<TrainDetails> findTrainsByMaxDuration(int maxDurationHours) {
        if (maxDurationHours <= 0) {
            System.out.println("✗ Service Error: Duration must be positive");
            return new ArrayList<>();
        }

        List<TrainDetails> allTrains = getAllTrainDetails();
        return allTrains.stream()
                .filter(train -> {
                    String duration = train.getTrainRideDuration();
                    if (duration != null && duration.contains("h")) {
                        try {
                            // Extract hours from duration string (assumes format like "2h 30m" or "2h")
                            String[] parts = duration.split("h");
                            int hours = Integer.parseInt(parts[0].trim());
                            return hours <= maxDurationHours;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * Search trains by multiple criteria (for advanced search functionality)
     * @param searchTerm The search term to match against train number, line, departure station, or arrival station
     * @return List of trains matching the search term
     */
    public List<TrainDetails> searchTrains(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        List<TrainDetails> allTrains = getAllTrainDetails();

        return allTrains.stream()
                .filter(train ->
                        train.getTrainNumber().toLowerCase().contains(lowerSearchTerm) ||
                                train.getTrainLine().toLowerCase().contains(lowerSearchTerm) ||
                                train.getTrainDepartureStation().toLowerCase().contains(lowerSearchTerm) ||
                                train.getTrainArrivalStation().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Validate train number format (basic validation)
     * @param trainNumber The train number to validate
     * @return true if valid format, false otherwise
     */
    public boolean isValidTrainNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            return false;
        }
        // Basic validation: not empty and contains at least 2 characters
        return trainNumber.trim().length() >= 2;
    }

    /**
     * Validate train details before creating/updating
     * @param trainDetails The train details to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidTrainDetails(TrainDetails trainDetails) {
        if (trainDetails == null) {
            System.out.println("✗ Service Error: Train details cannot be null");
            return false;
        }

        if (!isValidTrainNumber(trainDetails.getTrainNumber())) {
            System.out.println("✗ Service Error: Invalid train number format");
            return false;
        }

        if (trainDetails.getTrainLine() == null || trainDetails.getTrainLine().trim().isEmpty()) {
            System.out.println("✗ Service Error: Train line is required");
            return false;
        }

        if (trainDetails.getTrainDepartureStation() == null || trainDetails.getTrainDepartureStation().trim().isEmpty()) {
            System.out.println("✗ Service Error: Train departure station is required");
            return false;
        }

        if (trainDetails.getTrainArrivalStation() == null || trainDetails.getTrainArrivalStation().trim().isEmpty()) {
            System.out.println("✗ Service Error: Train arrival station is required");
            return false;
        }

        if (trainDetails.getTrainDepartureStation().equalsIgnoreCase(trainDetails.getTrainArrivalStation())) {
            System.out.println("✗ Service Error: Departure and arrival stations cannot be the same");
            return false;
        }

        if (trainDetails.getTrainRidePrice() == null || trainDetails.getTrainRidePrice().trim().isEmpty()) {
            System.out.println("✗ Service Error: Train ride price is required");
            return false;
        }

        // Validate price is numeric
        try {
            Double.parseDouble(trainDetails.getTrainRidePrice());
        } catch (NumberFormatException e) {
            System.out.println("✗ Service Error: Train ride price must be a valid number");
            return false;
        }

        return true;
    }
}