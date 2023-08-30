package com.serkowski.orderservice.controller;

import com.serkowski.orderservice.dto.ErrorHandlerItem;
import com.serkowski.orderservice.dto.ErrorHandlerResponse;
import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.model.error.OrderNotFound;
import com.serkowski.orderservice.service.api.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
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
    public Mono<OrderResponse> placeOrderDraft(@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.placeOrderDraft(orderRequest)
                .map(orderResponse -> {
                    orderResponse.add(linkTo(OrderController.class).slash(orderResponse.getOrderNumber()).withSelfRel());
                    return orderResponse;
                });
    }

    @PutMapping("/draft/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<OrderResponse> updateOrderDraft(@RequestBody OrderRequest orderRequest, @PathVariable String orderNumber) {
        OrderResponse response = orderService.updateOrder(orderRequest, orderNumber);
        response.add(linkTo(OrderController.class).slash(response.getOrderNumber()).withSelfRel());
        return Mono.just(response);
    }

    @GetMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<OrderResponse> getOrder(@PathVariable String orderNumber) {
        OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);
        response.add(linkTo(OrderController.class).slash(response.getOrderNumber()).withSelfRel());
        return Mono.just(response);
    }

    @DeleteMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrder(@PathVariable String orderNumber) {
        orderService.deleteOrderByOrderNumber(orderNumber);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ErrorHandlerResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        ex.getBindingResult().getAllErrors().forEach((error) ->
                response.getErrors().add(ErrorHandlerItem.builder()
                        .fieldName(((FieldError) error).getField())
                        .errorMessage(error.getDefaultMessage())
                        .build())
        );
        return Mono.just(response);
    }

    @ExceptionHandler(OrderNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ErrorHandlerResponse> handleOrderNotFound(OrderNotFound ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        response.setErrorMessage(ex.getMessage());
        return Mono.just(response);
    }
}