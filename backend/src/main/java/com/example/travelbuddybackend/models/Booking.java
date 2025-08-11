package com.example.travelbuddybackend.models;

public class Booking {

    private Integer id;
    private String bookingId;
    private String transportDetailsJson;
    private String clientName;
    private String clientEmail;
    private String clientPhone;

    public Booking() {}

    public Booking(Integer id, String bookingId, String clientName, String clientEmail, String clientPhone, String transportDetailsJson) {
        this.id = id;
        this.bookingId = bookingId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.transportDetailsJson = transportDetailsJson;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getTransportDetailsJson() {
        return transportDetailsJson;
    }

    public void setTransportDetailsJson(String transportDetailsJson) {
        this.transportDetailsJson = transportDetailsJson;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", bookingId='" + bookingId + '\'' +
                ", transportDetailsJson='" + transportDetailsJson + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientEmail='" + clientEmail + '\'' +
                ", clientPhone='" + clientPhone + '\'' +
                '}';
    }
}
