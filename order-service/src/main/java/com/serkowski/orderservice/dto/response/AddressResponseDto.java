package com.serkowski.orderservice.dto.response;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDto {

    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String country;
}
