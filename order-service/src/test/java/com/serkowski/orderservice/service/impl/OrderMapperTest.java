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

class OrderMapperTest {

    private final OrderMapper orderMapper = new OrderMapperImpl();


    @Test
    void shouldMapOrderRequestToOrderSummary() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(orderItems());
        orderRequest.setAddress(address());

        OrderSummary result = orderMapper.map(orderRequest, State.DRAFT);

        assertAll(
                "Map request to summary",
                () -> assertEquals(1, result.getOrderLineItemsList().get(0).getCount(), "Count should be 1"),
                () -> assertEquals("name", result.getOrderLineItemsList().get(0).getItemName(), "Item name should be \"name\""),
                () -> assertEquals("ref1", result.getOrderLineItemsList().get(0).getItemRef(), "Item reference should be \"ref1\""),
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
                .state(State.DRAFT)
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
                () -> assertEquals("test123", result.getOrderNumber(), "Order number should be \"test123\""),
                () -> assertEquals("DRAFT", result.getState(), "State should be \"draft\""),
                () -> assertEquals(12, result.getOrderItems().get(0).getCount(), "Count should be 12"),
                () -> assertEquals("item name", result.getOrderItems().get(0).getItemName(), "Item name should be \"item name\""),
                () -> assertEquals("ref1", result.getOrderItems().get(0).getItemRef(), "Item reference should be \"ref1\""),
                () -> assertEquals("line1", result.getAddress().getAddressLine1(), "First address line is \"line1\""),
                () -> assertEquals("line2", result.getAddress().getAddressLine2(), "Second address line is \"line2\""),
                () -> assertEquals("city1", result.getAddress().getCity(), "City is \"city1\""),
                () -> assertEquals("country", result.getAddress().getCountry(), "Country is \"country\"")
        );
    }

    @Test
    void shouldMapItemsRequestToEntity() {
        OrderItemRequestDto itemRequestDto = OrderItemRequestDto.builder()
                .count(1)
                .itemName("item1")
                .itemRef("ref1")
                .build();

        List<OrderItem> result = orderMapper.mapItems(List.of(itemRequestDto));

        assertAll(
                "Map request items to entity list",
                () -> assertEquals("item1", result.get(0).getItemName(), "Item name should be \"item1\""),
                () -> assertEquals(1, result.get(0).getCount(), "Count should be 1"),
                () -> assertEquals("ref1", result.get(0).getItemRef(), "Item reference should be \"ref1\"")
        );
    }

    @Test
    void shouldMapAddressRequestToEntity() {
        AddressRequestDto addressRequestDto = AddressRequestDto.builder()
                .addressLine1("line1")
                .addressLine2("line2")
                .city("city")
                .country("country")
                .build();

        Address result = orderMapper.mapAddress(addressRequestDto);

        assertAll(
                "Map request address to entity",
                () -> assertEquals("line1", result.getAddressLine1(), "First address line is \"line1\""),
                () -> assertEquals("line2", result.getAddressLine2(), "Second address line is \"line2\""),
                () -> assertEquals("city", result.getCity(), "City is \"city1\""),
                () -> assertEquals("country", result.getCountry(), "Country is \"country\"")
        );
    }

    private List<OrderItem> prepareOrderLineItems() {
        return List.of(OrderItem.builder()
                .id(1L)
                .count(12)
                .itemName("item name")
                .itemRef("ref1")
                .build());
    }

    private List<OrderItemRequestDto> orderItems() {
        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setCount(1);
        orderItemRequestDto.setItemName("name");
        orderItemRequestDto.setItemRef("ref1");
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