package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.request.ReservationRequestDto;
import com.serkowski.productservice.dto.request.ReserveItemDto;
import com.serkowski.productservice.model.Reservation;
import com.serkowski.productservice.repository.reservation.ReservationReadRepository;
import com.serkowski.productservice.repository.reservation.ReservationWriteRepository;
import com.serkowski.productservice.service.api.ProductItemService;
import com.serkowski.productservice.service.api.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Captor
    private ArgumentCaptor<Reservation> reservationArgumentCaptor;

    private ReservationService reservationService;

    @Mock
    private ReservationReadRepository reservationReadRepository;
    @Mock
    private ReservationWriteRepository reservationWriteRepository;
    @Mock
    private ProductItemService productItemService;

    @BeforeEach
    void init() {
        reservationService = new ReservationServiceImpl(reservationReadRepository, reservationWriteRepository, productItemService);
    }

    @Test
    public void shouldMakeReservation() {
        ReservationRequestDto requestDto = ReservationRequestDto.builder()
                .orderNumber("123")
                .items(List.of(ReserveItemDto.builder()
                        .count(2)
                        .itemRef("123123")
                        .build()))
                .build();

        when(productItemService.reserveItems(eq(requestDto.getItems()))).thenReturn(List.of("1111"));

        reservationService.reserve(requestDto);

        verify(reservationWriteRepository).save(reservationArgumentCaptor.capture());
        Reservation value = reservationArgumentCaptor.getValue();
        assertEquals("123", value.getOrderNumber());
        assertAll(
                "Assert result",
                () -> assertNotNull(value.getId()),
                () -> assertEquals("123", value.getOrderNumber()),
                () -> assertEquals("1111", value.getProductItems().get(0))
        );
    }

    @Test
    public void shouldUnlockReservation() {
        String orderNumber = "123";

        when(reservationReadRepository.findByOrderNumber(eq(orderNumber)))
                .thenReturn(Optional.of(Reservation.builder()
                        .id(UUID.randomUUID().toString())
                        .orderNumber(orderNumber)
                        .productItems(List.of("1111"))
                        .build()));

        reservationService.unlockReservation(orderNumber);

        verify(productItemService).unlockReservedItems(any());
    }

}