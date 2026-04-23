package org.westminster.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.westminster.models.DataStore;
import org.westminster.models.SensorReading;

import java.util.ArrayList;
import java.util.List;

@Path("/readings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReadingResource {

    private DataStore dataStore = DataStore.getInstance();

    // 1. POST a new reading: /api/v1/readings/{sensorId}
    @POST
    @Path("/{sensorId}")
    public Response addReading(@PathParam("sensorId") String sensorId, SensorReading reading) {
        // Check if sensor exists
        if (!dataStore.getSensors().containsKey(sensorId)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Sensor not found").build();
        }

        // Add reading to the list for this sensor
        dataStore.getReadings().computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

    // 2. GET all readings for a sensor: /api/v1/readings/{sensorId}
    @GET
    @Path("/{sensorId}")
    public List<SensorReading> getReadings(@PathParam("sensorId") String sensorId) {
        return dataStore.getReadings().getOrDefault(sensorId, new ArrayList<>());
    }
}