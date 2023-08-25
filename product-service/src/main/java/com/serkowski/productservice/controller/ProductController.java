package com.serkowski.productservice.controller;

import com.serkowski.productservice.dto.ErrorHandlerItem;
import com.serkowski.productservice.dto.ErrorHandlerResponse;
import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.model.error.ProductNotFound;
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
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto placeProduct(@Valid @RequestBody ProductDto productRequest) {
        ProductDto response = productService.placeProduct(productRequest);
        response.add(linkTo(ProductController.class).slash(response.getId()).withSelfRel());
        return response;
    }

    //    @PutMapping("/draft/{orderNumber}")
//    @ResponseStatus(HttpStatus.OK)
//    public ProductDto updateOrderDraft(@RequestBody ProductD+to orderRequest, @PathVariable String orderNumber) {
//        OrderResponse response = productService.updateOrder(orderRequest, orderNumber);
//        response.add(linkTo(OrderController.class).slash(response.getOrderNumber()).withSelfRel());
//        return response;
//    }
//
    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProduct(@PathVariable String productId) {
        ProductDto response = productService.getProductById(productId);
        response.add(linkTo(ProductController.class).slash(response.getId()).withSelfRel());
        return response;
    }

    //
//    @DeleteMapping("/{orderNumber}")
//    @ResponseStatus(HttpStatus.OK)
//    public void deleteOrder(@PathVariable String orderNumber) {
//        productService.deleteOrderByOrderNumber(orderNumber);
//    }
//
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