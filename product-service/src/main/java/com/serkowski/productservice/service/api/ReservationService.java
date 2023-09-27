package com.serkowski.productservice.service.api;

import com.serkowski.productservice.dto.request.ReservationRequestDto;

public interface ReservationService {

    /**
     * Reserve products.
     *
     * @param reservationRequestDto list of products to reserve
     */
    void reserve(ReservationRequestDto reservationRequestDto);

    /**
     * Unlock reserved products items.
     *
     * @param orderNumber order number
     */
    void unlockReservation(String orderNumber);
}
