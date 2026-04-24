package com.smartcampus.model;

// error response class - tutorial had something like this in the model package
// instead of building a HashMap in every mapper just use this as the response body
public class ErrorMessage {

    private String error;
    private int status;
    private String message;

    public ErrorMessage() {}

    public ErrorMessage(String error, int status, String message) {
        this.error = error;
        this.status = status;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
