package com.example.travelbuddybackend.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class BusDetails {

    private Integer id;

    @NotNull(message = "Bus number is required")
    @NotBlank(message = "Bus number cannot be blank")
    @Size(min = 2, max = 20, message = "Bus number must be between 2 and 20 characters")
    private String busNumber;

    @NotNull(message = "Bus line is required")
    @NotBlank(message = "Bus line cannot be blank")
    @Size(min = 2, max = 50, message = "Bus line must be between 2 and 50 characters")
    private String busLine;

    @NotNull(message = "Departure station is required")
    @Valid
    private BusStation busDepartureStation;

    @NotNull(message = "Arrival station is required")
    @Valid
    private BusStation busArrivalStation;

    @NotNull(message = "Departure date is required")
    @NotBlank(message = "Departure date cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Departure date must be in format YYYY-MM-DD")
    private String busDepartureDate;

    @NotNull(message = "Departure time is required")
    @NotBlank(message = "Departure time cannot be blank")
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", message = "Departure time must be in format HH:MM (24-hour)")
    private String busDepartureTime;

    @NotNull(message = "Arrival date is required")
    @NotBlank(message = "Arrival date cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Arrival date must be in format YYYY-MM-DD")
    private String busArrivalDate;

    @NotNull(message = "Arrival time is required")
    @NotBlank(message = "Arrival time cannot be blank")
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", message = "Arrival time must be in format HH:MM (24-hour)")
    private String busArrivalTime;

    @NotNull(message = "Ride duration is required")
    @NotBlank(message = "Ride duration cannot be blank")
    @Pattern(regexp = "\\d{1,2}h\\s?\\d{0,2}m?|\\d{1,2}h", message = "Duration must be in format like '2h 30m' or '2h'")
    private String busRideDuration;

    @NotNull(message = "Ride price is required")
    @NotBlank(message = "Ride price cannot be blank")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Pattern(regexp = "\\d+(\\.\\d{1,2})?", message = "Price must be a valid decimal number with max 2 decimal places")
    private String busRidePrice;

    public BusDetails() {}

    public BusDetails(Integer id, String busNumber, String busLine, BusStation busDepartureStation, BusStation busArrivalStation,
                           String busDepartureDate, String busDepartureTime, String busArrivalDate, String busArrivalTime,
                           String busRideDuration, String busRidePrice) {
        this.id = id;
        this.busNumber = busNumber;
        this.busLine = busLine;
        this.busDepartureStation = busDepartureStation;
        this.busArrivalStation = busArrivalStation;
        this.busDepartureDate = busDepartureDate;
        this.busDepartureTime = busDepartureTime;
        this.busArrivalDate = busArrivalDate;
        this.busArrivalTime = busArrivalTime;
        this.busRideDuration = busRideDuration;
        this.busRidePrice = busRidePrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusLine() {
        return busLine;
    }

    public void setBusLine(String busLine) {
        this.busLine = busLine;
    }

    public BusStation getBusDepartureStation() {
        return busDepartureStation;
    }

    public void setBusDepartureStation(BusStation busDepartureStation) {
        this.busDepartureStation = busDepartureStation;
    }

    public BusStation getBusArrivalStation() {
        return busArrivalStation;
    }

    public void setBusArrivalStation(BusStation busArrivalStation) {
        this.busArrivalStation = busArrivalStation;
    }

    public String getBusDepartureDate() {
        return busDepartureDate;
    }

    public void setBusDepartureDate(String busDepartureDate) {
        this.busDepartureDate = busDepartureDate;
    }

    public String getBusDepartureTime() {
        return busDepartureTime;
    }

    public void setBusDepartureTime(String busDepartureTime) {
        this.busDepartureTime = busDepartureTime;
    }

    public String getBusArrivalDate() {
        return busArrivalDate;
    }

    public void setBusArrivalDate(String busArrivalDate) {
        this.busArrivalDate = busArrivalDate;
    }

    public String getBusArrivalTime() {
        return busArrivalTime;
    }

    public void setBusArrivalTime(String busArrivalTime) {
        this.busArrivalTime = busArrivalTime;
    }

    public String getBusRideDuration() {
        return busRideDuration;
    }

    public void setBusRideDuration(String busRideDuration) {
        this.busRideDuration = busRideDuration;
    }

    public String getBusRidePrice() {
        return busRidePrice;
    }

    public void setBusRidePrice(String busRidePrice) {
        this.busRidePrice = busRidePrice;
    }

    @Override
    public String toString() {
        return "BusDetails{" +
                "id=" + id +
                ", busNumber='" + busNumber + '\'' +
                ", busLine='" + busLine + '\'' +
                ", busDepartureStation=" + busDepartureStation +
                ", busArrivalStation=" + busArrivalStation +
                ", busDepartureDate='" + busDepartureDate + '\'' +
                ", busDepartureTime='" + busDepartureTime + '\'' +
                ", busArrivalDate='" + busArrivalDate + '\'' +
                ", busArrivalTime='" + busArrivalTime + '\'' +
                ", busRideDuration='" + busRideDuration + '\'' +
                ", busRidePrice='" + busRidePrice + '\'' +
                '}';
    }
}
