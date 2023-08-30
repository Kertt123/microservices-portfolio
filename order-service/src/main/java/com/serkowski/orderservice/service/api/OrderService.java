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
     * Update already existing order draft
     *
     * @param orderRequest request
     * @param orderNumber  order number
     * @return {@link OrderResponse}
     */
    OrderResponse updateOrder(OrderRequest orderRequest, String orderNumber);

    /**
     * Get order by order number
     *
     * @param orderNumber order number
     * @return {@link OrderResponse}
     */
    OrderResponse getOrderByOrderNumber(String orderNumber);

    /**
     * Delete order by order number.
     *
     * @param orderNumber order number
     */
    void deleteOrderByOrderNumber(String orderNumber);

}
