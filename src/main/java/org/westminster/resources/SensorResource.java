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

    // 1. GET all sensors: http://localhost:8080/api/v1/sensors
    @GET
    public List<Sensor> getAllSensors() {
        return List.copyOf(dataStore.getSensors().values());
    }

    // 2. POST a new sensor: http://localhost:8080/api/v1/sensors
    @POST
    public Response addSensor(Sensor sensor) {
        // Business Rule: Check if the room exists before adding sensor
        if (!dataStore.getRooms().containsKey(sensor.getRoomId())) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found. Cannot assign sensor.\"}")
                    .build();
        }

        dataStore.getSensors().put(sensor.getId(), sensor);
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // 3. GET sensors for a specific room: http://localhost:8080/api/v1/sensors/room/{roomId}
    @GET
    @Path("/room/{roomId}")
    public List<Sensor> getSensorsByRoom(@PathParam("roomId") String roomId) {
        return dataStore.getSensors().values().stream()
                .filter(s -> s.getRoomId().equals(roomId))
                .collect(Collectors.toList());
    }
}