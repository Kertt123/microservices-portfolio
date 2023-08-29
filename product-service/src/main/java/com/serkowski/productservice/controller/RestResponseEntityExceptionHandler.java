package com.serkowski.productservice.controller;

import com.serkowski.productservice.dto.ErrorHandlerItem;
import com.serkowski.productservice.dto.ErrorHandlerResponse;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.model.error.ReservationItemsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        ex.getBindingResult().getAllErrors().forEach((error) ->
                response.getErrors().add(ErrorHandlerItem.builder()
                        .fieldName(((FieldError) error).getField())
                        .errorMessage(error.getDefaultMessage())
                        .build())
        );
        return new ResponseEntity(Optional.of(response), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public final ErrorHandlerResponse handleProductNotFound(ProductNotFound ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        response.setErrorMessage(ex.getMessage());
        return response;
    }

    @ExceptionHandler(ReservationItemsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public final ErrorHandlerResponse handleReservationException(ReservationItemsException ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        response.setErrorMessage(ex.getMessage());
        return response;
    }
}
