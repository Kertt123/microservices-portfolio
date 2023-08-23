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

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/draft")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrderDraft(@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.placeOrderDraft(orderRequest);
    }

    @PutMapping("/draft")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse updateOrderDraft(@RequestBody OrderRequest orderRequest, @RequestParam String orderNumber) {
        return orderService.updateOrder(orderRequest, orderNumber);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CompletableFuture<OrderResponse> getOrder(@RequestParam String orderNumber) {
        log.info("Retrieving order by number");
        return CompletableFuture.supplyAsync(() -> orderService.getOrderByOrderNumber(orderNumber));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrder(@RequestParam String orderNumber) {
        orderService.deleteOrderByOrderNumber(orderNumber);
        log.info("Deleted");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorHandlerResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            response.getErrors().add(ErrorHandlerItem.builder()
                    .fieldName(((FieldError) error).getField())
                    .errorMessage(error.getDefaultMessage())
                    .build());
        });
        return response;
    }

    @ExceptionHandler(OrderNotFound.class)
    public ErrorHandlerResponse handleOrderNotFound(OrderNotFound ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        response.setErrorMessage(ex.getMessage());
        return response;
    }
}