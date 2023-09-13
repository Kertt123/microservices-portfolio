package com.serkowski.orderservice.config;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static reactor.core.publisher.Mono.just;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(@Value("${api.client.connectTimeout}") int connectTimeout,
                                              @Value("${api.client.readTimeout}") int readTimeout,
                                              @Value("${api.client.writeTimeout}") int writeTimeout) {
        return WebClient.builder()
                .filter(logRequest())
                .filter(logResponseDetails())
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(clientConnectorConfig(connectTimeout, readTimeout, writeTimeout)));
    }


    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Sending [{}] request to URL [{}] with request headers [{}]",
                    clientRequest.method(), clientRequest.url(), clientRequest.headers());
            return just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponseDetails() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse ->
                clientResponse.bodyToMono(String.class).defaultIfEmpty("").flatMap(responseBody -> {
                    final ClientResponse orgClientResponse = clientResponse.mutate().body(responseBody).build();
                    log.info("Received response from API with body [{}] status [{}] with response headers [{}]",
                            responseBody, clientResponse.statusCode(), clientResponse.headers().asHttpHeaders().toSingleValueMap());
                    return just(orgClientResponse);
                })
        );
    }

    private HttpClient clientConnectorConfig(int connectTimeout, int readTimeout, int writeTimeout) {
        return HttpClient.create().option(CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, MILLISECONDS));
                    conn.addHandlerLast(new WriteTimeoutHandler(writeTimeout, MILLISECONDS));
                });
    }
}
