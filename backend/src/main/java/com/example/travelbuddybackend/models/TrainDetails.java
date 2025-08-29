package com.example.travelbuddybackend.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class TrainDetails {

    private Integer id;

    @NotNull(message = "Train number is required")
    @NotBlank(message = "Train number cannot be blank")
    @Size(min = 2, max = 20, message = "Train number must be between 2 and 20 characters")
    private String trainNumber;

    @NotNull(message = "Train line is required")
    @NotBlank(message = "Train line cannot be blank")
    @Size(min = 2, max = 50, message = "Train line must be between 2 and 50 characters")
    private String trainLine;

    @NotNull(message = "Departure station is required")
    @Valid
    private TrainStation trainDepartureStation;

    @NotNull(message = "Arrival station is required")
    @Valid
    private TrainStation trainArrivalStation;

    @NotNull(message = "Departure date is required")
    @NotBlank(message = "Departure date cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Departure date must be in format YYYY-MM-DD")
    private String trainDepartureDate;

    @NotNull(message = "Departure time is required")
    @NotBlank(message = "Departure time cannot be blank")
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", message = "Departure time must be in format HH:MM (24-hour)")
    private String trainDepartureTime;

    @NotNull(message = "Arrival date is required")
    @NotBlank(message = "Arrival date cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Arrival date must be in format YYYY-MM-DD")
    private String trainArrivalDate;

    @NotNull(message = "Arrival time is required")
    @NotBlank(message = "Arrival time cannot be blank")
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", message = "Arrival time must be in format HH:MM (24-hour)")
    private String trainArrivalTime;

    @NotNull(message = "Ride duration is required")
    @NotBlank(message = "Ride duration cannot be blank")
    @Pattern(regexp = "\\d{1,2}h\\s?\\d{0,2}m?|\\d{1,2}h", message = "Duration must be in format like '2h 30m' or '2h'")
    private String trainRideDuration;

    @NotNull(message = "Ride price is required")
    @NotBlank(message = "Ride price cannot be blank")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Pattern(regexp = "\\d+(\\.\\d{1,2})?", message = "Price must be a valid decimal number with max 2 decimal places")
    private String trainRidePrice;

    // Default constructor
    public TrainDetails() {}

    // Constructor with all fields
    public TrainDetails(Integer id, String trainNumber, String trainLine,
                        TrainStation trainDepartureStation, TrainStation trainArrivalStation,
                        String trainDepartureDate, String trainDepartureTime,
                        String trainArrivalDate, String trainArrivalTime,
                        String trainRideDuration, String trainRidePrice) {
        this.id = id;
        this.trainNumber = trainNumber;
        this.trainLine = trainLine;
        this.trainDepartureStation = trainDepartureStation;
        this.trainArrivalStation = trainArrivalStation;
        this.trainDepartureDate = trainDepartureDate;
        this.trainDepartureTime = trainDepartureTime;
        this.trainArrivalDate = trainArrivalDate;
        this.trainArrivalTime = trainArrivalTime;
        this.trainRideDuration = trainRideDuration;
        this.trainRidePrice = trainRidePrice;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber != null ? trainNumber.toUpperCase() : null;
    }

    public String getTrainLine() {
        return trainLine;
    }

    public void setTrainLine(String trainLine) {
        this.trainLine = trainLine;
    }

    public TrainStation getTrainDepartureStation() {
        return trainDepartureStation;
    }

    public void setTrainDepartureStation(TrainStation trainDepartureStation) {
        this.trainDepartureStation = trainDepartureStation;
    }

    public TrainStation getTrainArrivalStation() {
        return trainArrivalStation;
    }

    public void setTrainArrivalStation(TrainStation trainArrivalStation) {
        this.trainArrivalStation = trainArrivalStation;
    }

    public String getTrainDepartureDate() {
        return trainDepartureDate;
    }

    public void setTrainDepartureDate(String trainDepartureDate) {
        this.trainDepartureDate = trainDepartureDate;
    }

    public String getTrainDepartureTime() {
        return trainDepartureTime;
    }

    public void setTrainDepartureTime(String trainDepartureTime) {
        this.trainDepartureTime = trainDepartureTime;
    }

    public String getTrainArrivalDate() {
        return trainArrivalDate;
    }

    public void setTrainArrivalDate(String trainArrivalDate) {
        this.trainArrivalDate = trainArrivalDate;
    }

    public String getTrainArrivalTime() {
        return trainArrivalTime;
    }

    public void setTrainArrivalTime(String trainArrivalTime) {
        this.trainArrivalTime = trainArrivalTime;
    }

    public String getTrainRideDuration() {
        return trainRideDuration;
    }

    public void setTrainRideDuration(String trainRideDuration) {
        this.trainRideDuration = trainRideDuration;
    }

    public String getTrainRidePrice() {
        return trainRidePrice;
    }

    public void setTrainRidePrice(String trainRidePrice) {
        this.trainRidePrice = trainRidePrice;
    }

    @Override
    public String toString() {
        return "TrainDetails{" +
                "id=" + id +
                ", trainNumber='" + trainNumber + '\'' +
                ", trainLine='" + trainLine + '\'' +
                ", trainDepartureStation=" + trainDepartureStation +
                ", trainArrivalStation=" + trainArrivalStation +
                ", trainDepartureDate='" + trainDepartureDate + '\'' +
                ", trainDepartureTime='" + trainDepartureTime + '\'' +
                ", trainArrivalDate='" + trainArrivalDate + '\'' +
                ", trainArrivalTime='" + trainArrivalTime + '\'' +
                ", trainRideDuration='" + trainRideDuration + '\'' +
                ", trainRidePrice='" + trainRidePrice + '\'' +
                '}';
    }
}