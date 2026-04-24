package com.smartcampus.dao;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// in-memory storage, no database
// started with ArrayList like the tutorial MockDatabase but HashMap is simpler for looking up by id
// private static final List<Room> rooms = new ArrayList<>();
public class DataStore {

    private static final Map<String, Room> rooms = new HashMap<>();
    private static final Map<String, Sensor> sensors = new HashMap<>();
    private static final Map<String, List<SensorReading>> sensorReadings = new HashMap<>();

    // had sample data here in a static block while testing, removed it
    // static {
    //     rooms.put("LIB-301", new Room("LIB-301", "Library Room 301", 30));
    // }

    // ---- rooms ----

    public static Room createRoom(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    // look up a room by id
    public static Room getRoomById(String id) {
        return rooms.get(id);
    }

    public static List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public static boolean roomExists(String id) {
        return rooms.containsKey(id);
    }

    // remove it from the map
    public static void deleteRoom(String id) {
        rooms.remove(id);
    }

    // ---- sensors ----

    public static Sensor createSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        // update the room so its sensorIds stays in sync
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().add(sensor.getId());
        }
        return sensor;
    }

    public static Sensor getSensorById(String id) {
        return sensors.get(id);
    }

    public static List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }

    public static List<Sensor> getSensorsByType(String type) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor sensor : sensors.values()) {
            if (sensor.getType().equalsIgnoreCase(type)) {
                result.add(sensor);
            }
        }
        return result;
    }

    public static void deleteSensor(String id) {
        Sensor sensor = sensors.remove(id);
        if (sensor != null) {
            // remove from the rooms list too otherwise it shows stale data
            Room room = rooms.get(sensor.getRoomId());
            if (room != null) {
                room.getSensorIds().remove(id);
            }
            sensorReadings.remove(id);
        }
    }

    public static void updateSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    // ---- readings ----

    public static SensorReading addReading(String sensorId, SensorReading reading) {
        List<SensorReading> readings = sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>());
        readings.add(reading);
        return reading;
    }

    public static List<SensorReading> getReadings(String sensorId) {
        List<SensorReading> readings = sensorReadings.get(sensorId);
        return readings != null ? new ArrayList<>(readings) : new ArrayList<>();
    }
}
