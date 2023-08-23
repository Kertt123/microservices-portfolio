package com.serkowski.orderservice.service.impl;

import com.serkowski.orderservice.dto.request.AddressRequestDto;
import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.model.Address;
import com.serkowski.orderservice.model.OrderItem;
import com.serkowski.orderservice.model.OrderSummary;
import com.serkowski.orderservice.model.State;
import com.serkowski.orderservice.service.api.OrderMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class OrderMapperTest {

    private OrderMapper orderMapper = new OrderMapperImpl();


    @Test
    void shouldMapOrderRequestToOrderSummary() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(orderItems());
        orderRequest.setAddressDto(address());

        OrderSummary result = orderMapper.map(orderRequest, State.DRAFT);

        assertAll(
                "Map request to summary",
                () -> assertEquals(1, result.getOrderLineItemsList().get(0).getCount(), "Count should be 1"),
                () -> assertEquals("name", result.getOrderLineItemsList().get(0).getItemName(), "Item name should be \"name\""),
                () -> assertEquals("test", result.getAddress().getAddressLine1(), "First address line is \"test\""),
                () -> assertEquals("test2", result.getAddress().getAddressLine2(), "Second address line is \"test2\""),
                () -> assertEquals("city", result.getAddress().getCity(), "City is \"city\""),
                () -> assertEquals("country", result.getAddress().getCountry(), "Country is \"country\"")
        );
    }

    @Test
    void shouldMapOrderSummaryToOrderResponse() {
        OrderSummary orderSummary = OrderSummary.builder()
                .id(1L)
                .orderNumber("test123")
                .orderLineItemsList(prepareOrderLineItems())
                .address(Address.builder()
                        .addressLine1("line1")
                        .addressLine2("line2")
                        .city("city1")
                        .country("country")
                        .build())
                .build();

        OrderResponse result = orderMapper.map(orderSummary);

        assertAll(
                "Map summary to response",
                () -> assertEquals(1L, result.getId(), "Count should be 1"),
                () -> assertEquals(12, result.getOrderItems().get(0).getCount(), "Count should be 1"),
                () -> assertEquals("name", result.getOrderLineItemsList().get(0).getItemName(), "Item name should be \"name\""),
                () -> assertEquals("test", result.getAddress().getAddressLine1(), "First address line is \"test\""),
                () -> assertEquals("test2", result.getAddress().getAddressLine2(), "Second address line is \"test2\""),
                () -> assertEquals("city", result.getAddress().getCity(), "City is \"city\""),
                () -> assertEquals("country", result.getAddress().getCountry(), "Country is \"country\"")
        );
    }

    private List<OrderItem> prepareOrderLineItems() {
        return List.of(OrderItem.builder()
                .id(1L)
                .count(12)
                .itemName("item name")
                .build());
    }

    private List<OrderItemRequestDto> orderItems() {
        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setCount(1);
        orderItemRequestDto.setItemName("name");
        return List.of(orderItemRequestDto);
    }

    private AddressRequestDto address() {
        AddressRequestDto addressRequestDto = new AddressRequestDto();
        addressRequestDto.setAddressLine1("test");
        addressRequestDto.setAddressLine2("test2");
        addressRequestDto.setCity("city");
        addressRequestDto.setCountry("country");
        return addressRequestDto;

    }

}