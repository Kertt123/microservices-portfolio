package com.serkowski.productservice.repository.product;

import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.model.Product;
import com.serkowski.productservice.model.error.ProductNotFound;
import com.serkowski.productservice.service.api.ProductService;
import com.serkowski.productservice.service.impl.ProductServiceImpl;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataMongoTest
@Testcontainers
class ProductRepositoryTest {

    @Autowired
    ProductReadRepository productReadRepository;

    @Autowired
    ProductWriteRepository productWriteRepository;


    ProductService productService;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

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
        productService = new ProductServiceImpl(productReadRepository, productWriteRepository);
        productWriteRepository.deleteAll();
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

        productService.placeProduct(productRequest);

        assertEquals(1, productReadRepository.findAll().size());
    }

    @Test
    void shouldUpdateProduct() {
        Product save = productWriteRepository.save(Product.builder()
                .id(UUID.randomUUID().toString())
                .name("name1")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build());

        ProductDto productRequest = ProductDto.builder()
                .id(UUID.fromString(save.getId()))
                .name("name")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build();

        productService.updateProduct(productRequest);

        productReadRepository.findById(save.getId()).ifPresent(product -> assertEquals("name", product.getName()));
    }

    @Test
    void shouldThrowExceptionDuringUpdateProductBecauseProductWasNotFound() {
        ProductDto productRequest = ProductDto.builder()
                .id(UUID.randomUUID())
                .build();

        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productService.updateProduct(productRequest)
        );
        assertEquals("Product which id: " + productRequest.getId().toString() + " not exist, so can't be updated", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionDuringGetProduct() {
        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productService.getProductById("testNumber123")
        );
        assertEquals("Product which id: testNumber123 not exist", exception.getMessage());
    }

    @Test
    void shouldDeleteProductById() {
        Product save = productWriteRepository.save(Product.builder()
                .id(UUID.randomUUID().toString())
                .name("name1")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build());

        productService.deleteProductById(save.getId());


        assertEquals(0, productReadRepository.findAll().size());
    }

    @Test
    void shouldThrowExceptionDuringDeleteProduct() {
        ProductNotFound exception = assertThrows(ProductNotFound.class, () ->
                productService.deleteProductById("testNumber123")
        );

        assertEquals("Product which id: testNumber123 not exist, so can't be deleted", exception.getMessage());
    }

}