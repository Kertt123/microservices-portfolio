package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.model.Availability;
import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.model.ProductItem;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.repository.product.ProductReadRepository;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.repository.product.item.ProductItemWriteRepository;
import com.serkowski.productservice.service.api.ProductItemService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductItemServiceImplTest {

    private ProductItemService productItemService;
    @Mock
    private ProductReadRepository productReadRepository;
    @Mock
    private ProductWriteRepository productWriteRepository;
    @Mock
    private ProductItemWriteRepository productItemWriteRepository;

    @BeforeEach
    void init() {
        productItemService = new ProductItemServiceImpl(productReadRepository, productWriteRepository, productItemWriteRepository);
    }

    @Test
    void shouldAddItemToEmpty() {
        UUID uuid = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber")
                .build();
        ProductItemDto expectedResult = ProductItemDto.builder()
                .id(uuid)
                .availability("AVAILABLE")
                .updateDate(time)
                .serialNumber("serialNumber")
                .build();
        when(productReadRepository.findById(eq("123"))).thenReturn(Optional.ofNullable(Product.builder()
                .id(uuid.toString())
                .build()));
        when(productWriteRepository.save(any())).thenReturn(Product.builder()
                .id(uuid.toString())
                .build());
        when(productItemWriteRepository.save(any())).thenReturn(ProductItem.builder()
                .id(uuid.toString())
                .availability(Availability.AVAILABLE)
                .updateDate(time)
                .serialNumber("serialNumber")
                .build());

        ProductItemDto result = productItemService.addItem("123", productItemDto);

        assertEquals(expectedResult, result);
    }

    @Test
    void shouldAddItemToNotEmpty() {
        UUID uuid = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber")
                .build();
        ProductItemDto expectedResult = ProductItemDto.builder()
                .id(uuid)
                .availability("AVAILABLE")
                .updateDate(time)
                .serialNumber("serialNumber")
                .build();
        when(productReadRepository.findById(eq("123"))).thenReturn(Optional.ofNullable(Product.builder()
                .id(uuid.toString())
                .items(getProductItems())
                .build()));
        when(productWriteRepository.save(any())).thenReturn(Product.builder()
                .id(uuid.toString())
                .build());
        when(productItemWriteRepository.save(any())).thenReturn(ProductItem.builder()
                .id(uuid.toString())
                .availability(Availability.AVAILABLE)
                .updateDate(time)
                .serialNumber("serialNumber")
                .build());

        ProductItemDto result = productItemService.addItem("123", productItemDto);

        assertEquals(expectedResult, result);
    }

    @Test
    void shouldThrowExceptionDuringAddItemProductBecauseProductWasNotFound() {
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber")
                .build();
        when(productReadRepository.findById(eq("123"))).thenReturn(Optional.empty());

        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productItemService.addItem("123", productItemDto)
        );
        assertEquals("Product which id: 123 not exist", exception.getMessage());
    }

    @NotNull
    private static List<ProductItem> getProductItems() {
        List<ProductItem> items = new ArrayList<>();
        items.add(ProductItem.builder().build());
        return items;
    }

}