package com.serkowski.productservice.model.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AddItemIndexException extends RuntimeException{
    public AddItemIndexException(String message) {
        super(message);
    }
    public AddItemIndexException(String message, Throwable cause) {
        super(message, cause);
    }
}
