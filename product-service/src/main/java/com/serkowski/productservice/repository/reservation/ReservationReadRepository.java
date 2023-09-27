package com.serkowski.productservice.repository.reservation;

import com.serkowski.productservice.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ReservationReadRepository extends MongoRepository<Reservation, String> {

    Optional<Reservation> findByOrderNumber(String orderNumber);
}
