package com.example.travelbuddybackend.models;

public class TrainStation {

    private Integer id;
    private String trainStationFullName;
    private String trainStationCode;
    private String trainStationCityLocation;

    public TrainStation() {}

    public TrainStation(Integer id, String trainStationFullName, String trainStationCode, String trainStationCityLocation) {
        this.id = id;
        this.trainStationFullName = trainStationFullName;
        this.trainStationCode = trainStationCode;
        this.trainStationCityLocation = trainStationCityLocation;
    }

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
        return "trainStationModel{" +
                "id=" + id +
                ", trainStationFullName='" + trainStationFullName + '\'' +
                ", trainStationCode='" + trainStationCode + '\'' +
                ", trainStationCityLocation='" + trainStationCityLocation + '\'' +
                '}';
    }
}
