package com.smartcampus.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

// logs every request and response - both interfaces in one class
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        // LOGGER.info("--- Incoming Request ---");  // had these as separate lines like the tutorial
        // LOGGER.info("Method: " + method);
        // LOGGER.info("URI: " + uri);
        logger.info("Incoming Request - Method: " + method + ", URI: " + uri);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();
        // LOGGER.info("--- Outgoing Response ---");
        // LOGGER.info("Status: " + status);
        logger.info("Response - Method: " + requestContext.getMethod() + ", Status: " + status);
    }
}
