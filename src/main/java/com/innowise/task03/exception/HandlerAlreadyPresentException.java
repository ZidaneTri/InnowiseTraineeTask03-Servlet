package com.innowise.task03.exception;

public class HandlerAlreadyPresentException extends RuntimeException {

    public HandlerAlreadyPresentException(String message) {
        super(message);
    }
}
