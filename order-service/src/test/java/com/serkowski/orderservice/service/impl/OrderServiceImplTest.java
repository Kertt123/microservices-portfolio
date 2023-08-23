package com.serkowski.orderservice.service.impl;

import com.serkowski.orderservice.dto.request.AddressRequestDto;
import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.model.Address;
import com.serkowski.orderservice.model.OrderItem;
import com.serkowski.orderservice.model.OrderSummary;
import com.serkowski.orderservice.model.State;
import com.serkowski.orderservice.model.error.OrderNotFound;
import com.serkowski.orderservice.repository.read.OrderReadRepository;
import com.serkowski.orderservice.repository.write.OrderWriteRepository;
import com.serkowski.orderservice.service.api.OrderMapper;
import com.serkowski.orderservice.service.api.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    private OrderService orderService;
    @Mock
    private OrderWriteRepository orderWriteRepository;
    @Mock
    private OrderReadRepository orderReadRepository;
    @Mock
    private OrderMapper orderMapper;

    @BeforeEach
    void init() {
        orderService = new OrderServiceImpl(orderWriteRepository, orderReadRepository, orderMapper);
    }

    @Test
    void shouldPlaceOrder() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(orderItems());
        orderRequest.setAddressDto(address());
        when(orderMapper.map(eq(orderRequest), eq(State.DRAFT))).thenReturn(OrderSummary.builder().build());
        when(orderWriteRepository.save(any())).thenReturn(prepareOrder());

        orderService.placeOrderDraft(orderRequest);

        verify(orderWriteRepository).save(any(OrderSummary.class));
        verify(orderMapper).map(any(OrderSummary.class));
    }

    @Test
    void shouldUpdateOrder() {
        OrderResponse response = OrderResponse.builder().build();
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(orderItems());
        orderRequest.setAddressDto(address());
        when(orderReadRepository.findByOrderNumber(eq("testNumber123"))).thenReturn(Optional.ofNullable(OrderSummary.builder().build()));
        when(orderMapper.mapAddress(eq(orderRequest.getAddressDto()))).thenReturn(Address.builder().build());
        when(orderMapper.mapItems(eq(orderRequest.getOrderItems()))).thenReturn(List.of(OrderItem.builder().build()));
        when(orderMapper.map(any(OrderSummary.class))).thenReturn(response);
        when(orderWriteRepository.save(any())).thenReturn(prepareOrder());

        OrderResponse result = orderService.updateOrder(orderRequest, "testNumber123");

        assertEquals(response, result);
        verify(orderWriteRepository).save(any(OrderSummary.class));
        verify(orderMapper).map(any(OrderSummary.class));
    }

    @Test
    void shouldThrowExceptionDuringUpdateOrder() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(orderItems());
        orderRequest.setAddressDto(address());
        when(orderReadRepository.findByOrderNumber(eq("testNumber123"))).thenReturn(Optional.empty());

        OrderNotFound exception = assertThrows(OrderNotFound.class, () -> {
            orderService.updateOrder(orderRequest, "testNumber123");
        });
        assertEquals("Can't update order which is not exist for number: testNumber123", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionDuringGetOrder() {
        when(orderReadRepository.findByOrderNumber(eq("testNumber123"))).thenReturn(Optional.empty());

        OrderNotFound exception = assertThrows(OrderNotFound.class, () -> {
            orderService.getOrderByOrderNumber("testNumber123");
        });
        assertEquals("Order which number: testNumber123 not exist", exception.getMessage());
    }

    @Test
    void shouldGetOrderByOrderNumber() {
        OrderResponse response = OrderResponse.builder().build();
        when(orderReadRepository.findByOrderNumber(eq("testNumber123"))).thenReturn(Optional.ofNullable(OrderSummary.builder().build()));
        when(orderMapper.map(any(OrderSummary.class))).thenReturn(response);

        OrderResponse result = orderService.getOrderByOrderNumber("testNumber123");

        assertEquals(response, result);
    }

    @Test
    void shouldDeleteOrderByOrderNumber() {
        OrderSummary order = OrderSummary.builder().build();
        when(orderReadRepository.findByOrderNumber(eq("testNumber123"))).thenReturn(Optional.ofNullable(order));

        orderService.deleteOrderByOrderNumber("testNumber123");

        verify(orderWriteRepository).delete(eq(order));
    }

    @Test
    void shouldThrowExceptionDuringDeleteOrder() {
        when(orderReadRepository.findByOrderNumber(eq("testNumber123"))).thenReturn(Optional.empty());

        OrderNotFound exception = assertThrows(OrderNotFound.class, () -> {
            orderService.deleteOrderByOrderNumber("testNumber123");
        });
        assertEquals("Order which number: testNumber123 not exist, so can't be deleted", exception.getMessage());
    }

    private OrderSummary prepareOrder() {
        return OrderSummary.builder()
                .id(1L)
                .build();
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