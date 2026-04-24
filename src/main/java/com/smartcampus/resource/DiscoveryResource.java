package com.smartcampus.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

// GET /api/v1 - root endpoint so clients can discover the api without hardcoding paths
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> discover() {
        Map<String, Object> response = new HashMap<>();

        response.put("name", "Smart Campus Sensor & Room Management API");
        response.put("version", "1.0.0");
        response.put("description", "RESTful API for managing rooms and sensors across campus");
        response.put("adminContact", "admin@smartcampus.edu");

        // links to the main resources so the client knows where to go
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        response.put("links", links);

        return response;
    }
}
