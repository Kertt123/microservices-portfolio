package com.serkowski.orderservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDto {

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String country;
}
