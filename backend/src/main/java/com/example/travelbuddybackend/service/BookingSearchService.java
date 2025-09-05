// Enhanced BookingSearchService.java - Uses your existing detail services
package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingSearchService {

    @Autowired
    private BookingService bookingService;

    // Use your existing detail services
    @Autowired
    private FlightDetailsService flightDetailsService;

    @Autowired
    private TrainDetailsService trainDetailsService;

    @Autowired
    private BusDetailsService busDetailsService;

    // Simple search criteria class
    public static class BookingSearchCriteria {
        private String transportType;
        private String departureCity;
        private String arrivalCity;
        private String departureStation;
        private String arrivalStation;
        private LocalDateTime departureTime;
        private Double minPrice;
        private Double maxPrice;
        private String airline; // For flights
        private String line;    // For trains/buses

        // Constructors
        public BookingSearchCriteria() {}

        // Getters and setters
        public String getTransportType() { return transportType; }
        public void setTransportType(String transportType) { this.transportType = transportType; }

        public String getDepartureCity() { return departureCity; }
        public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }

        public String getArrivalCity() { return arrivalCity; }
        public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }

        public String getDepartureStation() { return departureStation; }
        public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }

        public String getArrivalStation() { return arrivalStation; }
        public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }

        public LocalDateTime getDepartureTime() { return departureTime; }
        public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

        public Double getMinPrice() { return minPrice; }
        public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

        public Double getMaxPrice() { return maxPrice; }
        public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

        public String getAirline() { return airline; }
        public void setAirline(String airline) { this.airline = airline; }

        public String getLine() { return line; }
        public void setLine(String line) { this.line = line; }
    }

    // Simple result class that combines available tickets from all services
    public static class AvailableTicket {
        private Long id;
        private String transportType;
        private String number; // flight/train/bus number
        private String departureLocation;
        private String arrivalLocation;
        private String departureTime;
        private String arrivalTime;
        private Double price;
        private String additionalInfo; // airline, line, etc.

        public AvailableTicket(Long id, String transportType, String number,
                               String departureLocation, String arrivalLocation,
                               String departureTime, String arrivalTime,
                               Double price, String additionalInfo) {
            this.id = id;
            this.transportType = transportType;
            this.number = number;
            this.departureLocation = departureLocation;
            this.arrivalLocation = arrivalLocation;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.price = price;
            this.additionalInfo = additionalInfo;
        }

        // Getters
        public Long getId() { return id; }
        public String getTransportType() { return transportType; }
        public String getNumber() { return number; }
        public String getDepartureLocation() { return departureLocation; }
        public String getArrivalLocation() { return arrivalLocation; }
        public String getDepartureTime() { return departureTime; }
        public String getArrivalTime() { return arrivalTime; }
        public Double getPrice() { return price; }
        public String getAdditionalInfo() { return additionalInfo; }
    }

    /**
     * Search available tickets using your existing detail services
     * This finds available flights, trains, and buses to book
     */
    public List<AvailableTicket> searchAvailableTickets(BookingSearchCriteria criteria) {
        System.out.println("🔍 Searching available tickets with criteria");

        List<AvailableTicket> allTickets = new ArrayList<>();

        // Search flights using your FlightDetailsService
        if (criteria.getTransportType() == null || criteria.getTransportType().equals("flight")) {
            allTickets.addAll(searchFlights(criteria));
        }

        // Search trains using your TrainDetailsService
        if (criteria.getTransportType() == null || criteria.getTransportType().equals("train")) {
            allTickets.addAll(searchTrains(criteria));
        }

        // Search buses using your BusDetailsService
        if (criteria.getTransportType() == null || criteria.getTransportType().equals("bus")) {
            allTickets.addAll(searchBuses(criteria));
        }

        // Apply additional filters
        return allTickets.stream()
                .filter(ticket -> matchesPriceRange(ticket.getPrice(), criteria.getMinPrice(), criteria.getMaxPrice()))
                .collect(Collectors.toList());
    }

    /**
     * Search existing bookings using your BookingService methods
     */
    public List<Booking> searchExistingBookings(BookingSearchCriteria criteria) {
        if (criteria.getTransportType() != null) {
            String prefix = getTransportPrefix(criteria.getTransportType());
            return bookingService.findBookingsByTransportType(prefix);
        }
        return bookingService.getAllBookings();
    }

    // Search flights using your existing FlightDetailsService methods
    private List<AvailableTicket> searchFlights(BookingSearchCriteria criteria) {
        List<FlightDetails> flights = flightDetailsService.getAllFlightDetails();

        System.out.println("🛩️ Initial flight count: " + flights.size());

        // Apply airline filter if specified
        if (criteria.getAirline() != null && !criteria.getAirline().trim().isEmpty()) {
            flights = flightDetailsService.findFlightsByAirline(criteria.getAirline());
            System.out.println("🛩️ After airline filter (" + criteria.getAirline() + "): " + flights.size());
        }

        // Apply price range filter using your existing method
        if (criteria.getMinPrice() != null && criteria.getMaxPrice() != null) {
            flights = flightDetailsService.findFlightsByPriceRange(
                    criteria.getMinPrice().toString(),
                    criteria.getMaxPrice().toString()
            );
            System.out.println("🛩️ After price filter (" + criteria.getMinPrice() + "-" + criteria.getMaxPrice() + "): " + flights.size());
        }

        // Convert to AvailableTicket objects and apply location filters
        List<AvailableTicket> results = flights.stream()
                .filter(flight -> {
                    boolean matches = matchesAirportLocation(flight.getFlightOrigin(), criteria.getDepartureCity());
                    if (!matches) {
                        System.out.println("🛩️ Flight " + flight.getFlightNumber() + " filtered out by departure city: " +
                                getAirportDisplayName(flight.getFlightOrigin()) + " vs " + criteria.getDepartureCity());
                    }
                    return matches;
                })
                .filter(flight -> {
                    boolean matches = matchesAirportLocation(flight.getFlightDestination(), criteria.getArrivalCity());
                    if (!matches) {
                        System.out.println("🛩️ Flight " + flight.getFlightNumber() + " filtered out by arrival city: " +
                                getAirportDisplayName(flight.getFlightDestination()) + " vs " + criteria.getArrivalCity());
                    }
                    return matches;
                })
                .filter(flight -> {
                    boolean matches = matchesAirportLocation(flight.getFlightOrigin(), criteria.getDepartureStation());
                    if (!matches && criteria.getDepartureStation() != null && !criteria.getDepartureStation().trim().isEmpty()) {
                        System.out.println("🛩️ Flight " + flight.getFlightNumber() + " filtered out by departure station: " +
                                getAirportDisplayName(flight.getFlightOrigin()) + " vs " + criteria.getDepartureStation());
                    }
                    return matches;
                })
                .filter(flight -> {
                    boolean matches = matchesAirportLocation(flight.getFlightDestination(), criteria.getArrivalStation());
                    if (!matches && criteria.getArrivalStation() != null && !criteria.getArrivalStation().trim().isEmpty()) {
                        System.out.println("🛩️ Flight " + flight.getFlightNumber() + " filtered out by arrival station: " +
                                getAirportDisplayName(flight.getFlightDestination()) + " vs " + criteria.getArrivalStation());
                    }
                    return matches;
                })
                .map(flight -> new AvailableTicket(
                        flight.getId().longValue(),
                        "flight",
                        flight.getFlightNumber(),
                        getAirportDisplayName(flight.getFlightOrigin()),
                        getAirportDisplayName(flight.getFlightDestination()),
                        flight.getFlightDepartureDate() + " " + flight.getFlightDepartureTime(),
                        flight.getFlightArrivalDate() + " " + flight.getFlightArrivalTime(),
                        Double.parseDouble(flight.getFlightPrice()),
                        flight.getFlightAirline()
                ))
                .collect(Collectors.toList());

        System.out.println("🛩️ Final flight results: " + results.size());
        return results;
    }

    private List<AvailableTicket> searchTrains(BookingSearchCriteria criteria) {
        List<TrainDetails> trains = trainDetailsService.getAllTrainDetails();

        System.out.println("🚂 Starting train search with " + trains.size() + " trains");
        System.out.println("🚂 Looking for: " + criteria.getDepartureStation() + " → " + criteria.getArrivalStation());

        // Apply line filter if specified
        if (criteria.getLine() != null && !criteria.getLine().trim().isEmpty()) {
            trains = trainDetailsService.findTrainsByLine(criteria.getLine());
            System.out.println("🚂 After line filter: " + trains.size() + " trains");
        }

        // Apply price range filter using your existing method
        if (criteria.getMinPrice() != null && criteria.getMaxPrice() != null) {
            trains = trainDetailsService.findTrainsByPriceRange(
                    criteria.getMinPrice().toString(),
                    criteria.getMaxPrice().toString()
            );
            System.out.println("🚂 After price filter: " + trains.size() + " trains");
        }

        // Enhanced filtering with detailed logging
        List<AvailableTicket> results = trains.stream()
                .filter(train -> {
                    String depCode = train.getTrainDepartureStation() != null ?
                            train.getTrainDepartureStation().getTrainStationCode() : "null";
                    String arrCode = train.getTrainArrivalStation() != null ?
                            train.getTrainArrivalStation().getTrainStationCode() : "null";

                    System.out.println("🚂 Checking train " + train.getTrainNumber() +
                            ": " + depCode + " → " + arrCode);

                    boolean depMatch = matchesStationLocation(train.getTrainDepartureStation(), criteria.getDepartureStation());
                    System.out.println("   🔍 Departure match (" + depCode + " vs " + criteria.getDepartureStation() + "): " + depMatch);

                    return depMatch;
                })
                .filter(train -> {
                    String arrCode = train.getTrainArrivalStation() != null ?
                            train.getTrainArrivalStation().getTrainStationCode() : "null";

                    boolean arrMatch = matchesStationLocation(train.getTrainArrivalStation(), criteria.getArrivalStation());
                    System.out.println("   🔍 Arrival match (" + arrCode + " vs " + criteria.getArrivalStation() + "): " + arrMatch);

                    return arrMatch;
                })
                .map(train -> {
                    System.out.println("✅ Train " + train.getTrainNumber() + " passed all filters!");
                    return new AvailableTicket(
                            train.getId().longValue(),
                            "train",
                            train.getTrainNumber(),
                            getTrainStationDisplayName(train.getTrainDepartureStation()),
                            getTrainStationDisplayName(train.getTrainArrivalStation()),
                            train.getTrainDepartureDate() + " " + train.getTrainDepartureTime(),
                            train.getTrainArrivalDate() + " " + train.getTrainArrivalTime(),
                            Double.parseDouble(train.getTrainRidePrice()),
                            train.getTrainLine()
                    );
                })
                .collect(Collectors.toList());

        System.out.println("🚂 Final results: " + results.size() + " trains");
        return results;
    }

    // Search buses using your existing BusDetailsService methods
    private List<AvailableTicket> searchBuses(BookingSearchCriteria criteria) {
        List<BusDetails> buses = busDetailsService.getAllBusDetails();

        // Apply line filter if specified
        if (criteria.getLine() != null && !criteria.getLine().trim().isEmpty()) {
            buses = busDetailsService.findBusesByLine(criteria.getLine());
        }

        // Apply price range filter using your existing method
        if (criteria.getMinPrice() != null && criteria.getMaxPrice() != null) {
            buses = busDetailsService.findBusesByPriceRange(
                    criteria.getMinPrice().toString(),
                    criteria.getMaxPrice().toString()
            );
        }

        // Convert to AvailableTicket objects and apply location filters
        return buses.stream()
                .filter(bus -> matchesBusStationLocation(bus.getBusDepartureStation(), criteria.getDepartureStation()))
                .filter(bus -> matchesBusStationLocation(bus.getBusArrivalStation(), criteria.getArrivalStation()))
                .map(bus -> new AvailableTicket(
                        bus.getId().longValue(),
                        "bus",
                        bus.getBusNumber(),
                        getBusStationDisplayName(bus.getBusDepartureStation()),
                        getBusStationDisplayName(bus.getBusArrivalStation()),
                        bus.getBusDepartureDate() + " " + bus.getBusDepartureTime(),
                        bus.getBusArrivalDate() + " " + bus.getBusArrivalTime(),
                        Double.parseDouble(bus.getBusRidePrice()),
                        bus.getBusLine()
                ))
                .collect(Collectors.toList());
    }

    // Helper methods for handling complex objects
    private boolean matchesAirportLocation(Airport airport, String searchLocation) {
        if (searchLocation == null || searchLocation.trim().isEmpty()) {
            return true; // No filter applied
        }
        if (airport == null) return false;

        String search = searchLocation.toLowerCase().trim();

        // Check if search is a numeric ID (from station search component)
        try {
            Long searchId = Long.parseLong(search);
            if (airport.getId() != null && airport.getId().equals(searchId.intValue())) {
                System.out.println("🎯 Airport ID match: " + airport.getId() + " = " + searchId);
                return true;
            }
        } catch (NumberFormatException e) {
            // Not a number, continue with text matching
        }

        // Text-based matching for manual input
        boolean nameMatch = airport.getAirportFullName() != null && airport.getAirportFullName().toLowerCase().contains(search);
        boolean codeMatch = airport.getAirportCode() != null && airport.getAirportCode().toUpperCase().contains(search);
        boolean cityMatch = airport.getAirportCityLocation() != null && airport.getAirportCityLocation().toLowerCase().contains(search);

        if (nameMatch || codeMatch || cityMatch) {
            System.out.println("🎯 Airport text match: " + getAirportDisplayName(airport) + " matches " + search);
            return true;
        }

        System.out.println("❌ No airport match: " + getAirportDisplayName(airport) + " vs " + search);
        return false;
    }

    private boolean matchesStationLocation(TrainStation station, String searchLocation) {
        System.out.println("🔍 matchesStationLocation called:");
        System.out.println("   Station: " + (station != null ? station.getTrainStationCode() : "null"));
        System.out.println("   Search: " + searchLocation);

        if (searchLocation == null || searchLocation.trim().isEmpty()) {
            System.out.println("   ✅ No filter applied - returning true");
            return true; // No filter applied
        }

        if (station == null) {
            System.out.println("   ❌ Station is null - returning false");
            return false;
        }

        String search = searchLocation.toLowerCase().trim();
        System.out.println("   Search lowercased: '" + search + "'");

        // Check if search is a numeric ID (from station search component)
        try {
            Long searchId = Long.parseLong(search);
            if (station.getId() != null && station.getId().equals(searchId.intValue())) {
                System.out.println("   ✅ Train Station ID match: " + station.getId() + " = " + searchId);
                return true;
            }
            System.out.println("   ❌ ID mismatch: " + station.getId() + " != " + searchId);
        } catch (NumberFormatException e) {
            System.out.println("   📝 Not a number, checking text match...");
        }

        // Text-based matching for manual input
        String stationName = station.getTrainStationFullName();
        String stationCode = station.getTrainStationCode();
        String stationCity = station.getTrainStationCityLocation();

        System.out.println("   Station details:");
        System.out.println("     Name: '" + stationName + "'");
        System.out.println("     Code: '" + stationCode + "'");
        System.out.println("     City: '" + stationCity + "'");

        boolean nameMatch = stationName != null && stationName.toLowerCase().contains(search);
        boolean codeMatch = stationCode != null && stationCode.toLowerCase().contains(search);
        boolean cityMatch = stationCity != null && stationCity.toLowerCase().contains(search);

        System.out.println("   Match results:");
        System.out.println("     Name match: " + nameMatch);
        System.out.println("     Code match: " + codeMatch +
                (stationCode != null ? " ('" + stationCode.toLowerCase() + "' contains '" + search + "')" : ""));
        System.out.println("     City match: " + cityMatch);

        boolean result = nameMatch || codeMatch || cityMatch;
        System.out.println("   🎯 Final result: " + result);

        return result;
    }

    private boolean matchesBusStationLocation(BusStation station, String searchLocation) {
        if (searchLocation == null || searchLocation.trim().isEmpty()) {
            return true; // No filter applied
        }
        if (station == null) return false;

        String search = searchLocation.toLowerCase().trim();

        // Check if search is a numeric ID (from station search component)
        try {
            Long searchId = Long.parseLong(search);
            if (station.getId() != null && station.getId().equals(searchId.intValue())) {
                System.out.println("🎯 Bus Station ID match: " + station.getId() + " = " + searchId);
                return true;
            }
        } catch (NumberFormatException e) {
            // Not a number, continue with text matching
        }

        // Text-based matching for manual input
        boolean nameMatch = station.getBusStationFullName() != null && station.getBusStationFullName().toLowerCase().contains(search);
        boolean codeMatch = station.getBusStationCode() != null && station.getBusStationCode().toUpperCase().contains(search);
        boolean cityMatch = station.getBusStationCityLocation() != null && station.getBusStationCityLocation().toLowerCase().contains(search);

        return nameMatch || codeMatch || cityMatch;
    }

    private String getAirportDisplayName(Airport airport) {
        if (airport == null) return "Unknown Airport";
        return airport.getAirportFullName() + " (" + airport.getAirportCode() + ")";
    }

    private String getTrainStationDisplayName(TrainStation station) {
        if (station == null) return "Unknown Station";
        return station.getTrainStationFullName() + " (" + station.getTrainStationCode() + ")";
    }

    private String getBusStationDisplayName(BusStation station) {
        if (station == null) return "Unknown Station";
        return station.getBusStationFullName() + " (" + station.getBusStationCode() + ")";
    }

    // Helper method for price range filtering
    private boolean matchesPriceRange(Double price, Double minPrice, Double maxPrice) {
        boolean minOk = minPrice == null || price >= minPrice;
        boolean maxOk = maxPrice == null || price <= maxPrice;
        return minOk && maxOk;
    }

    private String getTransportPrefix(String transportType) {
        if (transportType == null) return "";
        switch (transportType.toLowerCase()) {
            case "flight": return "FL";
            case "train": return "TR";
            case "bus": return "BS";
            default: return "";
        }
    }
}