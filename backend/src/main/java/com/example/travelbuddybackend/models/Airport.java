package com.example.travelbuddybackend.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Airport {

    private Integer id;

    @NotNull(message = "Airport name is required")
    @NotBlank(message = "Airport name cannot be blank")
    @Size(min = 3, max = 100, message = "Airport name must be between 3 and 100 characters")
    private String airportFullName;

    @NotNull(message = "Airport code is required")
    @NotBlank(message = "Airport code cannot be blank")
    @Size(min = 3, max = 4, message = "Airport code must be 3 or 4 characters")
    @Pattern(regexp = "[A-Z]{3,4}", message = "Airport code must be uppercase letters only (e.g., LAX, KJFK)")
    private String airportCode;

    @NotNull(message = "City location is required")
    @NotBlank(message = "City location cannot be blank")
    @Size(min = 2, max = 50, message = "City location must be between 2 and 50 characters")
    private String airportCityLocation;

    @NotNull(message = "Country location is required")
    @NotBlank(message = "Country location cannot be blank")
    @Size(min = 2, max = 50, message = "Country location must be between 2 and 50 characters")
    private String airportCountryLocation;

    @NotNull(message = "Timezone is required")
    @NotBlank(message = "Timezone cannot be blank")
    @Pattern(regexp = "[A-Z]{3,4}|UTC[+-]\\d{1,2}|[A-Za-z_/]+", message = "Timezone must be in valid format (e.g., EST, UTC-5, America/New_York)")
    private String airportTimezone;

    // Default constructor
    public Airport() {}

    // Constructor with all fields
    public Airport(Integer id, String airportFullName, String airportCode,
                   String airportCityLocation, String airportCountryLocation,
                   String airportTimezone) {
        this.id = id;
        this.airportFullName = airportFullName;
        this.airportCode = airportCode;
        this.airportCityLocation = airportCityLocation;
        this.airportCountryLocation = airportCountryLocation;
        this.airportTimezone = airportTimezone;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAirportFullName() {
        return airportFullName;
    }

    public void setAirportFullName(String airportFullName) {
        this.airportFullName = airportFullName;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getAirportCityLocation() {
        return airportCityLocation;
    }

    public void setAirportCityLocation(String airportCityLocation) {
        this.airportCityLocation = airportCityLocation;
    }

    public String getAirportCountryLocation() {
        return airportCountryLocation;
    }

    public void setAirportCountryLocation(String airportCountryLocation) {
        this.airportCountryLocation = airportCountryLocation;
    }

    public String getAirportTimezone() {
        return airportTimezone;
    }

    public void setAirportTimezone(String airportTimezone) {
        this.airportTimezone = airportTimezone;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "id=" + id +
                ", airportFullName='" + airportFullName + '\'' +
                ", airportCode='" + airportCode + '\'' +
                ", airportCityLocation='" + airportCityLocation + '\'' +
                ", airportCountryLocation='" + airportCountryLocation + '\'' +
                ", airportTimezone='" + airportTimezone + '\'' +
                '}';
    }
}