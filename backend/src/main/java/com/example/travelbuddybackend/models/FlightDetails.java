package com.example.travelbuddybackend.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class FlightDetails {

    private Integer id;

    @NotNull(message = "Flight number is required")
    @NotBlank(message = "Flight number cannot be blank")
    @Size(min = 2, max = 15, message = "Flight number must be between 2 and 15 characters")
    @Pattern(regexp = "[A-Z]{2}\\d{3,4}", message = "Flight number must follow format like 'AA1234' (airline code + digits)")
    private String flightNumber;

    @NotNull(message = "Airline is required")
    @NotBlank(message = "Airline cannot be blank")
    @Size(min = 2, max = 50, message = "Airline name must be between 2 and 50 characters")
    private String flightAirline;

    @NotNull(message = "Origin airport is required")
    @Valid
    private Airport flightOrigin;

    @NotNull(message = "Destination airport is required")
    @Valid
    private Airport flightDestination;

    @NotNull(message = "Departure date is required")
    @NotBlank(message = "Departure date cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Departure date must be in format YYYY-MM-DD")
    private String flightDepartureDate;

    @NotNull(message = "Arrival date is required")
    @NotBlank(message = "Arrival date cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Arrival date must be in format YYYY-MM-DD")
    private String flightArrivalDate;

    @NotNull(message = "Departure time is required")
    @NotBlank(message = "Departure time cannot be blank")
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", message = "Departure time must be in format HH:MM (24-hour)")
    private String flightDepartureTime;

    @NotNull(message = "Arrival time is required")
    @NotBlank(message = "Arrival time cannot be blank")
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", message = "Arrival time must be in format HH:MM (24-hour)")
    private String flightArrivalTime;

    @NotNull(message = "Travel time is required")
    @NotBlank(message = "Travel time cannot be blank")
    @Pattern(regexp = "\\d{1,2}h\\s?\\d{0,2}m?|\\d{1,2}h", message = "Travel time must be in format like '2h 30m' or '2h'")
    private String flightTravelTime;

    @NotNull(message = "Flight price is required")
    @NotBlank(message = "Flight price cannot be blank")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Pattern(regexp = "\\d+(\\.\\d{1,2})?", message = "Price must be a valid decimal number with max 2 decimal places")
    private String flightPrice;

    // Default constructor
    public FlightDetails() {}

    // Constructor with all fields
    public FlightDetails(Integer id, String flightNumber, String flightAirline,
                         Airport flightOrigin, Airport flightDestination,
                         String flightDepartureDate, String flightArrivalDate,
                         String flightDepartureTime, String flightArrivalTime,
                         String flightTravelTime, String flightPrice) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.flightAirline = flightAirline;
        this.flightOrigin = flightOrigin;
        this.flightDestination = flightDestination;
        this.flightDepartureDate = flightDepartureDate;
        this.flightArrivalDate = flightArrivalDate;
        this.flightDepartureTime = flightDepartureTime;
        this.flightArrivalTime = flightArrivalTime;
        this.flightTravelTime = flightTravelTime;
        this.flightPrice = flightPrice;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getFlightAirline() {
        return flightAirline;
    }

    public void setFlightAirline(String flightAirline) {
        this.flightAirline = flightAirline;
    }

    public Airport getFlightOrigin() {
        return flightOrigin;
    }

    public void setFlightOrigin(Airport flightOrigin) {
        this.flightOrigin = flightOrigin;
    }

    public Airport getFlightDestination() {
        return flightDestination;
    }

    public void setFlightDestination(Airport flightDestination) {
        this.flightDestination = flightDestination;
    }

    public String getFlightDepartureDate() {
        return flightDepartureDate;
    }

    public void setFlightDepartureDate(String flightDepartureDate) {
        this.flightDepartureDate = flightDepartureDate;
    }

    public String getFlightArrivalDate() {
        return flightArrivalDate;
    }

    public void setFlightArrivalDate(String flightArrivalDate) {
        this.flightArrivalDate = flightArrivalDate;
    }

    public String getFlightDepartureTime() {
        return flightDepartureTime;
    }

    public void setFlightDepartureTime(String flightDepartureTime) {
        this.flightDepartureTime = flightDepartureTime;
    }

    public String getFlightArrivalTime() {
        return flightArrivalTime;
    }

    public void setFlightArrivalTime(String flightArrivalTime) {
        this.flightArrivalTime = flightArrivalTime;
    }

    public String getFlightTravelTime() {
        return flightTravelTime;
    }

    public void setFlightTravelTime(String flightTravelTime) {
        this.flightTravelTime = flightTravelTime;
    }

    public String getFlightPrice() {
        return flightPrice;
    }

    public void setFlightPrice(String flightPrice) {
        this.flightPrice = flightPrice;
    }

    @Override
    public String toString() {
        return "FlightDetails{" +
                "id=" + id +
                ", flightNumber='" + flightNumber + '\'' +
                ", flightAirline='" + flightAirline + '\'' +
                ", flightOrigin=" + flightOrigin +
                ", flightDestination=" + flightDestination +
                ", flightDepartureDate='" + flightDepartureDate + '\'' +
                ", flightArrivalDate='" + flightArrivalDate + '\'' +
                ", flightDepartureTime='" + flightDepartureTime + '\'' +
                ", flightArrivalTime='" + flightArrivalTime + '\'' +
                ", flightTravelTime='" + flightTravelTime + '\'' +
                ", flightPrice='" + flightPrice + '\'' +
                '}';
    }
}