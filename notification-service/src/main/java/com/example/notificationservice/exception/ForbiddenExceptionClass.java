package com.example.notificationservice.exception;

public class ForbiddenExceptionClass extends RuntimeException{
    public ForbiddenExceptionClass(String message){
        super(message);
    }
}
