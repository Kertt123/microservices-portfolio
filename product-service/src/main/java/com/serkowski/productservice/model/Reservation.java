package com.serkowski.productservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(value = "reservation")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Reservation {

    @Id
    private String id;
    private String orderNumber;
    private List<String> productItems;
    private LocalDateTime date;
    private ReservationStatus reservationStatus;
}
