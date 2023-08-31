package com.serkowski.productservice.controller;

import com.serkowski.productservice.dto.ErrorHandlerItem;
import com.serkowski.productservice.dto.ErrorHandlerResponse;
import com.serkowski.productservice.model.error.AddItemIndexException;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.model.error.ReservationItemsException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<ErrorHandlerResponse> handleException(WebExchangeBindException ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        ex.getBindingResult().getAllErrors().forEach((error) ->
                response.getErrors().add(ErrorHandlerItem.builder()
                        .fieldName(((FieldError) error).getField())
                        .errorMessage(error.getDefaultMessage())
                        .build())
        );
        return Mono.just(response);
    }

    @ExceptionHandler(ProductNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Mono<ErrorHandlerResponse> handleProductNotFound(ProductNotFound ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        response.setErrorMessage(ex.getMessage());
        return Mono.just(response);
    }

    @ExceptionHandler(value = {ReservationItemsException.class, AddItemIndexException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<ErrorHandlerResponse> handleReservationException(ReservationItemsException ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        response.setErrorMessage(ex.getMessage());
        return Mono.just(response);
    }
}
