package com.serkowski.orderservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDto {

    @NotEmpty(message = "The address line of the address can't be empty")
    private String addressLine1;
    private String addressLine2;
    @NotEmpty(message = "The city of the address can't be empty")
    private String city;
    @NotEmpty(message = "The country of the address can't be empty")
    private String country;
}
