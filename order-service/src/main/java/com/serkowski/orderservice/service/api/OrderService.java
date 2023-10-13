package com.serkowski.orderservice.service.api;

import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;
import reactor.core.publisher.Mono;

public interface OrderService {


    /**
     * Place order draft.
     *
     * @param orderRequest request
     * @return {@link OrderResponse}
     */
    Mono<OrderResponse> placeOrderDraft(OrderRequest orderRequest);

    /**
     * Place order draft.
     *
     * @param orderNumber order number
     * @param versionNumber version number
     * @return {@link OrderResponse}
     */
    Mono<OrderResponse> acceptOrder(String orderNumber, Integer versionNumber);

    /**
     * Update already existing order draft
     *
     * @param orderRequest  request
     * @param orderNumber   order number
     * @param versionNumber version number
     * @return {@link OrderResponse}
     */
    OrderResponse updateOrder(OrderRequest orderRequest, String orderNumber, Integer versionNumber);

    /**
     * Get order by order number
     *
     * @param orderNumber   order number
     * @param versionNumber version number
     * @return {@link OrderResponse}
     */
    OrderResponse getOrderByOrderNumber(String orderNumber, Integer versionNumber);

    /**
     * Delete order by order number.
     *
     * @param orderNumber order number
     */
    void deleteOrderByOrderNumber(String orderNumber);

}
