package com.rest.earthquakeapi.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Exception handler to handle custom exception
    @ExceptionHandler(QuakeDataNotFoundException.class)
    public ResponseEntity<QuakeDataErrorResponse> handleException(QuakeDataNotFoundException exc) {
        QuakeDataErrorResponse error = new QuakeDataErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
