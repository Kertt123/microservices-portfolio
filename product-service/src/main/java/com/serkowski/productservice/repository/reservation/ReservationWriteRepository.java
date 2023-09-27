package com.serkowski.productservice.repository.reservation;

import com.serkowski.productservice.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservationWriteRepository extends MongoRepository<Reservation, String> {

}
