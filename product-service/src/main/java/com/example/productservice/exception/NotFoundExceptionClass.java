package com.example.productservice.exception;

public class NotFoundExceptionClass extends RuntimeException {
    public NotFoundExceptionClass(String message) {
        super(message);
    }
}
