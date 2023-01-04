package com.example.iotapp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {

    private String email;
    private Map<String, Object> home;
    private String name;

    public User(){}

    public User(String email, Map<String,Object> home, String name) {
        this.email = email;
        this.home = home;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Object> getHome() {
        return home;
    }

    public String getName() {
        return name;
    }
}
