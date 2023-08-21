package com.serkowski.orderservice.service;

import com.serkowski.orderservice.dto.request.AddressRequestDto;
import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.AddressResponseDto;
import com.serkowski.orderservice.dto.response.OrderItemResponseDto;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.model.Address;
import com.serkowski.orderservice.model.Order;
import com.serkowski.orderservice.model.OrderItem;
import com.serkowski.orderservice.repository.read.OrderReadRepository;
import com.serkowski.orderservice.repository.write.OrderWriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderWriteRepository orderWriteRepository;
    private final OrderReadRepository orderReadRepository;

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(String.valueOf(UUID.randomUUID()));
        order.setOrderLineItemsList(mapItems(orderRequest.getOrderItems()));
        order.setAddress(mapAddress(orderRequest.getAddressDto()));
        Order save = orderWriteRepository.save(order);

        return OrderResponse.builder()
                .id(save.getId())
                .oderNumber(save.getOrderNumber())
                .orderItems(mapResponseItems(save.getOrderLineItemsList()))
                .addressDto(mapAddressResponse(save.getAddress()))
                .build();
    }

    public OrderResponse updateOrder(OrderRequest orderRequest) {
        return null;
    }

    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderReadRepository.findByOrderNumber(orderNumber);
        return OrderResponse.builder()
                .id(order.getId())
                .oderNumber(order.getOrderNumber())
                .orderItems(mapResponseItems(order.getOrderLineItemsList()))
                .addressDto(mapAddressResponse(order.getAddress()))
                .build();
    }

    public void deleteOrderByOrderNumber(String orderNumber) {
        orderWriteRepository.deleteByOrderNumber(orderNumber);
    }

    private List<OrderItem> mapItems(List<OrderItemRequestDto> orderItems) {
        return orderItems.stream().map(orderItemDto -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setItemName(orderItemDto.getItemName());
            orderItem.setCount(orderItem.getCount());
            return orderItem;
        }).collect(Collectors.toList());
    }

    private Address mapAddress(AddressRequestDto addressDto) {
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
