package com.serkowski.productservice.model.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReservationItemsException extends RuntimeException{
    public ReservationItemsException(String message) {
        super(message);
    }
    public ReservationItemsException(String message, Throwable cause) {
        super(message, cause);
    }
}
