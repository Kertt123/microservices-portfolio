package com.serkowski.productservice.controller;

import com.serkowski.productservice.config.SecurityConfig;
import com.serkowski.productservice.dto.ErrorHandlerResponse;
import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.service.api.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private ProductService productService;

    @Test
    void shouldCreateProduct() {
        ProductDto productRequest = ProductDto.builder()
                .name("name")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build();

        when(productService.placeProduct(eq(productRequest))).thenReturn(ProductDto.builder().build());

        webTestClient.post().uri("/api/product")
                .body(BodyInserters.fromValue(productRequest))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductDto.class);
    }


    @Test
    void shouldFailDuringProductCrateBecauseOfMissingInnerFields() throws Exception {
        ProductDto productRequest = ProductDto.builder()
                .build();

        webTestClient.post().uri("/api/product")
                .body(BodyInserters.fromValue(productRequest))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorHandlerResponse.class)
                .consumeWith(result -> {
                    ErrorHandlerResponse responseBody = result.getResponseBody();
                    assertEquals(6, responseBody.getErrors().size());
                });
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ProductDto response = createProductAndReturnResponse(ProductDto.builder());
        response.setName("nameAfter");

        when(productService.updateProduct(eq(response))).thenReturn(ProductDto.builder().build());

        webTestClient.put().uri("/api/product")
                .body(BodyInserters.fromValue(response))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDto.class);
    }

    @Test
    void shouldNotUpdateProductBecauseOfWrongId() throws Exception {
        ProductDto productRequest = ProductDto.builder()
                .id(UUID.randomUUID())
                .name("name")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build();

        when(productService.updateProduct(eq(productRequest))).thenThrow(ProductNotFound.class);

        webTestClient.put().uri("/api/product")
                .body(BodyInserters.fromValue(productRequest))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorHandlerResponse.class);
    }


    @Test
    void shouldGetProduct() throws Exception {
        ProductDto response = createProductAndReturnResponse(ProductDto.builder()
                .id(UUID.randomUUID()));

        when(productService.getProductById(eq(response.getId().toString()))).thenReturn(ProductDto.builder().build());

        webTestClient.get().uri("/api/product/" + response.getId().toString())
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDto.class);
    }


    @Test
    void shouldNotGetProductBecauseOfId() {
        when(productService.getProductById(eq("dummyProductId"))).thenThrow(ProductNotFound.class);

        webTestClient.get().uri("/api/product/dummyProductId")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorHandlerResponse.class);
    }


    @Test
    void shouldDeleteProduct() {
        ProductDto response = createProductAndReturnResponse(ProductDto.builder());

        webTestClient.delete().uri("/api/product/" + response.getId().toString())
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void shouldNotDeleteProductBecauseOfWrongId() {
        doThrow(ProductNotFound.class).when(productService).deleteProductById(eq("dummyProductId"));

        webTestClient.delete().uri("/api/product/dummyProductId")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorHandlerResponse.class);

    }

    private ProductDto createProductAndReturnResponse(ProductDto.ProductDtoBuilder builder) {
        ProductDto productRequest = builder
                .name("name")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build();

        when(productService.placeProduct(eq(productRequest))).thenReturn(ProductDto.builder().id(UUID.randomUUID()).build());

        return webTestClient.post().uri("/api/product")
                .body(BodyInserters.fromValue(productRequest))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(ProductDto.class)
                .returnResult()
                .getResponseBody();
    }
}