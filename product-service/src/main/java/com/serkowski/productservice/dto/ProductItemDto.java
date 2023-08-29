package com.serkowski.productservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductItemDto extends RepresentationModel<ProductDto> {

    private UUID id;
    @NotEmpty(message = "Product item need to have a serial number")
    private String serialNumber;
    private String availability;
    private LocalDateTime updateDate;
    private LocalDateTime reservationTimeDate;
}
