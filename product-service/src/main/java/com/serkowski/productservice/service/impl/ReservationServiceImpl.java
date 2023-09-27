package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.request.ReservationRequestDto;
import com.serkowski.productservice.model.Reservation;
import com.serkowski.productservice.repository.reservation.ReservationReadRepository;
import com.serkowski.productservice.repository.reservation.ReservationWriteRepository;
import com.serkowski.productservice.service.api.ProductItemService;
import com.serkowski.productservice.service.api.ReservationService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationReadRepository reservationReadRepository;
    private final ReservationWriteRepository reservationWriteRepository;
    private final ProductItemService productItemService;

    @Override
    public void reserve(ReservationRequestDto reservationRequestDto) {
        reservationWriteRepository.save(Reservation.builder()
                .id(UUID.randomUUID().toString())
                .orderNumber(reservationRequestDto.getOrderNumber())
                .productItems(productItemService.reserveItems(reservationRequestDto.getItems()))
                .build());
    }

    @Override
    public void unlockReservation(String orderNumber) {
        reservationReadRepository.findByOrderNumber(orderNumber)
                .ifPresent(reservation -> productItemService.unlockReservedItems(reservation.getProductItems()));
    }
}
