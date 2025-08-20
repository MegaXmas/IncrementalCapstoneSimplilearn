package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.BusDetails;
import com.example.travelbuddybackend.models.FlightDetails;
import com.example.travelbuddybackend.repository.FlightDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightDetailsService {

    private final FlightDetailsRepository flightDetailsRepository;

    @Autowired
    public FlightDetailsService(FlightDetailsRepository flightDetailsRepository) {
        this.flightDetailsRepository = flightDetailsRepository;
    }

    /**
     * Get all flight details from the database
     * @return List of all flight details (empty list if none found or error occurs)
     */
    public List<FlightDetails> getAllFlightDetails() {
        return flightDetailsRepository.findAll();
    }

    /**
     * Get flight details by their ID
     * @param id The flight details ID to search for
     * @return Optional containing the flight details if found, empty otherwise
     */
    public Optional<FlightDetails> getFlightDetailsById(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Flight details ID cannot be null");
            return Optional.empty();
        }
        return flightDetailsRepository.findById(id);
    }

    /**
     * Get flight details by flight number
     * @param flightNumber The flight number to search for
     * @return Optional containing the flight details if found, empty otherwise
     */
    public Optional<FlightDetails> getFlightDetailsByNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: Flight number cannot be null or empty");
            return Optional.empty();
        }
        return flightDetailsRepository.findByFlightNumber(flightNumber);
    }

    /**
     * Get flight details by origin and destination (uses repository method)
     * @param origin The origin airport/city
     * @param destination The destination airport/city
     * @return List of flights matching the route
     */
    public List<FlightDetails> getFlightsByRoute(String origin, String destination) {
        if (origin == null || origin.trim().isEmpty() ||
                destination == null || destination.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both origin and destination are required");
            return new ArrayList<>();
        }
        return flightDetailsRepository.findByOriginAndDestination(origin, destination);
    }

    /**
     * Add new flight details to the database
     * @param flightDetails The flight details to add
     * @return true if flight details were successfully added, false otherwise
     */
    public boolean addFlightDetails(FlightDetails flightDetails) {
        if (flightDetails == null) {
            System.out.println("✗ Service Error: Cannot add null flight details");
            return false;
        }

        boolean success = flightDetailsRepository.createFlightDetails(flightDetails);
        if (success) {
            System.out.println("✓ Service: Flight details successfully added through service layer");
        } else {
            System.out.println("✗ Service: Failed to add flight details through service layer");
        }
        return success;
    }

    /**
     * Update existing flight details in the database
     * @param flightDetails The flight details with updated information
     * @return true if flight details were successfully updated, false otherwise
     */
    public boolean updateFlightDetails(FlightDetails flightDetails) {
        if (flightDetails == null) {
            System.out.println("✗ Service Error: Cannot update null flight details");
            return false;
        }

        if (flightDetails.getId() == null || flightDetails.getId() <= 0) {
            System.out.println("✗ Service Error: Flight details must have a valid ID for update");
            return false;
        }

        boolean success = flightDetailsRepository.updateFlightDetails(flightDetails);
        if (success) {
            System.out.println("✓ Service: Flight details successfully updated through service layer");
        } else {
            System.out.println("✗ Service: Failed to update flight details through service layer");
        }
        return success;
    }

    /**
     * Delete flight details from the database
     * @param id The ID of the flight details to delete
     * @return true if flight details were successfully deleted, false otherwise
     */
    public boolean deleteFlightDetails(Integer id) {
        if (id == null) {
            System.out.println("✗ Service Error: Flight details ID cannot be null");
            return false;
        }

        if (id <= 0) {
            System.out.println("✗ Service Error: Invalid flight details ID: " + id);
            return false;
        }

        boolean success = flightDetailsRepository.deleteFlightDetails(id);
        if (success) {
            System.out.println("✓ Service: Flight details successfully deleted through service layer");
        } else {
            System.out.println("✗ Service: Failed to delete flight details through service layer");
        }
        return success;
    }

    /**
     * Check if flight details exist in the database by ID
     * @param id The flight details ID to check
     * @return true if flight details exist, false otherwise
     */
    public boolean flightDetailsExists(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return getFlightDetailsById(id).isPresent();
    }

    /**
     * Check if flight details exist in the database by flight number
     * @param flightNumber The flight number to check
     * @return true if flight details exist, false otherwise
     */
    public boolean flightDetailsExistsByNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            return false;
        }
        return getFlightDetailsByNumber(flightNumber).isPresent();
    }

    /**
     * Get the total number of flight details in the database
     * @return The count of all flight details
     */
    public int getFlightDetailsCount() {
        List<FlightDetails> flightDetailsList = getAllFlightDetails();
        return flightDetailsList.size();
    }

    public List<FlightDetails> findFlightsByFlightNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            System.out.println("✗ Service Error: flightNumber cannot be null or empty");
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> flight.getFlightNumber().equalsIgnoreCase(flightNumber))
                .collect(Collectors.toList());
    }

    /**
     * Find flights by airline
     * @param airline The airline to search for
     * @return List of flights operated by the specified airline
     */
    public List<FlightDetails> findFlightsByAirline(String airline) {
        if (airline == null || airline.trim().isEmpty()) {
            System.out.println("✗ Service Error: Airline cannot be null or empty");
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> flight.getFlightAirline().equalsIgnoreCase(airline))
                .collect(Collectors.toList());
    }

    /**
     * Find flights by origin airport/city
     * @param origin The origin to search for
     * @return List of flights departing from the specified origin
     */
    public List<FlightDetails> findFlightsByOrigin(String origin) {
        if (origin == null || origin.trim().isEmpty()) {
            System.out.println("✗ Service Error: Origin cannot be null or empty");
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> flight.getFlightOrigin().equalsIgnoreCase(origin))
                .collect(Collectors.toList());
    }

    /**
     * Find flights by destination airport/city
     * @param destination The destination to search for
     * @return List of flights arriving at the specified destination
     */
    public List<FlightDetails> findFlightsByDestination(String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            System.out.println("✗ Service Error: Destination cannot be null or empty");
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> flight.getFlightDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
    }

    /**
     * Find bus details by departure and arrival airports
     * @param origin The departure airport to search for
     * @param destination The arrival airport to search for
     * @return List of flight details matching the route
     */
    public List<FlightDetails> findFlightDetailsByRoute(String origin, String destination) {
        if (origin == null || origin.trim().isEmpty() ||
                destination == null || destination.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both departure and arrival airports are required");
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flights -> flights.getFlightOrigin().equalsIgnoreCase(origin) &&
                        flights.getFlightDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
    }

    /**
     * Find flights by departure date
     * @param departureDate The departure date to search for (format should match database)
     * @return List of flights departing on the specified date
     */
    public List<FlightDetails> findFlightsByDepartureDate(String departureDate) {
        if (departureDate == null || departureDate.trim().isEmpty()) {
            System.out.println("✗ Service Error: Departure date cannot be null or empty");
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> flight.getFlightDepartureDate().equals(departureDate))
                .collect(Collectors.toList());
    }

    public List<FlightDetails> findFlightsByArrivalDate(String arrival) {
        if (arrival == null || arrival.trim().isEmpty()) {
            System.out.println("✗ Service Error: arrival date cannot be null or empty");
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> flight.getFlightArrivalDate().equals(arrival))
                .collect(Collectors.toList());
    }

    public List<FlightDetails> findFlightsByDepartureTime(String departureTime) {
        if (departureTime == null || departureTime.trim().isEmpty()) {
            System.out.println("✗ Service Error: Departure time cannot be null or empty");
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> flight.getFlightDepartureTime().equals(departureTime))
                .collect(Collectors.toList());
    }

    public List<FlightDetails> findFlightsByArrivalTime(String arrivalTime) {
        if (arrivalTime == null || arrivalTime.trim().isEmpty()) {
            System.out.println("✗ Service Error: Arrival time cannot be null or empty");
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> flight.getFlightArrivalTime().equals(arrivalTime))
                .collect(Collectors.toList());
    }

    /**
     * Find flights within a price range
     * @param minPrice The minimum price (as string to match model)
     * @param maxPrice The maximum price (as string to match model)
     * @return List of flights within the specified price range
     */
    public List<FlightDetails> findFlightsByPriceRange(String minPrice, String maxPrice) {
        if (minPrice == null || minPrice.trim().isEmpty() ||
                maxPrice == null || maxPrice.trim().isEmpty()) {
            System.out.println("✗ Service Error: Both minimum and maximum prices are required");
            return new ArrayList<>();
        }

        try {
            double min = Double.parseDouble(minPrice);
            double max = Double.parseDouble(maxPrice);

            List<FlightDetails> allFlights = getAllFlightDetails();
            return allFlights.stream()
                    .filter(flight -> {
                        try {
                            double price = Double.parseDouble(flight.getFlightPrice());
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
     * Search flights by multiple criteria (for advanced search functionality)
     * @param searchTerm The search term to match against flight number, airline, origin, or destination
     * @return List of flights matching the search term
     */
    public List<FlightDetails> searchFlights(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("✗ Service Error: Search term cannot be null or empty");
            return new ArrayList<>();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        List<FlightDetails> allFlights = getAllFlightDetails();

        return allFlights.stream()
                .filter(flight ->
                        flight.getFlightNumber().toLowerCase().contains(lowerSearchTerm) ||
                        flight.getFlightAirline().toLowerCase().contains(lowerSearchTerm) ||
                        flight.getFlightOrigin().toLowerCase().contains(lowerSearchTerm) ||
                        flight.getFlightDestination().toLowerCase().contains(lowerSearchTerm) ||
                        flight.getFlightDepartureDate().toLowerCase().contains(lowerSearchTerm) ||
                        flight.getFlightArrivalDate().toLowerCase().contains(lowerSearchTerm) ||
                        flight.getFlightDepartureTime().toLowerCase().contains(lowerSearchTerm) ||
                        flight.getFlightArrivalTime().toLowerCase().contains(lowerSearchTerm) ||
                        flight.getFlightPrice().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Validate flight number format (basic validation)
     * @param flightNumber The flight number to validate
     * @return true if valid format, false otherwise
     */
    public boolean isValidFlightNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            return false;
        }
        // Basic validation: not empty and contains at least 2 characters
        return flightNumber.trim().length() >= 2;
    }

    /**
     * Validate flight details before creating/updating
     * @param flightDetails The flight details to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidFlightDetails(FlightDetails flightDetails) {
        if (flightDetails == null) {
            System.out.println("✗ Service Error: Flight details cannot be null");
            return false;
        }

        if (!isValidFlightNumber(flightDetails.getFlightNumber())) {
            System.out.println("✗ Service Error: Invalid flight number format");
            return false;
        }

        if (flightDetails.getFlightAirline() == null || flightDetails.getFlightAirline().trim().isEmpty()) {
            System.out.println("✗ Service Error: Flight airline is required");
            return false;
        }

        if (flightDetails.getFlightOrigin() == null || flightDetails.getFlightOrigin().trim().isEmpty()) {
            System.out.println("✗ Service Error: Flight origin is required");
            return false;
        }

        if (flightDetails.getFlightDestination() == null || flightDetails.getFlightDestination().trim().isEmpty()) {
            System.out.println("✗ Service Error: Flight destination is required");
            return false;
        }

        if (flightDetails.getFlightOrigin().equalsIgnoreCase(flightDetails.getFlightDestination())) {
            System.out.println("✗ Service Error: Origin and destination cannot be the same");
            return false;
        }

        if (flightDetails.getFlightPrice() == null || flightDetails.getFlightPrice().trim().isEmpty()) {
            System.out.println("✗ Service Error: Flight price is required");
            return false;
        }

        // Validate price is numeric
        try {
            Double.parseDouble(flightDetails.getFlightPrice());
        } catch (NumberFormatException e) {
            System.out.println("✗ Service Error: Flight price must be a valid number");
            return false;
        }

        return true;
    }
}