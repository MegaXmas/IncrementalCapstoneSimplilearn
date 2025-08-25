package com.example.travelbuddybackend.models;

public class Airport {

    private Integer id;
    private String airportFullName;
    private String airportCode;
    private String airportCityLocation;
    private String airportCountryLocation;
    private String airportTimezone;

    public Airport() {}

    public Airport(Integer id, String airportFullName, String airportCode, String airportCityLocation,
                   String airportCountryLocation, String airportTimezone) {
        this.id = id;
        this.airportFullName = airportFullName;
        this.airportCode = airportCode;
        this.airportCityLocation = airportCityLocation;
        this.airportCountryLocation = airportCountryLocation;
        this.airportTimezone = airportTimezone;
    }

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
