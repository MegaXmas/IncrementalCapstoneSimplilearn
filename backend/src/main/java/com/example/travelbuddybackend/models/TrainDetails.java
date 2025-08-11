package com.example.travelbuddybackend.models;

public class TrainDetails {

    private Integer id;
    private String trainNumber;
    private String trainLine;
    private String trainDepartureStation;
    private String trainArrivalStation;
    private String trainDepartureDate;
    private String trainDepartureTime;
    private String trainArrivalDate;
    private String trainArrivalTime;
    private String trainRideDuration;
    private String trainRidePrice;

    public TrainDetails() {}

    public TrainDetails(Integer id, String trainNumber, String trainLine, String trainDepartureStation, String trainArrivalStation,
                        String trainDepartureDate, String trainDepartureTime, String trainArrivalDate,
                        String trainArrivalTime, String trainRideDuration, String trainRidePrice) {
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
        this.trainNumber = trainNumber;
    }

    public String getTrainLine() {
        return trainLine;
    }

    public void setTrainLine(String trainLine) {
        this.trainLine = trainLine;
    }

    public String getTrainDepartureStation() {
        return trainDepartureStation;
    }

    public void setTrainDepartureStation(String trainDepartureStation) {
        this.trainDepartureStation = trainDepartureStation;
    }

    public String getTrainArrivalStation() {
        return trainArrivalStation;
    }

    public void setTrainArrivalStation(String trainArrivalStation) {
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
        return "trainDetailsModel{" +
                "id=" + id +
                ", trainNumber='" + trainNumber + '\'' +
                ", trainLine='" + trainLine + '\'' +
                ", trainDepartureStation='" + trainDepartureStation + '\'' +
                ", trainArrivalStation='" + trainArrivalStation + '\'' +
                ", trainDepartureDate='" + trainDepartureDate + '\'' +
                ", trainDepartureTime='" + trainDepartureTime + '\'' +
                ", trainArrivalDate='" + trainArrivalDate + '\'' +
                ", trainArrivalTime='" + trainArrivalTime + '\'' +
                ", trainRideDuration='" + trainRideDuration + '\'' +
                ", trainRidePrice='" + trainRidePrice + '\'' +
                '}';
    }
}
