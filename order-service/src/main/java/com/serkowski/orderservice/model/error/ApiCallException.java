package com.serkowski.orderservice.model.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class ApiCallException extends ResponseStatusException {

    private final HttpStatus status;
    private final String message;

    public ApiCallException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
        this.status = HttpStatus.resolve(statusCode.value());
        this.message = message;
    }
}
