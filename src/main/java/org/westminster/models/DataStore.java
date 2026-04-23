package org.westminster.models;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private static DataStore instance;

    // 1. Storage for Rooms
    private Map<String, Room> rooms = new ConcurrentHashMap<>();

    // 2. Storage for Sensors (Add this now!)
    private Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    // 3. Storage for the Readings (Data from sensors)
    private Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {
        // Default data for testing
        rooms.put("R1", new Room("R1", "Library", "1st Floor"));
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