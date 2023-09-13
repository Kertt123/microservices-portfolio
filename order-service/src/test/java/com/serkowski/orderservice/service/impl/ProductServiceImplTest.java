package com.serkowski.orderservice.service.impl;

import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import com.serkowski.orderservice.service.api.ProductService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    private ProductService productService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec uriSpec;
    @Mock
    private WebClient.RequestBodyUriSpec headerSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec response;
    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;
    @Mock
    private RetryRegistry retryRegistry;

    @BeforeEach
    void init() {
        productService = new ProductServiceImpl(webClientBuilder, circuitBreakerRegistry, retryRegistry);
    }

    @Test
    public void shouldReserveItems() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq("http://product-service/api/product/items/reserve"))).thenReturn(headerSpec);
        when(headerSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(response);
        when(response.onStatus(any(), any())).thenReturn(response);
        when(response.bodyToMono(eq(String.class))).thenReturn(Mono.just("success"));

        StepVerifier
                .create(productService.reserveItems("orderNumber1", List.of(OrderItemRequestDto.builder()
                        .itemRef("ref1")
                        .count(1)
                        .build())))
                .assertNext(response -> assertEquals("success", response))
                .verifyComplete();
    }

}