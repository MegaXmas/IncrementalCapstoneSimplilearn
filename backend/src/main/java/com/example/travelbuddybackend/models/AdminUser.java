package com.example.travelbuddybackend.models;

public class AdminUser {

    private Integer id;
    private String adminUsername;
    private String adminPassword;

//==================-Constructors===================
    public AdminUser() {}

    public AdminUser(Integer id, String adminUsername, String adminPassword) {
        this.id = id;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    @Override
    public String toString() {
        return "adminUserModel{" +
                "id=" + id +
                ", adminUsername='" + adminUsername + '\'' +
                ", adminPassword='" + adminPassword + '\'' +
                '}';
    }
}
