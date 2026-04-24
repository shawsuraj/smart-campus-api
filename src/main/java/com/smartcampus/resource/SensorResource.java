package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.dao.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // tried /sensors/type/{type} first but it clashed with /{sensorId} so switched to query param
    //
    //    @GET
    //    @Path("/type/{type}")
    //    public Response getSensorsByType(@PathParam("type") String type) {
    //        return Response.ok(DataStore.getSensorsByType(type)).build();
    //    }
    // get all sensors, or filter by type if ?type= is given
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> sensors;
        if (type != null && !type.isEmpty()) {
            sensors = DataStore.getSensorsByType(type);
        } else {
            sensors = DataStore.getAllSensors();
        }
        return Response.ok(sensors).build();
    }

    // roomId in the body must point to an existing room, 422 if not
    @POST
    public Response createSensor(Sensor sensor) {
        if (!DataStore.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room '" + sensor.getRoomId() + "' not found - sensor must be assigned to an existing room");
        }
        Sensor created = DataStore.createSensor(sensor);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // get a single sensor by id
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensorById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(buildError("Sensor not found", 404))
                    .build();
        }
        return Response.ok(sensor).build();
    }

    // sub-resource locator - @Path but no HTTP verb, JAX-RS calls this to get a SensorReadingResource
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsForSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensorById(sensorId);
        if (sensor == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return new SensorReadingResource(sensorId);
    }

    private Object buildError(String message, int status) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("status", status);
        error.put("message", message);
        return error;
    }
}
