package com.EventHive.exception;

import org.springframework.http.HttpStatus;

public class TheaterNotFoundException extends CustomException {
    public TheaterNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
