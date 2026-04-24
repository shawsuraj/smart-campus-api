package com.smartcampus.exception;

// thrown when you try to delete a room that still has sensors in it
// extends RuntimeException so you dont have to add throws to every method signature
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}
