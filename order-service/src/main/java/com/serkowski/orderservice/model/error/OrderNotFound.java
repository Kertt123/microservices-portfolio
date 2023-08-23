package com.serkowski.orderservice.model.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFound extends RuntimeException{
    public OrderNotFound(String message) {
        super(message);
    }
    public OrderNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
