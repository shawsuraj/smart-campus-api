package com.smartcampus.model;

// sensor status can be ACTIVE, MAINTENANCE or OFFLINE
// MAINTENANCE ones block new readings - checked in SensorReadingResource
// same as Room - couldnt use BaseModel/GenericDAO from tutorial because ids here are strings not ints
public class Sensor {

    private String id;
    private String type;         // e.g. Temperature, CO2, Motion
    private String status;
    private double currentValue; // gets updated when a new reading is posted
    private String roomId;

    public Sensor() {
    }

    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
