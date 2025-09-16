package com.example.travelbuddybackend.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TrainStation {

    private Integer id;

    @NotNull(message = "Train station name is required")
    @NotBlank(message = "Train station name cannot be blank")
    @Size(min = 2, max = 100, message = "Train station name must be between 2 and 100 characters")
    private String trainStationFullName;

    @NotNull(message = "Train station code is required")
    @NotBlank(message = "Train station code cannot be blank")
    @Size(min = 2, max = 10, message = "Train station code must be between 2 and 10 characters")
    @Pattern(regexp = "[A-Z0-9]{2,10}", message = "Train station code must be uppercase letters and/or numbers only")
    private String trainStationCode;

    @NotNull(message = "City location is required")
    @NotBlank(message = "City location cannot be blank")
    @Size(min = 2, max = 50, message = "City location must be between 2 and 50 characters")
    private String trainStationCityLocation;

    // Default constructor
    public TrainStation() {}

    // Constructor with all fields
    public TrainStation(Integer id, String trainStationFullName,
                        String trainStationCode, String trainStationCityLocation) {
        this.id = id;
        this.trainStationFullName = trainStationFullName;
        this.trainStationCode = trainStationCode;
        this.trainStationCityLocation = trainStationCityLocation;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTrainStationFullName() {
        return trainStationFullName;
    }

    public void setTrainStationFullName(String trainStationFullName) {
        this.trainStationFullName = trainStationFullName;
    }

    public String getTrainStationCode() {
        return trainStationCode;
    }

    public void setTrainStationCode(String trainStationCode) {
        this.trainStationCode = trainStationCode;
    }

    public String getTrainStationCityLocation() {
        return trainStationCityLocation;
    }

    public void setTrainStationCityLocation(String trainStationCityLocation) {
        this.trainStationCityLocation = trainStationCityLocation;
    }

    @Override
    public String toString() {
        return "TrainStation{" +
                "id=" + id +
                ", trainStationFullName='" + trainStationFullName + '\'' +
                ", trainStationCode='" + trainStationCode + '\'' +
                ", trainStationCityLocation='" + trainStationCityLocation + '\'' +
                '}';
    }
}