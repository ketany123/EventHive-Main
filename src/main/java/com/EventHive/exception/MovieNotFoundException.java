package com.EventHive.exception;

import org.springframework.http.HttpStatus;

public class MovieNotFoundException extends CustomException {
    public MovieNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
