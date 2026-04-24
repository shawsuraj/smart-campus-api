package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.dao.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// no class-level @Path here - kept getting 404s until i figured this out
// @Path("/readings")  <-- had this on the class, wrong, path goes on the locator in SensorResource
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // get all readings for this sensor
    @GET
    public Response getReadings() {
        List<SensorReading> readings = DataStore.getReadings(sensorId);
        return Response.ok(readings).build();
    }

    // add a new reading
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.getSensorById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(buildError("Sensor not found", 404))
                    .build();
        }

        // cant add readings if the sensor is down for maintenance
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor '" + sensorId + "' is in MAINTENANCE status and cannot accept new readings");
        }

        // server generates the id and timestamp, client just sends the value
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        SensorReading created = DataStore.addReading(sensorId, reading);

        // also update currentValue on the sensor so its always showing the latest
        sensor.setCurrentValue(reading.getValue());
        DataStore.updateSensor(sensor);

        // System.out.println("added reading for " + sensorId + " val=" + created.getValue());

        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    private Object buildError(String message, int status) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("status", status);
        error.put("message", message);
        return error;
    }
}
