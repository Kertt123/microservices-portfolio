package com.serkowski.productservice.repository.product.item;

import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.dto.request.ReserveItemsDto;
import com.serkowski.productservice.model.Availability;
import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.model.error.AddItemIndexException;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.model.error.ReservationItemsException;
import com.serkowski.productservice.repository.product.ProductReadRepository;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.service.api.ProductItemService;
import com.serkowski.productservice.service.impl.ProductItemServiceImpl;
import org.jetbrains.annotations.NotNull;
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
        Product save = saveProduct();
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
        Product save = saveProduct();
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
        Product save = saveProduct();
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber123")
                .build();
        ProductItemDto productItemDto2 = ProductItemDto.builder()
                .serialNumber("serialNumber123")
                .build();

        productItemService.addItem(save.getId(), productItemDto);

        AddItemIndexException exception = assertThrows(AddItemIndexException.class, () ->
                productItemService.addItem(save.getId(), productItemDto2)
        );
        assertEquals("Product with serial number: serialNumber123 already exist", exception.getMessage());
    }


    @Test
    void shouldThrowExceptionDuringGetProductItemBecauseProductItemWasNotFound() {
        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productItemService.getItemById("123")
        );
        assertEquals("Product item which id: 123 not exist", exception.getMessage());
    }

    @Test
    void shouldReserveProductsByIds() {
        Product save = saveProduct();
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber123")
                .build();
        ProductItemDto productItemDto2 = ProductItemDto.builder()
                .serialNumber("serialNumber12345")
                .build();

        ProductItemDto item1 = productItemService.addItem(save.getId(), productItemDto);
        ProductItemDto item2 = productItemService.addItem(save.getId(), productItemDto2);

        productItemService.reserveItems(ReserveItemsDto.builder()
                .ids(List.of(item1.getId().toString(), item2.getId().toString()))
                .build());

        assertTrue(productItemReadRepository.findByIds(List.of(item1.getId().toString(), item2.getId().toString()))
                .stream()
                .allMatch(productItem -> Availability.RESERVED == productItem.getAvailability()));
    }

    @Test
    void shouldThrowExceptionDuringReserveBecauseOneOfTheProductItemsIsAlreadyReserved() {
        Product save = saveProduct();
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber123")
                .build();
        ProductItemDto productItemDto2 = ProductItemDto.builder()
                .serialNumber("serialNumber12345")
                .build();
        ProductItemDto item1 = productItemService.addItem(save.getId(), productItemDto);
        ProductItemDto item2 = productItemService.addItem(save.getId(), productItemDto2);
        productItemReadRepository.findById(item1.getId().toString()).ifPresent(productItem -> {
            productItem.setAvailability(Availability.RESERVED);
            productItemWriteRepository.save(productItem);
        });

        ReservationItemsException exception = assertThrows(ReservationItemsException.class, () ->
                productItemService.reserveItems(ReserveItemsDto.builder()
                        .ids(List.of(item1.getId().toString(), item2.getId().toString()))
                        .build())
        );

        assertEquals("The product item with serial number: serialNumber123 is already reserved", exception.getMessage());
        productItemReadRepository.findById(item2.getId().toString()).ifPresent(productItem -> assertEquals(Availability.AVAILABLE, productItem.getAvailability()));
    }

    @NotNull
    private Product saveProduct() {
        return productWriteRepository.save(Product.builder()
                .id(UUID.randomUUID().toString())
                .name("name1")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build());
    }
}