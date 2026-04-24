package com.smartcampus.exception;

// thrown when someone tries to post a reading to a sensor thats in MAINTENANCE status
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}
