package com.serkowski.productservice.model.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFound extends RuntimeException{
    public ProductNotFound(String message) {
        super(message);
    }
    public ProductNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
