package org.westminster.models;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private static DataStore instance;

    // 1. Storage for Rooms
    private Map<String, Room> rooms = new ConcurrentHashMap<>();

    // 2. Storage for Sensors
    private Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    // 3. Storage for the Readings (Data from sensors)
    private Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {
        // FIXED: Changed "1st Floor" to an integer (e.g., 50) to match the new Room constructor
        rooms.put("R1", new Room("R1", "Library", 50));
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // Getters so your Resources can access the maps
    public Map<String, Room> getRooms() { return rooms; }

    public Map<String, Sensor> getSensors() { return sensors; }

    public Map<String, List<SensorReading>> getReadings() { return readings; }
}