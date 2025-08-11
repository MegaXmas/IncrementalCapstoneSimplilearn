package com.example.travelbuddybackend.models;

public class FlightDetails {

    private Integer id;
    private String flightNumber;
    private String flightAirline;
    private String flightOrigin;
    private String flightDestination;
    private String flightDepartureDate;
    private String flightArrivalDate;
    private String flightDepartureTime;
    private String flightArrivalTime;
    private String flightTravelTime;
    private String flightPrice;

    public FlightDetails() {}

    public FlightDetails(Integer id, String flightNumber, String flightAirline, String flightOrigin, String flightDestination,
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

    public String getFlightPrice() {
        return flightPrice;
    }

    public void setFlightPrice(String flightPrice) {
        this.flightPrice = flightPrice;
    }

    public String getFlightTravelTime() {
        return flightTravelTime;
    }

    public void setFlightTravelTime(String flightTravelTime) {
        this.flightTravelTime = flightTravelTime;
    }

    public String getFlightArrivalTime() {
        return flightArrivalTime;
    }

    public void setFlightArrivalTime(String flightArrivalTime) {
        this.flightArrivalTime = flightArrivalTime;
    }

    public String getFlightDepartureTime() {
        return flightDepartureTime;
    }

    public void setFlightDepartureTime(String flightDepartureTime) {
        this.flightDepartureTime = flightDepartureTime;
    }

    public String getFlightArrivalDate() {
        return flightArrivalDate;
    }

    public void setFlightArrivalDate(String flightArrivalDate) {
        this.flightArrivalDate = flightArrivalDate;
    }

    public String getFlightDepartureDate() {
        return flightDepartureDate;
    }

    public void setFlightDepartureDate(String flightDepartureDate) {
        this.flightDepartureDate = flightDepartureDate;
    }

    public String getFlightDestination() {
        return flightDestination;
    }

    public void setFlightDestination(String flightDestination) {
        this.flightDestination = flightDestination;
    }

    public String getFlightOrigin() {
        return flightOrigin;
    }

    public void setFlightOrigin(String flightOrigin) {
        this.flightOrigin = flightOrigin;
    }

    public String getFlightAirline() {
        return flightAirline;
    }

    public void setFlightAirline(String flightAirline) {
        this.flightAirline = flightAirline;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "flightDetailsModel{" +
                "id=" + id +
                ", flightNumber='" + flightNumber + '\'' +
                ", flightAirline='" + flightAirline + '\'' +
                ", flightOrigin='" + flightOrigin + '\'' +
                ", flightDestination='" + flightDestination + '\'' +
                ", flightDepartureDate='" + flightDepartureDate + '\'' +
                ", flightArrivalDate='" + flightArrivalDate + '\'' +
                ", flightDepartureTime='" + flightDepartureTime + '\'' +
                ", flightArrivalTime='" + flightArrivalTime + '\'' +
                ", flightTravelTime='" + flightTravelTime + '\'' +
                ", flightPrice='" + flightPrice + '\'' +
                '}';
    }
}
