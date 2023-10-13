package com.serkowski.orderservice.controller;

import com.serkowski.orderservice.dto.ErrorHandlerItem;
import com.serkowski.orderservice.dto.ErrorHandlerResponse;
import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.model.error.ApiCallException;
import com.serkowski.orderservice.model.error.OrderNotFound;
import com.serkowski.orderservice.model.error.ValidationException;
import com.serkowski.orderservice.service.api.OrderService;
import io.micrometer.tracing.annotation.NewSpan;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/draft")
    @ResponseStatus(HttpStatus.CREATED)
    @NewSpan("createOrderDraft")
    public Mono<OrderResponse> placeOrderDraft(@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.placeOrderDraft(orderRequest)
                .map(orderResponse -> {
                    orderResponse.add(linkTo(OrderController.class).slash(orderResponse.getOrderNumber()).slash(orderResponse.getVersion()).withSelfRel());
                    return orderResponse;
                });
    }

    @PostMapping("/accept/{orderNumber}/{versionNumber}")
    @NewSpan("acceptOrder")
    public Mono<OrderResponse> acceptOrder(@PathVariable String orderNumber, @PathVariable Integer versionNumber) {
        return orderService.acceptOrder(orderNumber, versionNumber)
                .map(orderResponse -> {
                    orderResponse.add(linkTo(OrderController.class).slash(orderResponse.getOrderNumber()).slash(orderResponse.getVersion()).withSelfRel());
                    return orderResponse;
                });
    }

    @PutMapping("/draft/{orderNumber}/{versionNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<OrderResponse> updateOrderDraft(@RequestBody OrderRequest orderRequest, @PathVariable String orderNumber, @PathVariable Integer versionNumber) {
        OrderResponse response = orderService.updateOrder(orderRequest, orderNumber, versionNumber);
        response.add(linkTo(OrderController.class).slash(response.getOrderNumber()).slash(response.getVersion()).withSelfRel());
        return Mono.just(response);
    }

    @GetMapping("/{orderNumber}/{versionNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<OrderResponse> getOrder(@PathVariable String orderNumber, @PathVariable Integer versionNumber) {
        OrderResponse response = orderService.getOrderByOrderNumber(orderNumber, versionNumber);
        response.add(linkTo(OrderController.class).slash(response.getOrderNumber()).slash(response.getVersion()).withSelfRel());
        return Mono.just(response);
    }

    @DeleteMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrder(@PathVariable String orderNumber) {
        orderService.deleteOrderByOrderNumber(orderNumber);
    }

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

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Mono<ErrorHandlerResponse> handleValidationException(ValidationException ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        response.setErrorMessage(ex.getMessage());
        return Mono.just(response);
    }

    @ExceptionHandler(OrderNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ErrorHandlerResponse> handleOrderNotFound(OrderNotFound ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        response.setErrorMessage(ex.getMessage());
        return Mono.just(response);
    }

    @ExceptionHandler(ApiCallException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorHandlerResponse> handleApiCallException(ApiCallException ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        response.setErrorMessage(ex.getMessage());
        return Mono.just(response);
    }
}