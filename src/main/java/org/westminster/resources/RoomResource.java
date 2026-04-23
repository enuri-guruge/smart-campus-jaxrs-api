package org.westminster.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.westminster.models.DataStore;
import org.westminster.models.Room;

import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private DataStore dataStore = DataStore.getInstance();

    // 1. GET: List all rooms
    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(dataStore.getRooms().values());
    }

    // 2. GET: Specific room details
    @GET
    @Path("/{id}")
    public Response getRoomById(@PathParam("id") String id) {
        Room room = dataStore.getRooms().get(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found\"}")
                    .build();
        }
        return Response.ok(room).build();
    }

    // 3. POST: Create a new room
    @POST
    public Response addRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(422)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"error\":\"Room ID is required\"}")
                    .build();
        }
        dataStore.getRooms().put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // 4. DELETE: Remove a room (with safety check)
    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        // Check if room exists
        if (!dataStore.getRooms().containsKey(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Business Rule: Check if room is empty (no sensors assigned)
        boolean hasSensors = dataStore.getSensors().values().stream()
                .anyMatch(s -> s.getRoomId().equals(id));

        if (hasSensors) {
            // Return 409 Conflict as per README
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"error\":\"Cannot delete room. Sensors are still assigned to it.\"}")
                    .build();
        }

        dataStore.getRooms().remove(id);
        return Response.noContent().build(); // 204 No Content
    }
}