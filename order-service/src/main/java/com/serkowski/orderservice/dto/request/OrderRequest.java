package com.serkowski.orderservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @Valid
    @NotEmpty(message = "Order items can't be empty")
    private List<OrderItemRequestDto> orderItems;
    @Valid
    @NotNull(message = "Address information can't be empty")
    private AddressRequestDto address;
}
