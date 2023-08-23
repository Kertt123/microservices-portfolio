package com.serkowski.orderservice.service.api;

import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;

public interface OrderService {


    /**
     * Place order draft.
     *
     * @param orderRequest request
     * @return {@link OrderResponse}
     */
    public OrderResponse placeOrderDraft(OrderRequest orderRequest);

    /**
     * Update already existing order draft
     *
     * @param orderRequest request
     * @param orderNumber  order number
     * @return {@link OrderResponse}
     */
    public OrderResponse updateOrder(OrderRequest orderRequest, String orderNumber);

    /**
     * Get order by order number
     *
     * @param orderNumber order number
     * @return {@link OrderResponse}
     */
    public OrderResponse getOrderByOrderNumber(String orderNumber);

    /**
     * Delete order by order number.
     *
     * @param orderNumber
     */
    public void deleteOrderByOrderNumber(String orderNumber);

}
