package com.example.travelbuddybackend.models;

//UNFINISHED IMPLEMENTATION
public class AvailableTicket {
    private final Long id;
    private final String transportType;
    private final String number; // flight/train/bus number
    private final String departureLocation;
    private final String arrivalLocation;
    private final String departureTime;
    private final String arrivalTime;
    private final Double price;
    private final String additionalInfo; // airline, line, etc.

    public AvailableTicket(Long id, String transportType, String number,
                           String departureLocation, String arrivalLocation,
                           String departureTime, String arrivalTime,
                           Double price, String additionalInfo) {
        this.id = id;
        this.transportType = transportType;
        this.number = number;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.additionalInfo = additionalInfo;
    }

    // Getters
    public Long getId() { return id; }
    public String getTransportType() { return transportType; }
    public String getNumber() { return number; }
    public String getDepartureLocation() { return departureLocation; }
    public String getArrivalLocation() { return arrivalLocation; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public Double getPrice() { return price; }
    public String getAdditionalInfo() { return additionalInfo; }
}