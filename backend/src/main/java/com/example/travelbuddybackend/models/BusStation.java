package com.example.travelbuddybackend.models;

import jakarta.validation.constraints.*;

public class BusStation {

    private Integer id;

    @NotNull(message = "Bus station name is required")
    @NotBlank(message = "Bus station name cannot be blank")
    @Size(min = 2, max = 100, message = "Bus station name must be between 2 and 100 characters")
    private String busStationFullName;

    @NotNull(message = "Bus station code is required")
    @NotBlank(message = "Bus station code cannot be blank")
    @Size(min = 2, max = 10, message = "Bus station code must be between 2 and 10 characters")
    @Pattern(regexp = "[A-Z0-9]{2,10}", message = "Bus station code must be uppercase letters and/or numbers only")
    private String busStationCode;

    @NotNull(message = "City location is required")
    @NotBlank(message = "City location cannot be blank")
    @Size(min = 2, max = 50, message = "City location must be between 2 and 50 characters")
    private String busStationCityLocation;

    // Default constructor
    public BusStation() {}

    // Constructor with all fields
    public BusStation(Integer id, String busStationFullName,
                      String busStationCode, String busStationCityLocation) {
        this.id = id;
        this.busStationFullName = busStationFullName;
        this.busStationCode = busStationCode;
        this.busStationCityLocation = busStationCityLocation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBusStationFullName() {
        return busStationFullName;
    }

    public void setBusStationFullName(String busStationFullName) {
        this.busStationFullName = busStationFullName;
    }

    public String getBusStationCode() {
        return busStationCode;
    }

    public void setBusStationCode(String busStationCode) {
        this.busStationCode = busStationCode;
    }

    public String getBusStationCityLocation() {
        return busStationCityLocation;
    }

    public void setBusStationCityLocation(String busStationCityLocation) {
        this.busStationCityLocation = busStationCityLocation;
    }

    @Override
    public String toString() {
        return "busStationModel{" +
                "id=" + id +
                ", busStationFullName='" + busStationFullName + '\'' +
                ", busStationCode='" + busStationCode + '\'' +
                ", busStationCityLocation='" + busStationCityLocation + '\'' +
                '}';
    }
}
