package com.serkowski.productservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto extends RepresentationModel<ProductDto> {

    private UUID id;
    @NotEmpty(message = "Product name can't be empty")
    private String name;
    @NotEmpty(message = "Product description can't be empty")
    private String description;
    @Size(min = 1, message = "Product need to have at least one category")
    @NotEmpty(message = "Product need category list can't be empty")
    private List<String> categories;
    @Size(min = 1, message = "Product need to have at least one tag")
    @NotEmpty(message = "Product tag list can't be empty")
    private List<String> tags;
    @NotNull(message = "Product need to have a price")
    private BigDecimal price;
    @Size(min = 1, message = "Product need to have at least one specification")
    @NotEmpty(message = "Product specification should not be empty")
    private Map<String, String> specification;
}
