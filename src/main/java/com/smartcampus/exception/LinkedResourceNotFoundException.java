package com.smartcampus.exception;

// thrown when a sensor is created with a roomId that doesnt exist
// linked resource means the thing it references doesnt exist - maps to 422
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
