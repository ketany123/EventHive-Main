package com.EventHive.exception;

import org.springframework.http.HttpStatus;

public class SeatAlreadyBookedException extends CustomException{

    public SeatAlreadyBookedException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
