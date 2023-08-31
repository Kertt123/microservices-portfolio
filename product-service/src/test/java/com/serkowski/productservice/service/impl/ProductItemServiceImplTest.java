package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.dto.request.ReserveItemsDto;
import com.serkowski.productservice.model.Availability;
import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.model.ProductItem;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.model.error.ReservationItemsException;
import com.serkowski.productservice.repository.product.ProductReadRepository;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.repository.product.item.ProductItemReadRepository;
import com.serkowski.productservice.repository.product.item.ProductItemWriteRepository;
import com.serkowski.productservice.service.api.ProductItemService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductItemServiceImplTest {

    @Captor
    ArgumentCaptor<List<ProductItem>> itemsCaptor;

    private ProductItemService productItemService;
    @Mock
    private ProductReadRepository productReadRepository;
    @Mock
    private ProductWriteRepository productWriteRepository;
    @Mock
    private ProductItemReadRepository productItemReadRepository;
    @Mock
    private ProductItemWriteRepository productItemWriteRepository;

    @BeforeEach
    void init() {
        productItemService = new ProductItemServiceImpl(productReadRepository, productWriteRepository, productItemReadRepository, productItemWriteRepository);
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

    @Test
    void shouldGetProductItem() {
        when(productItemReadRepository.findById(eq("123"))).thenReturn(Optional.ofNullable(ProductItem.builder()
                .id(UUID.randomUUID().toString())
                .availability(Availability.AVAILABLE)
                .build()));

        ProductItemDto result = productItemService.getItemById("123");

        assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionDuringGetProductItemBecauseProductItemWasNotFound() {
        when(productItemReadRepository.findById(eq("123"))).thenReturn(Optional.empty());

        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productItemService.getItemById("123")
        );
        assertEquals("Product item which id: 123 not exist", exception.getMessage());
    }

    @Test
    void shouldReserveProductsByIds() {
        ProductItem item1 = ProductItem.builder()
                .id(UUID.randomUUID().toString())
                .availability(Availability.AVAILABLE)
                .build();
        ProductItem item2 = ProductItem.builder()
                .id(UUID.randomUUID().toString())
                .availability(Availability.AVAILABLE)
                .build();
        when(productItemReadRepository.findByIds(any())).thenReturn(List.of(item1, item2));
        when(productItemWriteRepository.saveAll(itemsCaptor.capture())).thenReturn(Collections.emptyList());

        productItemService.reserveItems(ReserveItemsDto.builder()
                .ids(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .build());

        itemsCaptor.getValue()
                .forEach(item -> assertEquals(Availability.RESERVED, item.getAvailability()));
    }

    @Test
    void shouldReserveProductsBySerialNumbers() {
        ProductItem item1 = ProductItem.builder()
                .id(UUID.randomUUID().toString())
                .availability(Availability.AVAILABLE)
                .build();
        ProductItem item2 = ProductItem.builder()
                .id(UUID.randomUUID().toString())
                .availability(Availability.AVAILABLE)
                .build();
        when(productItemReadRepository.findBySerialNumbers(any())).thenReturn(List.of(item1, item2));
        when(productItemWriteRepository.saveAll(itemsCaptor.capture())).thenReturn(Collections.emptyList());

        productItemService.reserveItems(ReserveItemsDto.builder()
                .serialNumbers(List.of("serial1", "serial2"))
                .build());

        itemsCaptor.getValue()
                .forEach(item -> assertEquals(Availability.RESERVED, item.getAvailability()));
    }

    @Test
    void shouldThrowExceptionDuringReserveBecauseRequestIsEmpty() {
        ReservationItemsException exception = assertThrows(ReservationItemsException.class, () ->
                productItemService.reserveItems(ReserveItemsDto.builder()
                        .build())
        );
        assertEquals("To reserve the products the ids or serial numbers need to be provided", exception.getMessage());
    }

    @NotNull
    private List<ProductItem> getProductItems() {
        List<ProductItem> items = new ArrayList<>();
        items.add(ProductItem.builder().build());
        return items;
    }

}