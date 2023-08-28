package com.serkowski.productservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductItemDto {

    @NotEmpty(message = "Product item need to have a serial number")
    private String serialNumber;
}
