package com.EventHive.exception;

import com.EventHive.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AmountNotMatchException extends CustomException {

    public AmountNotMatchException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
