package com.smartcampus.exception;

import com.smartcampus.model.ErrorMessage;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// 422 - the request body was valid json but the roomId inside it doesnt exist
// different from 404 - the url was fine, the data inside the body was the problem
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        ErrorMessage errorMsg = new ErrorMessage("Unprocessable Entity", 422, exception.getMessage());
        return Response.status(422).entity(errorMsg).build();
    }
}
