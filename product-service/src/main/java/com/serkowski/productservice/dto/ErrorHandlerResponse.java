package com.serkowski.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorHandlerResponse {

    private String errorMessage = "";
    private List<ErrorHandlerItem> errors = new ArrayList<>();
}
