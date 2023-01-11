package com.example.iotapp;

public class Member {

    private String name;
    private String role;
    private String photo;

    public Member(String name, String role, String photo) {
        this.name = name;
        this.role = role;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
