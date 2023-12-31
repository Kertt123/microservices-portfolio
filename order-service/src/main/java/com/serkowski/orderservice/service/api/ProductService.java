package com.serkowski.orderservice.service.api;

import com.serkowski.orderservice.model.OrderItem;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {

    /**
     * Reserve items.
     *
     * @param orderNumber order number
     * @param orderItems  order items
     * @return response
     */
    Mono<String> reserveItems(String orderNumber, List<OrderItem> orderItems);

}
