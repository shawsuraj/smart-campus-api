package com.smartcampus.exception;

import com.smartcampus.model.ErrorMessage;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// catches SensorUnavailableException - happens when posting a reading to a MAINTENANCE sensor
// returns 403 Forbidden
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        ErrorMessage errorMsg = new ErrorMessage("Forbidden", 403, exception.getMessage());
        return Response.status(403).entity(errorMsg).build();
    }
}
