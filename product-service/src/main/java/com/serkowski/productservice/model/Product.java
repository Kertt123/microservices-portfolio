package com.serkowski.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Document(value = "product")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Product {

    @Id
    private UUID id;
    private String name;
    private String description;
    private List<String> categories;
    private List<String> tags;
    private BigDecimal price;
    private Map<String, String> specification;
}
