package com.serkowski.productservice.model.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
