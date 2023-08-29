package com.serkowski.productservice.controller;

import com.serkowski.productservice.dto.ErrorHandlerItem;
import com.serkowski.productservice.dto.ErrorHandlerResponse;
import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.dto.request.ReserveItemsDto;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.service.api.ProductItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductItemController {

    private final ProductItemService productItemService;

    @PostMapping("/{productId}/add-item")
    @ResponseStatus(HttpStatus.OK)
    public ProductItemDto addItem(@PathVariable String productId, @Valid @RequestBody ProductItemDto productRequest) {
        ProductItemDto response = productItemService.addItem(productId, productRequest);
        response.add(linkTo(ProductItemController.class).slash(response.getId()).withSelfRel());
        return response;
    }

    @GetMapping("/item/{productItemId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductItemDto getItem(@PathVariable String productItemId) {
        ProductItemDto response = productItemService.getItemById(productItemId);
        response.add(linkTo(ProductItemController.class).slash(response.getId()).withSelfRel());
        return response;
    }

    @PostMapping("/items/reserve")
    @ResponseStatus(HttpStatus.OK)
    public String reserveItems(@RequestBody ReserveItemsDto reserveItemsDto) {
        productItemService.reserveItems(reserveItemsDto);
        return "success";
    }
}