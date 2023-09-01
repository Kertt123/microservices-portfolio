package com.serkowski.orderservice.controller;

import com.serkowski.orderservice.config.SecurityConfig;
import com.serkowski.orderservice.dto.ErrorHandlerResponse;
import com.serkowski.orderservice.dto.request.AddressRequestDto;
import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.model.error.OrderNotFound;
import com.serkowski.orderservice.service.api.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private OrderService orderService;

    @Test
    void shouldCreateOrder() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(mapOrderItems());
        orderRequest.setAddressDto(mapAddress());

        when(orderService.placeOrderDraft(eq(orderRequest))).thenReturn(Mono.just(OrderResponse.builder().build()));

        webTestClient.post().uri("/api/order/draft")
                .body(BodyInserters.fromValue(orderRequest))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderResponse.class);
    }

    @Test
    void shouldFailDuringOrderCrateBecauseOfMissingItems() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAddressDto(mapAddress());

        webTestClient.post().uri("/api/order/draft")
                .body(BodyInserters.fromValue(orderRequest))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorHandlerResponse.class)
                .consumeWith(response -> assertEquals("Order items can't be empty", response.getResponseBody().getErrors().get(0).getErrorMessage()));
    }

    @Test
    void shouldFailDuringOrderCrateBecauseOfMissingAddress() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(mapOrderItems());

        webTestClient.post().uri("/api/order/draft")
                .body(BodyInserters.fromValue(orderRequest))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorHandlerResponse.class)
                .consumeWith(response -> assertEquals("Address information can't be empty", response.getResponseBody().getErrors().get(0).getErrorMessage()));
    }

    @Test
    void shouldFailDuringOrderCrateBecauseOfMissingInnerFields() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(OrderItemRequestDto.builder().build()));
        orderRequest.setAddressDto(AddressRequestDto.builder().build());

        webTestClient.post().uri("/api/order/draft")
                .body(BodyInserters.fromValue(orderRequest))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorHandlerResponse.class)
                .consumeWith(response -> assertEquals(6, response.getResponseBody().getErrors().size()));
    }

    @Test
    void shouldUpdateOrder() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(mapOrderItems());
        orderRequest.setAddressDto(mapAddress());

        when(orderService.updateOrder(eq(orderRequest), eq("123"))).thenReturn(OrderResponse.builder().build());

        webTestClient.put().uri("/api/order/draft/123")
                .body(BodyInserters.fromValue(orderRequest))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderRequest.class);
    }

    @Test
    void shouldNotUpdateOrderBecauseOfWrongOrderNumber() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(mapOrderItems());
        orderRequest.setAddressDto(mapAddress());

        when(orderService.updateOrder(eq(orderRequest), eq("123"))).thenThrow(OrderNotFound.class);

        webTestClient.put().uri("/api/order/draft/123")
                .body(BodyInserters.fromValue(orderRequest))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorHandlerResponse.class);
    }

    @Test
    void shouldGetOrder() {
        when(orderService.getOrderByOrderNumber(eq("123"))).thenReturn(OrderResponse.builder().build());

        webTestClient.get().uri("/api/order/123")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderResponse.class);
    }

    @Test
    void shouldNotGetOrderBecauseOfWrongOrderNumber() {
        when(orderService.getOrderByOrderNumber(eq("123"))).thenThrow(OrderNotFound.class);

        webTestClient.get().uri("/api/order/123")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorHandlerResponse.class);
    }

    @Test
    void shouldDeleteOrder() {
        webTestClient.delete().uri("/api/order/123")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldNotDeleteOrderBecauseOfWrongOrderNumber() {
        doThrow(OrderNotFound.class).when(orderService).deleteOrderByOrderNumber(eq("123"));

        webTestClient.delete().uri("/api/order/123")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorHandlerResponse.class);
    }

    private List<OrderItemRequestDto> mapOrderItems() {
        return List.of(OrderItemRequestDto.builder()
                .count(2)
                .itemRef("ref1")
                .itemName("name1")
                .build());
    }

    private AddressRequestDto mapAddress() {
        return AddressRequestDto.builder()
                .addressLine1("line1")
                .addressLine2("line2")
                .city("city")
                .country("country")
                .build();

    }
}
