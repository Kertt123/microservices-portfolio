package com.serkowski.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorHandlerResponse {

    private String errorMessage;
    private List<ErrorHandlerItem> errors = new ArrayList<>();
}
