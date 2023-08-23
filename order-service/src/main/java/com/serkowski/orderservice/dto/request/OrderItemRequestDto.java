package com.serkowski.orderservice.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequestDto {

    @Min(value = 1, message = "Item count need to be postivie number")
    private int count;
    @NotEmpty(message = "Item name can't be empty")
    private String itemName;
}
