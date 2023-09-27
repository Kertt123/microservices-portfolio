package com.serkowski.productservice.controller;

import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.service.api.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDto> placeProduct(@Valid @RequestBody ProductDto productRequest) {
        ProductDto response = productService.placeProduct(productRequest);
        response.add(linkTo(ProductController.class).slash(response.getId()).withSelfRel());
        return Mono.just(response);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductDto> updateProduct(@RequestBody ProductDto productRequest) {
        ProductDto response = productService.updateProduct(productRequest);
        response.add(linkTo(ProductController.class).slash(response.getId()).withSelfRel());
        return Mono.just(response);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductDto> getProduct(@PathVariable String productId) {
        ProductDto response = productService.getProductById(productId);
        response.add(linkTo(ProductController.class).slash(response.getId()).withSelfRel());
        return Mono.just(response);
    }


    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct(@PathVariable String productId) {
        productService.deleteProductById(productId);
    }
}