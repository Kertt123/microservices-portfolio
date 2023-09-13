package com.serkowski.orderservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import static com.serkowski.orderservice.util.ConstantResilience.CIRCUIT_BREAKER_CONFIG_NAME;
import static com.serkowski.orderservice.util.ConstantResilience.RETRY_CONFIG_NAME;
@Configuration
@Slf4j
public class EventsLogConfig {

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public EventsLogConfig(CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_CONFIG_NAME);
        this.retry = retryRegistry.retry(RETRY_CONFIG_NAME);
    }

    @PostConstruct
    public void logRegistryEvents() {
        retry.getEventPublisher()
                .onRetry(event -> log.info("RetryRegistryEventListener: [{}]", event));

        circuitBreaker.getEventPublisher()
                .onCallNotPermitted(event -> log.info("CircuitBreakerRegistryEventListener: State: [{}] Details: [{}]", circuitBreaker.getState(), event))
                .onSuccess(event -> log.info("CircuitBreakerRegistryEventListener: onSuccess: [{}] - State: [{}]", event.getEventType(), circuitBreaker.getState()))
                .onError(event -> log.info("CircuitBreakerRegistryEventListener: onError: [{}]", event))
                .onIgnoredError(event -> log.info("CircuitBreakerRegistryEventListener: onIgnoredError: [{}] - State: [{}]", event.getEventType(), circuitBreaker.getState()))
                .onReset(event -> log.info("CircuitBreakerRegistryEventListener: onReset: [{}] - State: [{}]", event.getEventType(), circuitBreaker.getState()))
                .onStateTransition(event -> log.info("CircuitBreakerRegistryEventListener: onStateTransition: [{}] - State: [{}]", event.getEventType(), circuitBreaker.getState()));
    }
}