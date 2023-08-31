package com.serkowski.productservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
    @Indexed(unique = true)
    private String serialNumber;
    private LocalDateTime updateDate;
    private LocalDateTime reservationTimeDate;
}
