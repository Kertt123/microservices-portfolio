package com.serkowski.orderservice.model.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ApiCallException extends RuntimeException{
    public ApiCallException(String message) {
        super(message);
    }
    public ApiCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
