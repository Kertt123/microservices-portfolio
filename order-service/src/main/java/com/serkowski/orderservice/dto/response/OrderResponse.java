package com.serkowski.orderservice.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private String oderNumber;
    private String state;
    private List<OrderItemResponseDto> orderItems;
    private AddressResponseDto addressDto;
}
