package com.serkowski.productservice.controller;

import com.serkowski.productservice.dto.request.ReservationRequestDto;
import com.serkowski.productservice.dto.request.UnlockReservationRequestDto;
import com.serkowski.productservice.service.api.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.OK)
    public Mono<String> makeReservation(@Valid @RequestBody ReservationRequestDto reservationRequestDto) {
        reservationService.reserve(reservationRequestDto);
        return Mono.just("success");
    }

    @PostMapping("/unlock")
    @ResponseStatus(HttpStatus.OK)
    public Mono<String> unlockReservation(@Valid @RequestBody UnlockReservationRequestDto unlockReservationRequestDto) {
        reservationService.unlockReservation(unlockReservationRequestDto.getOrderNumber());
        return Mono.just("success");
    }

}