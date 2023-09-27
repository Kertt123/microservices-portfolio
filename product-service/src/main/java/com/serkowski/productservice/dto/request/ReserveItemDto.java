package com.serkowski.productservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveItemDto {

    @NotEmpty(message = "Item reference can't be empty")
    private String itemRef;
    @NotNull(message = "Item count")
    private int count;
}
