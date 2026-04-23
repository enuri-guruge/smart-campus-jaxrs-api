package org.westminster.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.westminster.models.DataStore;
import org.westminster.models.Room;

import java.util.ArrayList;
import java.util.List;

@Path("/rooms") // This makes the URL http://localhost:8080/api/v1/rooms
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private DataStore dataStore = DataStore.getInstance();

    // GET: List all rooms
    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(dataStore.getRooms().values());
    }

    // POST: Create a new room
    @POST
    public Response addRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(422).entity("Room ID is required").build();
        }
        dataStore.getRooms().put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }
}