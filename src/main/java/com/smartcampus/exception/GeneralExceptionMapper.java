package com.smartcampus.exception;

import com.smartcampus.model.ErrorMessage;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

// catch-all mapper - catches anything not handled by the other mappers
// returns 500 and logs it so the stack trace never reaches the client
// had to add the WebApplicationException check because without it jersey's own 404s were getting swallowed here
@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = Logger.getLogger(GeneralExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        logger.severe("Unhandled exception: " + exception.getMessage());

        ErrorMessage errorMsg = new ErrorMessage("Internal Server Error", 500, "Something went wrong on the server");
        return Response.status(500).entity(errorMsg).build();
    }
}
