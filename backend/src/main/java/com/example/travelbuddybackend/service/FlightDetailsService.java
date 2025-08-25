package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.Airport;
import com.example.travelbuddybackend.models.BusDetails;
import com.example.travelbuddybackend.models.FlightDetails;
import com.example.travelbuddybackend.service.AirportService;
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

    private final AirportService airportService;

    @Autowired
    public FlightDetailsService(FlightDetailsRepository flightDetailsRepository,  AirportService airportService) {
        this.flightDetailsRepository = flightDetailsRepository;
        this.airportService = airportService;
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

        flightDetailsRepository.updateFlightDetails(flightDetails);

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

        flightDetailsRepository.updateFlightDetails(flightDetails);

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

        // Get matching airports from the search
        List<Airport> matchingAirports = airportService.findAirportByPartialName(origin);

        if (matchingAirports.isEmpty()) {
            System.out.println("✗ Service Error: No airports found matching: " + origin);
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> {
                    Airport flightOrigin = flight.getFlightOrigin();
                    if (flightOrigin == null) {
                        return false;
                    }
                    // Check if the flight's origin airport is in our matching airports list
                    return matchingAirports.stream()
                            .anyMatch(airport -> airport.getId().equals(flightOrigin.getId()));
                })
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

        // Get matching airports from the search
        List<Airport> matchingAirports = airportService.findAirportByPartialName(destination);

        if (matchingAirports.isEmpty()) {
            System.out.println("✗ Service Error: No airports found matching: " + destination);
            return new ArrayList<>();
        }

        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> {
                    Airport flightOrigin = flight.getFlightOrigin();
                    if (flightOrigin == null) {
                        return false;
                    }
                    // Check if the flight's origin airport is in our matching airports list
                    return matchingAirports.stream()
                            .anyMatch(airport -> airport.getId().equals(flightOrigin.getId()));
                })
                .collect(Collectors.toList());
    }

    /**
     * Find flight details by departure and arrival airports
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

        // Find matching airports for origin
        List<Airport> originAirports = airportService.findAirportByPartialName(origin);
        if (originAirports.isEmpty()) {
            System.out.println("✗ Service Error: No airports found matching origin: " + origin);
            return new ArrayList<>();
        }

        // Find matching airports for destination
        List<Airport> destinationAirports = airportService.findAirportByPartialName(destination);
        if (destinationAirports.isEmpty()) {
            System.out.println("✗ Service Error: No airports found matching destination: " + destination);
            return new ArrayList<>();
        }

        // Filter flights that match both origin and destination
        List<FlightDetails> allFlights = getAllFlightDetails();
        return allFlights.stream()
                .filter(flight -> {
                    Airport flightOrigin = flight.getFlightOrigin();
                    Airport flightDestination = flight.getFlightDestination();

                    // Check for null values
                    if (flightOrigin == null || flightDestination == null) {
                        return false;
                    }

                    // Check if flight's origin matches any of the searched origins
                    boolean originMatches = originAirports.stream()
                            .anyMatch(airport -> airport.getId().equals(flightOrigin.getId()));

                    // Check if flight's destination matches any of the searched destinations
                    boolean destinationMatches = destinationAirports.stream()
                            .anyMatch(airport -> airport.getId().equals(flightDestination.getId()));

                    // Both origin and destination must match
                    return originMatches && destinationMatches;
                })
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
                .filter(flight -> {
                    // Search in flight number and airline
                    boolean basicFieldsMatch = flight.getFlightNumber().toLowerCase().contains(lowerSearchTerm) ||
                            flight.getFlightAirline().toLowerCase().contains(lowerSearchTerm) ||
                            flight.getFlightDepartureDate().toLowerCase().contains(lowerSearchTerm) ||
                            flight.getFlightArrivalDate().toLowerCase().contains(lowerSearchTerm) ||
                            flight.getFlightDepartureTime().toLowerCase().contains(lowerSearchTerm) ||
                            flight.getFlightArrivalTime().toLowerCase().contains(lowerSearchTerm) ||
                            flight.getFlightPrice().toLowerCase().contains(lowerSearchTerm);

                    // Search in origin airport details
                    boolean originMatches = false;
                    Airport origin = flight.getFlightOrigin();
                    if (origin != null) {
                        originMatches = origin.getAirportFullName().toLowerCase().contains(lowerSearchTerm) ||
                                origin.getAirportCode().toLowerCase().contains(lowerSearchTerm) ||
                                origin.getAirportCityLocation().toLowerCase().contains(lowerSearchTerm) ||
                                origin.getAirportCountryLocation().toLowerCase().contains(lowerSearchTerm);
                    }

                    // Search in destination airport details
                    boolean destinationMatches = false;
                    Airport destination = flight.getFlightDestination();
                    if (destination != null) {
                        destinationMatches = destination.getAirportFullName().toLowerCase().contains(lowerSearchTerm) ||
                                destination.getAirportCode().toLowerCase().contains(lowerSearchTerm) ||
                                destination.getAirportCityLocation().toLowerCase().contains(lowerSearchTerm) ||
                                destination.getAirportCountryLocation().toLowerCase().contains(lowerSearchTerm);
                    }

                    // Return true if any field matches
                    return basicFieldsMatch || originMatches || destinationMatches;
                })
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
}