package com.serkowski.productservice.controller;

import com.serkowski.productservice.config.SecurityConfig;
import com.serkowski.productservice.dto.ErrorHandlerResponse;
import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.model.error.AddItemIndexException;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.service.api.ProductItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductItemController.class)
@Import(SecurityConfig.class)
class ProductItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private ProductItemService productItemService;

    @Test
    void shouldAddItem() {
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber1")
                .build();

        when(productItemService.addItem(eq("123"), eq(productItemDto))).thenReturn(ProductItemDto.builder().build());

        webTestClient.post().uri("/api/product/123/add-item")
                .body(BodyInserters.fromValue(productItemDto))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductItemDto.class);
    }

    @Test
    void shouldNotAddProductItemBecauseOfWrongId() {
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber1")
                .build();
        when(productItemService.addItem(eq("123"), eq(productItemDto))).thenThrow(ProductNotFound.class);

        webTestClient.post().uri("/api/product/123/add-item")
                .body(BodyInserters.fromValue(productItemDto))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorHandlerResponse.class);
    }

    @Test
    void shouldNotAddProductItemBecauseItemWithSuchSerialNumberAlreadyExist() {
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber1")
                .build();
        when(productItemService.addItem(eq("123"), eq(productItemDto))).thenThrow(AddItemIndexException.class);

        webTestClient.post().uri("/api/product/123/add-item")
                .body(BodyInserters.fromValue(productItemDto))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorHandlerResponse.class);
    }

    @Test
    void shouldGetProduct() {
        when(productItemService.getItemById(eq("123"))).thenReturn(ProductItemDto.builder().id(UUID.randomUUID()).build());

        webTestClient.get().uri("/api/product/item/123")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductItemDto.class);
    }

    @Test
    void shouldNotGetProductItemBecauseOfWrongId() {
        when(productItemService.getItemById(eq("123"))).thenThrow(ProductNotFound.class);

        webTestClient.get().uri("/api/product/item/123")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorHandlerResponse.class);
    }


}