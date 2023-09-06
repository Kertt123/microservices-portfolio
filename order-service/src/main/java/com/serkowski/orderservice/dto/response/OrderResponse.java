package com.serkowski.orderservice.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse extends RepresentationModel<OrderResponse> {

    private String orderNumber;
    private String state;
    private List<OrderItemResponseDto> orderItems;
    private AddressResponseDto address;
    private Integer version;
}
