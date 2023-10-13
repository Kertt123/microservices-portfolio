package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.repository.product.ProductReadRepository;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.service.api.ProductInnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductInnerServiceImplTest {

    private ProductInnerService productInnerService;
    @Mock
    private ProductReadRepository productReadRepository;
    @Mock
    private ProductWriteRepository productWriteRepository;

    @BeforeEach
    void init() {
        productInnerService = new ProductInnerServiceImpl(productReadRepository, productWriteRepository);
    }

    @Test
    void shouldSaveProduct() {
        Product product = Product.builder()
                .build();

        productInnerService.saveProduct(product);

        verify(productWriteRepository).save(eq(product));
    }

    @Test
    void shouldReturnProductById() {
        String productId = "123";
        when(productReadRepository.findById(eq(productId))).thenReturn(Optional.of(Product.builder()
                .build()));

        Optional<Product> result = productInnerService.findById(productId);

        assertTrue(result.isPresent());
    }


}