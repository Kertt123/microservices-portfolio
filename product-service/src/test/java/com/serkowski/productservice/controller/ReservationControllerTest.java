package com.serkowski.productservice.controller;

import com.serkowski.productservice.config.SecurityConfig;
import com.serkowski.productservice.dto.ErrorHandlerResponse;
import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.dto.request.ReservationRequestDto;
import com.serkowski.productservice.dto.request.ReserveItemDto;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.model.error.ReservationItemsException;
import com.serkowski.productservice.service.api.ProductService;
import com.serkowski.productservice.service.api.ReservationService;
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
class ReservationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReservationService reservationService;

    @Test
    void shouldReserveProductsByIds() {
        ReservationRequestDto reservationRequestDto = ReservationRequestDto.builder()
                .orderNumber("order1")
                .items(List.of(ReserveItemDto.builder()
                        .itemRef("ref1")
                        .count(2)
                        .build()
                ))
                .build();

        webTestClient.post().uri("/api/reservation/reserve")
                .body(BodyInserters.fromValue(reservationRequestDto))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("success");
    }

    @Test
    void shouldNotReserveBecauseOfIncorrectRequest() {
        ReservationRequestDto reservationRequestDto = ReservationRequestDto.builder()
                .build();
        doThrow(ReservationItemsException.class).when(reservationService).reserve(eq(reservationRequestDto));

        webTestClient.post().uri("/api/reservation/reserve")
                .body(BodyInserters.fromValue(reservationRequestDto))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorHandlerResponse.class)
                .consumeWith(response -> assertEquals(2, response.getResponseBody().getErrors().size()));
    }

    @Test
    void shouldNotReserveBecauseOfException() {
        ReservationRequestDto reservationRequestDto = ReservationRequestDto.builder()
                .orderNumber("order1")
                .items(List.of(ReserveItemDto.builder()
                        .itemRef("ref1")
                        .count(2)
                        .build()
                ))
                .build();
        doThrow(ReservationItemsException.class).when(reservationService).reserve(eq(reservationRequestDto));

        webTestClient.post().uri("/api/reservation/reserve")
                .body(BodyInserters.fromValue(reservationRequestDto))
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorHandlerResponse.class);
    }
}