package com.serkowski.productservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnlockReservationRequestDto {

    @NotEmpty(message = "Order number can't be empty")
    private String orderNumber;
}
