package com.example.newsfeedservice.exception;

public class NotFoundExceptionClass extends RuntimeException {
    public NotFoundExceptionClass(String message) {
        super(message);
    }
}
