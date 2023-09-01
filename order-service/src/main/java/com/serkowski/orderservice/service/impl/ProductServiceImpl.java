package com.serkowski.orderservice.service.impl;

import com.serkowski.orderservice.dto.ErrorHandlerResponse;
import com.serkowski.orderservice.dto.request.ReserveItemsDto;
import com.serkowski.orderservice.model.error.ApiCallException;
import com.serkowski.orderservice.service.api.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<String> reserveItems(List<String> ids) {
        return webClientBuilder.build().post()
                .uri("http://product-service/api/product/items/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .body(BodyInserters.fromValue(ReserveItemsDto.builder()
                        .ids(ids)
                        .build()))
                .retrieve()
                .onStatus(httpStatusCode -> BAD_REQUEST == httpStatusCode, response -> response.bodyToMono(ErrorHandlerResponse.class)
                        .flatMap(errorResponse -> Mono.error(new ApiCallException(errorResponse.getErrorMessage()))))
                .bodyToMono(String.class);
    }
}
