package com.serkowski.productservice.repository.product.item;

import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.model.Availability;
import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.model.error.AddItemIndexException;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.repository.product.ProductReadRepository;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.service.api.ProductItemService;
import com.serkowski.productservice.service.impl.ProductItemServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
class ProductItemRepositoryTest {


    @Autowired
    ProductReadRepository productReadRepository;

    @Autowired
    ProductWriteRepository productWriteRepository;
    @Autowired
    ProductItemReadRepository productItemReadRepository;

    @Autowired
    ProductItemWriteRepository productItemWriteRepository;


    ProductItemService productItemService;

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeAll
    static void beforeAll() {

        mongoDBContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mongoDBContainer.stop();
    }

    @BeforeEach
    void clean() {
        productItemService = new ProductItemServiceImpl(productReadRepository, productWriteRepository, productItemReadRepository, productItemWriteRepository);
        productWriteRepository.deleteAll();
        productItemWriteRepository.deleteAll();
    }

    @Test
    void shouldAddItemToEmpty() {
        Product save = productWriteRepository.save(Product.builder()
                .id(UUID.randomUUID().toString())
                .name("name1")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build());
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber123")
                .build();

        productItemService.addItem(save.getId(), productItemDto);

        productItemReadRepository.findBySerialNumber(productItemDto.getSerialNumber())
                .ifPresent(result -> assertAll(
                        "Assert result",
                        () -> assertNotNull(result.getId(), "Product item id should not be null"),
                        () -> assertEquals("serialNumber123", result.getSerialNumber(), "Serial number should be \"serialNumber123\""),
                        () -> assertEquals(Availability.AVAILABLE, result.getAvailability(), "Availability should be \"AVAILABLE\""),
                        () -> assertNotNull(result.getUpdateDate(), "Update date should not be empty")
                ));

    }

    @Test
    void shouldAddItemToNotEmpty() {
        Product save = productWriteRepository.save(Product.builder()
                .id(UUID.randomUUID().toString())
                .name("name1")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build());
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber123")
                .build();
        ProductItemDto productItemDto2 = ProductItemDto.builder()
                .serialNumber("serialNumber12345")
                .build();

        productItemService.addItem(save.getId(), productItemDto);
        productItemService.addItem(save.getId(), productItemDto2);

        assertEquals(2, productItemReadRepository.findAll().size());
        productReadRepository.findById(save.getId())
                .ifPresent(product -> assertEquals(2, product.getItems().size()));
    }

    @Test
    void shouldThrowExceptionDuringAddItemProductBecauseProductWasNotFound() {
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber")
                .build();

        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productItemService.addItem("123", productItemDto)
        );
        assertEquals("Product which id: 123 not exist", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionDuringAddItemProductBecauseProductWithSuchSerialNumberAlreadyExist() {
        Product save = productWriteRepository.save(Product.builder()
                .id(UUID.randomUUID().toString())
                .name("name1")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build());
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber123")
                .build();
        ProductItemDto productItemDto2 = ProductItemDto.builder()
                .serialNumber("serialNumber123")
                .build();

        productItemService.addItem(save.getId(), productItemDto);
        productItemService.addItem(save.getId(), productItemDto2);

        AddItemIndexException exception = assertThrows(AddItemIndexException.class, () ->
                productItemService.addItem("123", productItemDto)
        );
        assertEquals("Product with serial number: serialNumber123 already exist", exception.getMessage());
    }
//
//    @Test
//    void shouldGetProductItem() {
//        when(productItemReadRepository.findById(eq("123"))).thenReturn(Optional.ofNullable(ProductItem.builder()
//                .id(UUID.randomUUID().toString())
//                .availability(Availability.AVAILABLE)
//                .build()));
//
//        ProductItemDto result = productItemService.getItemById("123");
//
//        assertNotNull(result);
//    }
//
//    @Test
//    void shouldThrowExceptionDuringGetProductItemBecauseProductItemWasNotFound() {
//        when(productItemReadRepository.findById(eq("123"))).thenReturn(Optional.empty());
//
//        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
//                productItemService.getItemById("123")
//        );
//        assertEquals("Product item which id: 123 not exist", exception.getMessage());
//    }
//
//    @Test
//    void shouldReserveProductsByIds() {
//        ProductItem item1 = ProductItem.builder()
//                .id(UUID.randomUUID().toString())
//                .availability(Availability.AVAILABLE)
//                .build();
//        ProductItem item2 = ProductItem.builder()
//                .id(UUID.randomUUID().toString())
//                .availability(Availability.AVAILABLE)
//                .build();
//        when(productItemReadRepository.findByIds(any())).thenReturn(List.of(item1, item2));
//        when(productItemWriteRepository.saveAll(itemsCaptor.capture())).thenReturn(Collections.emptyList());
//
//        productItemService.reserveItems(ReserveItemsDto.builder()
//                .ids(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
//                .build());
//
//        itemsCaptor.getValue()
//                .forEach(item -> assertEquals(Availability.RESERVED, item.getAvailability()));
//    }
//
//    @Test
//    void shouldReserveProductsBySerialNumbers() {
//        ProductItem item1 = ProductItem.builder()
//                .id(UUID.randomUUID().toString())
//                .availability(Availability.AVAILABLE)
//                .build();
//        ProductItem item2 = ProductItem.builder()
//                .id(UUID.randomUUID().toString())
//                .availability(Availability.AVAILABLE)
//                .build();
//        when(productItemReadRepository.findBySerialNumbers(any())).thenReturn(List.of(item1, item2));
//        when(productItemWriteRepository.saveAll(itemsCaptor.capture())).thenReturn(Collections.emptyList());
//
//        productItemService.reserveItems(ReserveItemsDto.builder()
//                .serialNumbers(List.of("serial1", "serial2"))
//                .build());
//
//        itemsCaptor.getValue()
//                .forEach(item -> assertEquals(Availability.RESERVED, item.getAvailability()));
//    }
//
//    @Test
//    void shouldThrowExceptionDuringReserveBecauseRequestIsEmpty() {
//        ReservationItemsException exception = assertThrows(ReservationItemsException.class, () ->
//                productItemService.reserveItems(ReserveItemsDto.builder()
//                        .build())
//        );
//        assertEquals("To reserve the products the ids or serial numbers need to be provided", exception.getMessage());
//    }
//
//    @Test
//    void shouldThrowExceptionDuringReserveBecauseOneOfTheProductItemsIsAlreadyReserved() {
//        ProductItem item1 = ProductItem.builder()
//                .serialNumber("serial1")
//                .availability(Availability.RESERVED)
//                .build();
//        ProductItem item2 = ProductItem.builder()
//                .serialNumber("serial2")
//                .availability(Availability.AVAILABLE)
//                .build();
//        when(productItemReadRepository.findBySerialNumbers(any())).thenReturn(List.of(item1, item2));
//
//        ReservationItemsException exception = assertThrows(ReservationItemsException.class, () ->
//                productItemService.reserveItems(ReserveItemsDto.builder()
//                        .serialNumbers(List.of("serial1", "serial2"))
//                        .build())
//        );
//        assertEquals("The product item with serial number: serial1is already reserved", exception.getMessage());
//    }

}