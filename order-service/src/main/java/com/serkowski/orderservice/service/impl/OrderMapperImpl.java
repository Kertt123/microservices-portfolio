package com.serkowski.orderservice.service.impl;

import com.serkowski.orderservice.dto.request.AddressRequestDto;
import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.AddressResponseDto;
import com.serkowski.orderservice.dto.response.OrderItemResponseDto;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.model.Address;
import com.serkowski.orderservice.model.OrderItem;
import com.serkowski.orderservice.model.OrderSummary;
import com.serkowski.orderservice.model.State;
import com.serkowski.orderservice.service.api.OrderMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderSummary map(OrderRequest orderRequest, State state) {
        return OrderSummary.builder()
                .orderNumber(String.valueOf(UUID.randomUUID()))
                .orderLineItemsList(mapItems(orderRequest.getOrderItems()))
                .state(state)
                .address(mapAddress(orderRequest.getAddressDto()))
                .build();
    }

    @Override
    public OrderResponse map(OrderSummary orderSummary) {
        return OrderResponse.builder()
                .orderNumber(orderSummary.getOrderNumber())
                .state(orderSummary.getState().name())
                .orderItems(mapResponseItems(orderSummary.getOrderLineItemsList()))
                .address(mapAddressResponse(orderSummary.getAddress()))
                .build();
    }

    @Override
    public List<OrderItem> mapItems(List<OrderItemRequestDto> orderItems) {
        return orderItems.stream().map(orderItemDto -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setItemName(orderItemDto.getItemName());
            orderItem.setCount(orderItemDto.getCount());
            orderItem.setItemRef(orderItemDto.getItemRef());
            return orderItem;
        }).collect(Collectors.toList());
    }
    @Override
    public Address mapAddress(AddressRequestDto addressDto) {
        Address address = new Address();
        address.setAddressLine1(addressDto.getAddressLine1());
        address.setAddressLine2(addressDto.getAddressLine2());
        address.setCity(addressDto.getCity());
        address.setCountry(addressDto.getCountry());
        return address;
    }

    private List<OrderItemResponseDto> mapResponseItems(List<OrderItem> orderLineItemsList) {
        return orderLineItemsList.stream()
                .map(entity ->
                        OrderItemResponseDto.builder()
                                .id(entity.getId())
                                .count(entity.getCount())
                                .itemName(entity.getItemName())
                                .itemRef(entity.getItemRef())
                                .build()
                ).collect(Collectors.toList());
    }

    private AddressResponseDto mapAddressResponse(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .country(address.getCountry())
                .build();
    }
}
