package com.serkowski.productservice.controller;

import com.serkowski.productservice.dto.ErrorHandlerItem;
import com.serkowski.productservice.dto.ErrorHandlerResponse;
import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.service.api.ProductItemService;
import com.serkowski.productservice.service.api.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductItemController {

    private final ProductItemService productService;

    @PostMapping("/{productId}/add-item")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductItemDto addItem(@PathVariable String productId, @Valid @RequestBody ProductItemDto productRequest) {
        return productService.addItem(productId, productRequest);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorHandlerResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        ex.getBindingResult().getAllErrors().forEach((error) ->
                response.getErrors().add(ErrorHandlerItem.builder()
                        .fieldName(((FieldError) error).getField())
                        .errorMessage(error.getDefaultMessage())
                        .build())
        );
        return response;
    }

    @ExceptionHandler(ProductNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorHandlerResponse handleProductNotFound(ProductNotFound ex) {
        ErrorHandlerResponse response = new ErrorHandlerResponse();
        response.setErrorMessage(ex.getMessage());
        return response;
    }
}