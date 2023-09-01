package com.serkowski.productservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveItemsDto {

    @NotEmpty(message = "Order number can't be empty")
    private String orderNumber;

    @NotEmpty(message = "Reservation items should not be empty")
    private List<ReserveItem> items;
}
