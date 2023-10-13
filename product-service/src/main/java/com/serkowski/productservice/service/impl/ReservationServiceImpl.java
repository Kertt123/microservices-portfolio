package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.request.ReservationRequestDto;
import com.serkowski.productservice.model.Reservation;
import com.serkowski.productservice.model.ReservationStatus;
import com.serkowski.productservice.repository.reservation.ReservationReadRepository;
import com.serkowski.productservice.repository.reservation.ReservationWriteRepository;
import com.serkowski.productservice.service.api.ProductItemService;
import com.serkowski.productservice.service.api.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
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
                .date(LocalDateTime.now())
                .reservationStatus(ReservationStatus.ACTIVE)
                .build());
    }

    @Override
    public void unlockReservation(String orderNumber) {
        reservationReadRepository.findByOrderNumber(orderNumber)
                .ifPresent(reservation -> productItemService.unlockReservedItems(reservation.getProductItems()));
    }
}
