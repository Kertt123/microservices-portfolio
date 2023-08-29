package com.serkowski.productservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(value = "product_item")
public class ProductItem {

    @Id
    private String id;
    private Availability availability;
    private String serialNumber;
    private LocalDateTime updateDate;
    private LocalDateTime reservationTimeDate;
    @DocumentReference(lazy = true, lookup = "{ 'productId' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Product customer;
}
