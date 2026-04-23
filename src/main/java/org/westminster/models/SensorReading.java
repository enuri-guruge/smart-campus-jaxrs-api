package org.westminster.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SensorReading {
    private double value;
    private String timestamp;

    public SensorReading() {
        // Automatically set the time when the reading is created
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public SensorReading(double value) {
        this();
        this.value = value;
    }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getTimestamp() { return timestamp; }
}