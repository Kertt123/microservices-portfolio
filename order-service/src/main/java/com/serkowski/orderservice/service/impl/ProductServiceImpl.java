package com.serkowski.orderservice.service.impl;

import com.serkowski.orderservice.dto.ErrorHandlerResponse;
import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import com.serkowski.orderservice.dto.request.ReserveItemDto;
import com.serkowski.orderservice.dto.request.ReserveItemsDto;
import com.serkowski.orderservice.model.error.ApiCallException;
import com.serkowski.orderservice.service.api.ProductService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.serkowski.orderservice.util.ConstantResilience.CIRCUIT_BREAKER_CONFIG_NAME;
import static com.serkowski.orderservice.util.ConstantResilience.RETRY_CONFIG_NAME;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final WebClient.Builder webClientBuilder;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public ProductServiceImpl(WebClient.Builder webClientBuilder, final CircuitBreakerRegistry circuitBreakerRegistry, final RetryRegistry retryRegistry) {
        this.webClientBuilder = webClientBuilder;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_CONFIG_NAME);
        this.retry = retryRegistry.retry(RETRY_CONFIG_NAME);
    }

    @Override
    public Mono<String> reserveItems(String orderNumber, List<OrderItemRequestDto> orderItems) {
        return webClientBuilder.build().post()
                .uri("http://product-service/api/product/items/reserve")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .body(BodyInserters.fromValue(buildBody(orderNumber, orderItems)))
                .retrieve()
                .onStatus(httpStatusCode -> BAD_REQUEST == httpStatusCode, response -> response.bodyToMono(ErrorHandlerResponse.class)
                        .flatMap(errorResponse -> Mono.error(new ApiCallException(errorResponse.getErrorMessage()))))
                .bodyToMono(String.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker)) // ORDER - If written below, circuit breaker will record a single failure after the max-retry
                .transformDeferred(RetryOperator.of(retry));
    }

    private static ReserveItemsDto buildBody(String orderNumber, List<OrderItemRequestDto> orderItems) {
        return ReserveItemsDto.builder()
                .orderNumber(orderNumber)
                .items(orderItems.stream()
                        .map(item -> ReserveItemDto.builder()
                                .itemRef(item.getItemRef())
                                .count(item.getCount())
                                .build()
                        )
                        .toList())
                .build();
    }
}
