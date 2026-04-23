package org.westminster.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.westminster.models.DataStore;
import org.westminster.models.Sensor;

import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private DataStore dataStore = DataStore.getInstance();

    /**
     * 1. GET sensors
     * Supports filtering by type via QueryParam: /api/v1/sensors?type=Temp
     */
    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = List.copyOf(dataStore.getSensors().values());

        if (type != null && !type.isEmpty()) {
            return allSensors.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        return allSensors;
    }

    /**
     * 2. POST a new sensor
     * Validates referential integrity against the Room store.
     * Returns 422 if the roomId does not exist.
     */
    @POST
    public Response addSensor(Sensor sensor) {
        // Business Rule: Check if the room exists in the DataStore
        if (!dataStore.getRooms().containsKey(sensor.getRoomId())) {
            String errorJson = "{\"error\":\"Room not found. Cannot assign sensor.\"}";

            // Return 422 Unprocessable Entity with explicit JSON header
            return Response.status(422)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(errorJson)
                    .build();
        }

        // Add to store and return 201 Created
        dataStore.getSensors().put(sensor.getId(), sensor);
        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    /**
     * 3. GET sensors for a specific room
     * Identified by path variable: /api/v1/sensors/room/{roomId}
     */
    @GET
    @Path("/room/{roomId}")
    public List<Sensor> getSensorsByRoom(@PathParam("roomId") String roomId) {
        return dataStore.getSensors().values().stream()
                .filter(s -> s.getRoomId().equals(roomId))
                .collect(Collectors.toList());
    }
}