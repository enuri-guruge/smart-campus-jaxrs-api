package org.westminster.models;

public class Sensor {
    private String id;
    private String name;
    private String type; // e.g., "Temperature" or "Humidity"
    private String roomId; // Links the sensor to a Room

    public Sensor() {}

    public Sensor(String id, String name, String type, String roomId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.roomId = roomId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}