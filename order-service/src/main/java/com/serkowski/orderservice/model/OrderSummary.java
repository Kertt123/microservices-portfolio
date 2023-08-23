package com.serkowski.orderservice.model;

import lombok.*;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderLineItemsList;
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;
    @Enumerated(EnumType.ORDINAL)
    private State state;
}