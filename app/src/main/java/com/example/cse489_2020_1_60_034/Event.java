package com.example.cse489_2020_1_60_034;

public class Event {
    String key = "";
    String name = "";
    String place = "";
    String datetime = "";
    String capacity = "";
    String budget = "";
    String email = "";
    String phone = "";
    String description = "";
    String eventType = "";

    public Event(String key, String name, String place, String eventType, String datetime, String capacity, String budget, String email, String phone, String description){
        this.key = key;
        this.name = name;
        this.place = place;
        this.eventType = eventType;
        this.datetime = datetime;
        this.capacity = capacity;
        this.budget = budget;
        this.email = email;
        this.phone = phone;
        this.description = description;
    }
}
