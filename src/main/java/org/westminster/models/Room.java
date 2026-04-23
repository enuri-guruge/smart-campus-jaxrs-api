package org.westminster.models;

public class Room {
    private String id;
    private String name;
    private String location;

    // IMPORTANT: JAX-RS needs an empty constructor to work!
    public Room() {}

    public Room(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}