package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.repository.ProductReadRepository;
import com.serkowski.productservice.repository.ProductWriteRepository;
import com.serkowski.productservice.service.api.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    private ProductService productService;
    @Mock
    private ProductReadRepository productReadRepository;
    @Mock
    private ProductWriteRepository productWriteRepository;

    @BeforeEach
    void init() {
        productService = new ProductServiceImpl(productReadRepository, productWriteRepository);
    }

    @Test
    void shouldPlaceProduct() {
        ProductDto productRequest = ProductDto.builder()
                .name("name")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build();
        when(productWriteRepository.save(any())).thenReturn(Product.builder()
                .id(UUID.randomUUID().toString())
                .build());

        productService.placeProduct(productRequest);

        verify(productWriteRepository).save(any(Product.class));
    }

    @Test
    void shouldUpdateProduct() {
        ProductDto productRequest = ProductDto.builder()
                .id(UUID.randomUUID())
                .name("name")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build();
        when(productReadRepository.findById(eq(productRequest.getId().toString()))).thenReturn(Optional.ofNullable(Product.builder()
                .id(UUID.randomUUID().toString())
                .build()));
        when(productWriteRepository.save(any())).thenReturn(Product.builder()
                .id(UUID.randomUUID().toString())
                .build());

        productService.updateProduct(productRequest);

        verify(productWriteRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionDuringUpdateProductBecauseProductWasNotFound() {
        ProductDto productRequest = ProductDto.builder()
                .id(UUID.randomUUID())
                .build();
        when(productReadRepository.findById(eq(productRequest.getId().toString()))).thenReturn(Optional.empty());

        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productService.updateProduct(productRequest)
        );
        assertEquals("Product which id: " + productRequest.getId().toString() + " not exist, so can't be updated", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionDuringUpdateProductBecauseIdWasNotSetInRequest() {
        ProductDto productRequest = ProductDto.builder()
                .build();

        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productService.updateProduct(productRequest)
        );
        assertEquals("Product can't be update because id field is empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionDuringGetProduct() {
        when(productReadRepository.findById(eq("testNumber123"))).thenReturn(Optional.empty());

        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productService.getProductById("testNumber123")
        );
        assertEquals("Product which id: testNumber123 not exist", exception.getMessage());
    }

    @Test
    void shouldGetProductById() {
        String id = UUID.randomUUID().toString();
        when(productReadRepository.findById(eq("testNumber123"))).thenReturn(Optional.ofNullable(Product.builder()
                .id(id)
                .build()));

        ProductDto result = productService.getProductById("testNumber123");

        assertEquals(id, result.getId().toString());
    }

    @Test
    void shouldDeleteProductByOrderNumber() {
        when(productReadRepository.findById(eq("testNumber123"))).thenReturn(Optional.ofNullable(Product.builder()
                .id(UUID.randomUUID().toString())
                .build()));

        productService.deleteProductById("testNumber123");

        verify(productWriteRepository).delete(any());
    }

    @Test
    void shouldThrowExceptionDuringDeleteProduct() {
        when(productReadRepository.findById(eq("testNumber123"))).thenReturn(Optional.empty());

        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productService.deleteProductById("testNumber123")
        );
        assertEquals("Product which id: testNumber123 not exist, so can't be deleted", exception.getMessage());
    }

}