package com.example.travelbuddybackend.models;

public class BusStation {

    private Integer id;
    private String busStationFullName;
    private String busStationCode;
    private String busStationCityLocation;

    public BusStation() {}

    public BusStation(Integer id, String busStationFullName, String busStationCode, String busStationCityLocation) {
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
