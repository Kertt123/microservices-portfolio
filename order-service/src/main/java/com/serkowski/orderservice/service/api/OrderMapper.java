package com.serkowski.orderservice.service.api;

import com.serkowski.orderservice.dto.request.AddressRequestDto;
import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.model.Address;
import com.serkowski.orderservice.model.OrderItem;
import com.serkowski.orderservice.model.OrderSummary;
import com.serkowski.orderservice.model.State;

import java.util.List;

public interface OrderMapper {


    /**
     * Map {@link OrderRequest} to {@link OrderSummary}
     *
     * @param orderRequest request
     * @param state        order state
     * @return {@link OrderSummary}
     */
    public OrderSummary map(OrderRequest orderRequest, State state);

    /**
     * Map {@link OrderSummary} to {@link OrderResponse}
     *
     * @param orderSummary summary
     * @return {@link OrderResponse}
     */
    public OrderResponse map(OrderSummary orderSummary);

    /**
     * Map {@link OrderItemRequestDto} to {@link OrderItem}
     *
     * @param orderItems orderItems
     * @return {@link OrderItem}
     */
    List<OrderItem> mapItems(List<OrderItemRequestDto> orderItems);

    /**
     * Map {@link AddressRequestDto} to {@link Address}
     *
     * @param addressDto adress
     * @return {@link Address}
     */
    Address mapAddress(AddressRequestDto addressDto);
}
