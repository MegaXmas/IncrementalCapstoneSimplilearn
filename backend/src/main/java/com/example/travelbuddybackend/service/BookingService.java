package com.example.travelbuddybackend.service;

import com.example.travelbuddybackend.models.Client;
import com.example.travelbuddybackend.models.TrainDetails;
import com.example.travelbuddybackend.models.FlightDetails;
import com.example.travelbuddybackend.models.BusDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final TrainDetailsService trainDetailsService;
    private final FlightDetailsService flightDetailsService;
    private final BusDetailsService busDetailsService;

    // ============Custom exceptions for booking-related problems=============
    public static class InvalidBookingException extends RuntimeException {
        public InvalidBookingException(String message) {
            super(message);
        }
    }

    public static class BookingProcessException extends RuntimeException {
        public BookingProcessException(String message) {
            super(message);
        }
    }

    @Autowired
    public BookingService(TrainDetailsService trainDetailsService, FlightDetailsService flightDetailsService, BusDetailsService busDetailsService) {
        this.trainDetailsService = trainDetailsService;
        this.flightDetailsService = flightDetailsService;
        this.busDetailsService = busDetailsService;
    }

    // Method for booking flights
    public String bookTicket(Client client, FlightDetails flightDetails) {
        // Flight booking logic
        System.out.println("Booking flight for: " + client.getName());
        System.out.println("Flight: " + flightDetails.getFlightNumber());

        // Generate booking ID
        String bookingId = "FLIGHT_" + System.currentTimeMillis();

        // Save booking to database (you'll add this later)
        // bookingRepository.save(new Booking(client, flightDetails, bookingId));

        return bookingId;
    }

    // Method for booking buses
    public String bookTicket(Client client, BusDetails busDetails) {
        // Bus booking logic
        System.out.println("Booking bus for: " + client.getName());
        System.out.println("Bus: " + busDetails.getBusNumber());

        String bookingId = "BUS_" + System.currentTimeMillis();

        // Save booking to database
        // bookingRepository.save(new Booking(client, busDetails, bookingId));

        return bookingId;
    }

    // Method for booking trains
    public String bookTicket(Client client, TrainDetails trainDetails) {
        // Train booking logic
        System.out.println("Booking train for: " + client.getName());
        System.out.println("Train: " + trainDetails.getTrainNumber());

        String bookingId = "TRAIN_" + System.currentTimeMillis();

        // Save booking to database
        // bookingRepository.save(new Booking(client, trainDetails, bookingId));

        return bookingId;
    }

    private void validateBookingInputs(Client client) {
        if (client == null) {
            throw new InvalidBookingException("Client cannot be null");
        }

        validateClientData(client);
    }


    /**
     * Helper method to validate client data
     * @param client The Client who is booking the Route and needs to be validated
     * */
    private void validateClientData(Client client) {
        if (client.getId() == null || client.getId() <= 0) {
            throw new InvalidBookingException("Client must have a valid ID");
        }

        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new InvalidBookingException("Client must have a valid name");
        }

        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            throw new InvalidBookingException("Client must have a valid email");
        }

        // Basic email validation
        if (!client.getEmail().contains("@")) {
            throw new InvalidBookingException("Client email must be valid: " + client.getEmail());
        }
    }
}
