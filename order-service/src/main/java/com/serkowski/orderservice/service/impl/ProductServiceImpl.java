package com.serkowski.orderservice.service.impl;

import com.serkowski.orderservice.dto.request.ReserveItemsDto;
import com.serkowski.orderservice.service.api.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final WebClient.Builder webClientBuilder;
    private final DiscoveryClient discoveryClient;

    @Override
    public Mono<String> reserveItems(List<String> ids) {
        return webClientBuilder.build().post()
                .uri(getProductUri() + "/api/product/items/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(ReserveItemsDto.builder()
                        .ids(ids)
                        .build()))
                .retrieve()
                .bodyToMono(String.class);
    }

    private String getProductUri() {
        return discoveryClient.getInstances("PRODUCT-SERVICE").get(0).getUri().toString();
    }
}
