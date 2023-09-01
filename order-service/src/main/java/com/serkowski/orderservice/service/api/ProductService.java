package com.serkowski.orderservice.service.api;

import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {

    /**
     * Reserve items.
     *
     * @param orderItems order items
     * @return response
     */
    Mono<String> reserveItems(List<OrderItemRequestDto> orderItems);

}
