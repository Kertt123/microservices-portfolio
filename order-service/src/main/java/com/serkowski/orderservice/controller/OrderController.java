package com.serkowski.orderservice.controller;

import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Placing Order");
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CompletableFuture<OrderResponse> updateOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Placing Order");
        return CompletableFuture.supplyAsync(() -> orderService.updateOrder(orderRequest));
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
}