package com.rest.earthquakeapi.exception;

public class QuakeDataNotFoundException extends RuntimeException{
    public QuakeDataNotFoundException(String message) {
        super(message);
    }

    public QuakeDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuakeDataNotFoundException(Throwable cause) {
        super(cause);
    }
}

