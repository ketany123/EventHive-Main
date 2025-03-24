package com.EventHive.exception;

import org.springframework.http.HttpStatus;

public class UserExistsException extends CustomException{

    public UserExistsException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
