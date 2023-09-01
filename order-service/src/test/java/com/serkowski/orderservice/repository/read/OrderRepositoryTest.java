package com.serkowski.orderservice.repository.read;

import com.serkowski.orderservice.dto.request.AddressRequestDto;
import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.model.OrderSummary;
import com.serkowski.orderservice.model.State;
import com.serkowski.orderservice.model.error.OrderNotFound;
import com.serkowski.orderservice.repository.write.OrderWriteRepository;
import com.serkowski.orderservice.service.api.OrderMapper;
import com.serkowski.orderservice.service.api.OrderService;
import com.serkowski.orderservice.service.api.ProductService;
import com.serkowski.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration
@Testcontainers
class OrderRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("order-service");

    @BeforeAll
    static void beforeAll() {

        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void clean() {
        orderWriteRepository.deleteAll();
    }


    private OrderService orderService;
    @Autowired
    private OrderWriteRepository orderWriteRepository;
    @Autowired
    private OrderReadRepository orderReadRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Mock
    private ProductService productService;

    @BeforeEach
    void init() {
        orderService = new OrderServiceImpl(orderWriteRepository, orderReadRepository, orderMapper, productService);
    }

    @Test
    void shouldPlaceOrder() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(orderItems());
        orderRequest.setAddressDto(address());
        when(productService.reserveItems(any())).thenReturn(Mono.just("success"));

        StepVerifier
                .create(orderService.placeOrderDraft(orderRequest))
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();

        assertEquals(1, orderReadRepository.findAll().size());
    }

    @Test
    void shouldUpdateOrder() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(orderItems());
        orderRequest.setAddressDto(address());
        OrderSummary save = orderWriteRepository.save(orderMapper.map(orderRequest, State.DRAFT));
        orderRequest.getAddressDto().setCity("city2");

        orderService.updateOrder(orderRequest, save.getOrderNumber());

        orderReadRepository.findByOrderNumber(save.getOrderNumber()).ifPresent(orderSummary -> assertEquals("city2", orderSummary.getAddress().getCity()));
    }

    @Test
    void shouldThrowExceptionDuringUpdateOrder() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(orderItems());
        orderRequest.setAddressDto(address());

        OrderNotFound exception = assertThrows(OrderNotFound.class, () ->
                orderService.updateOrder(orderRequest, "testNumber123")
        );
        assertEquals("Can't update order which is not exist for number: testNumber123", exception.getMessage());
    }

    @Test
    void shouldDeleteOrderByOrderNumber() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(orderItems());
        orderRequest.setAddressDto(address());
        OrderSummary save = orderWriteRepository.save(orderMapper.map(orderRequest, State.DRAFT));

        orderService.deleteOrderByOrderNumber(save.getOrderNumber());

        assertEquals(0, orderReadRepository.findAll().size());
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