package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.dao.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // get all rooms
    @GET
    public Response getAllRooms() {
        List<Room> rooms = DataStore.getAllRooms();
        return Response.ok(rooms).build();
    }

    // create a new room
    @POST
    public Response createRoom(Room room) {
        Room created = DataStore.createRoom(room);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // get one room by its id
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRoomById(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(buildError("Room not found", 404))
                    .build();
        }
        return Response.ok(room).build();
    }

    // had Response.ok() here first which gives 200, should be 204 for delete
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRoomById(roomId);
        if (room == null) {
            // room doesnt exist, nothing to delete
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(buildError("Room not found", 404))
                    .build();
        }

        // cant delete if sensors are still in the room
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room '" + roomId + "' - it still has sensors assigned. Remove all sensors first.");
        }

        DataStore.deleteRoom(roomId);
        return Response.noContent().build();
    }

    private Object buildError(String message, int status) {
        java.util.Map<String, Object> error = new java.util.HashMap<>();
        error.put("error", "Not Found");
        error.put("status", status);
        error.put("message", message);
        return error;
    }
}
