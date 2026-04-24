package com.smartcampus;

import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

// tried the web.xml package-scan approach first but switched to getClasses() so i know exactly whats registered
// @ApplicationPath("/api/v1") - had this but removed it, web.xml url-pattern handles the base path
public class ApiApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // resource classes
        classes.add(com.smartcampus.resource.DiscoveryResource.class);
        classes.add(com.smartcampus.resource.RoomResource.class);
        classes.add(com.smartcampus.resource.SensorResource.class);

        // SensorReadingResource not registered here - its a sub-resource, SensorResource creates it
        // classes.add(com.smartcampus.resource.SensorReadingResource.class); // had this, caused routing issues

        // exception mappers
        classes.add(com.smartcampus.exception.RoomNotEmptyExceptionMapper.class);
        classes.add(com.smartcampus.exception.LinkedResourceNotFoundExceptionMapper.class);
        classes.add(com.smartcampus.exception.SensorUnavailableExceptionMapper.class);
        classes.add(com.smartcampus.exception.GeneralExceptionMapper.class);

        // filter
        classes.add(com.smartcampus.filter.LoggingFilter.class);

        return classes;
    }
}
