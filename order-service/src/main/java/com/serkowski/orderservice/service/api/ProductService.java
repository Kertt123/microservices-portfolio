package com.serkowski.orderservice.service.api;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {

    /**
     * Reserve items.
     *
     * @param itemsIds items ids
     * @return response
     */
    Mono<String> reserveItems(List<String> itemsIds);

}
