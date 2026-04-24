package com.smartcampus.exception;

import com.smartcampus.model.ErrorMessage;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// catches RoomNotEmptyException and returns 409 Conflict
// @Provider tells JAX-RS to pick this up - same as the tutorial showed for DataNotFoundExceptionMapper
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        ErrorMessage errorMsg = new ErrorMessage("Conflict", 409, exception.getMessage());
        return Response.status(409).entity(errorMsg).build();
    }
}
