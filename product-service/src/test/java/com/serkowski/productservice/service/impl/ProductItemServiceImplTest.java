package com.serkowski.productservice.service.impl;

import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.dto.request.ReserveItemDto;
import com.serkowski.productservice.model.Availability;
import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.model.ProductItem;
import com.serkowski.productservice.model.error.AddItemIndexException;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.model.error.ReservationItemsException;
import com.serkowski.productservice.repository.product.item.ProductItemReadRepository;
import com.serkowski.productservice.repository.product.item.ProductItemWriteRepository;
import com.serkowski.productservice.service.api.ProductInnerService;
import com.serkowski.productservice.service.api.ProductItemService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductItemServiceImplTest {

    @Captor
    ArgumentCaptor<List<ProductItem>> itemsCaptor;

    private ProductItemService productItemService;
    @Mock
    private ProductItemReadRepository productItemReadRepository;
    @Mock
    private ProductItemWriteRepository productItemWriteRepository;
    @Mock
    private ProductInnerService productInnerService;

    @BeforeEach
    void init() {
        productItemService = new ProductItemServiceImpl(productInnerService, productItemReadRepository, productItemWriteRepository);
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
        when(productInnerService.findById(eq("123"))).thenReturn(Optional.ofNullable(Product.builder()
                .id(uuid.toString())
                .build()));
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
        when(productInnerService.findById(eq("123"))).thenReturn(Optional.ofNullable(Product.builder()
                .id(uuid.toString())
                .items(getProductItems())
                .build()));
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
        when(productInnerService.findById(eq("123"))).thenReturn(Optional.empty());

        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productItemService.addItem("123", productItemDto)
        );
        assertEquals("Product which id: 123 not exist", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionDuringAddItemProductBecauseProductWithSuchSerialNumberAlreadyExist() {
        UUID uuid = UUID.randomUUID();
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber123")
                .build();

        when(productInnerService.findById(eq("123"))).thenReturn(Optional.ofNullable(Product.builder()
                .id(uuid.toString())
                .build()));
        when(productItemWriteRepository.save(any())).thenThrow(DuplicateKeyException.class);

        AddItemIndexException exception = assertThrows(AddItemIndexException.class, () ->
                productItemService.addItem("123", productItemDto)
        );
        assertEquals("Product with serial number: serialNumber123 already exist", exception.getMessage());
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
        ProductItem item3 = ProductItem.builder()
                .id(UUID.randomUUID().toString())
                .availability(Availability.AVAILABLE)
                .build();
        when(productInnerService.findById(eq("123"))).thenReturn(Optional.ofNullable(Product.builder().items(List.of(item1, item2)).build()));
        when(productInnerService.findById(eq("321"))).thenReturn(Optional.ofNullable(Product.builder().items(List.of(item3)).build()));
        when(productItemWriteRepository.saveAll(itemsCaptor.capture())).thenReturn(Collections.emptyList());

        productItemService.reserveItems(List.of(ReserveItemDto.builder()
                        .itemRef("123")
                        .count(2)
                        .build(),
                ReserveItemDto.builder()
                        .itemRef("321")
                        .count(1)
                        .build()
        ));

        itemsCaptor.getValue()
                .forEach(item -> assertEquals(Availability.RESERVED, item.getAvailability()));
    }

    @Test
    void shouldThrowExceptionDuringReserveBecauseProductWasNotFound() {
        when(productInnerService.findById(eq("123"))).thenReturn(Optional.empty());

        ReservationItemsException exception = assertThrows(ReservationItemsException.class, () ->
                productItemService.reserveItems(List.of(ReserveItemDto.builder()
                        .itemRef("123")
                        .count(2)
                        .build()))
        );
        assertEquals("Reservation list is empty because of product not found or empty items list", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionDuringReserveBecauseOneOfTheProductItemsIsAlreadyReserved() {
        ProductItem item1 = ProductItem.builder()
                .serialNumber("serial1")
                .availability(Availability.RESERVED)
                .build();
        ProductItem item2 = ProductItem.builder()
                .serialNumber("serial2")
                .availability(Availability.AVAILABLE)
                .build();
        when(productInnerService.findById(eq("123"))).thenReturn(Optional.ofNullable(Product.builder().items(List.of(item1, item2)).build()));

        ReservationItemsException exception = assertThrows(ReservationItemsException.class, () ->
                productItemService.reserveItems(List.of(ReserveItemDto.builder()
                        .itemRef("123")
                        .count(2)
                        .build()
                ))
        );
        assertEquals("The amount of the available products is not enough to make a full reservation", exception.getMessage());
    }

    @Test
    public void shouldUnlockItems() {
        List<String> itemsToUnlock = List.of("1111", "22222");
        ProductItem item = ProductItem.builder()
                .availability(Availability.RESERVED)
                .reservationTimeDate(LocalDateTime.now())
                .build();

        when(productItemReadRepository.findByIds(eq(itemsToUnlock)))
                .thenReturn(List.of(item, ProductItem.builder().build()));

        productItemService.unlockReservedItems(itemsToUnlock);

        verify(productItemWriteRepository).saveAll(any());
        assertAll(
                "Assert result",
                () -> assertNull(item.getReservationTimeDate()),
                () -> assertEquals(Availability.AVAILABLE, item.getAvailability())
        );
    }

    @NotNull
    private List<ProductItem> getProductItems() {
        List<ProductItem> items = new ArrayList<>();
        items.add(ProductItem.builder().build());
        return items;
    }

}