package com.serkowski.orderservice.dto.response;


import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDto {

    private Long id;
    private int count;
    private String itemName;
}
