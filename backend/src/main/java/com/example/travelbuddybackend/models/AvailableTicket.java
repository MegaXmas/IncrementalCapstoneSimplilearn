package com.example.travelbuddybackend.models;

// Simple result class that combines available tickets from all services
public class AvailableTicket {
    private Long id;
    private String transportType;
    private String number; // flight/train/bus number
    private String departureLocation;
    private String arrivalLocation;
    private String departureTime;
    private String arrivalTime;
    private Double price;
    private String additionalInfo; // airline, line, etc.

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