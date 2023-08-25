package com.serkowski.productservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Document(value = "product")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Product {

    @Id
    private String id;
    private String name;
    private String description;
    private List<String> categories;
    private List<String> tags;
    private BigDecimal price;
    private Map<String, String> specification;
}
