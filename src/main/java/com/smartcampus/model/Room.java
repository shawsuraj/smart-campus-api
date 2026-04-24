package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

// tried implementing BaseModel like the tutorial but ids here are strings not ints so it didnt fit
public class Room {

    private String id; // e.g. "LIB-301" - string not int like the tutorial
    private String name;
    private int capacity;
    private List<String> sensorIds;

    // empty constructor needed - jackson uses it when deserialising the request body
    // if you dont have this you get a 400 with no useful error message
    public Room() {
        this.sensorIds = new ArrayList<>();
    }

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.sensorIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }
}
