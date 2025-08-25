package com.example.travelbuddybackend.models;

public class FlightDetails {

    private Integer id;
    private String flightNumber;
    private String flightAirline;
    private Airport flightOrigin;
    private Airport flightDestination;
    private String flightDepartureDate;
    private String flightArrivalDate;
    private String flightDepartureTime;
    private String flightArrivalTime;
    private String flightTravelTime;
    private String flightPrice;

    public FlightDetails() {}

    public FlightDetails(Integer id, String flightNumber, String flightAirline, Airport flightOrigin, Airport flightDestination,
                         String flightDepartureDate, String flightArrivalDate, String flightDepartureTime, String flightArrivalTime,
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
