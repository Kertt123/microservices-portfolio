package com.serkowski.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.dto.request.ReserveItemsDto;
import com.serkowski.productservice.model.Availability;
import com.serkowski.productservice.model.ProductItem;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.repository.product.item.ProductItemReadRepository;
import com.serkowski.productservice.repository.product.item.ProductItemWriteRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductItemControllerTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductItemReadRepository productItemReadRepository;
    @Autowired
    private ProductWriteRepository productWriteRepository;
    @Autowired
    private ProductItemWriteRepository productItemWriteRepository;

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
        productWriteRepository.deleteAll();
        productItemWriteRepository.deleteAll();
    }

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


    @Test
    void shouldCreateProduct() throws Exception {
        ProductDto productAndReturnResponse = createProductAndReturnResponse();

        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product/" + productAndReturnResponse.getId().toString() + "/add-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links").exists());

        assertEquals(1, productItemReadRepository.findAll().size());
    }

    @Test
    void shouldNotAddProductItemBecauseOfWrongId() throws Exception {
        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber1")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product/dummyId/add-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productItemDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Product which id: dummyId not exist")));

        assertEquals(0, productItemReadRepository.findAll().size());
    }

    @Test
    void shouldGetProduct() throws Exception {
        ProductDto productAndReturnResponse = createProductAndReturnResponse();

        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber1")
                .build();

        ProductItemDto itemDto = addItemAndReturnResponse(productAndReturnResponse, productItemDto);

        MvcResult getProductActionResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/product/item/" + itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productItemDto)))
                .andReturn();

        ProductItemDto getResultDto = objectMapper.readValue(getProductActionResult.getResponse().getContentAsString(), ProductItemDto.class);

        assertAll(
                "Verify product item dto",
                () -> assertNotNull(getResultDto.getId(), "Product item id should not be null"),
                () -> assertEquals("serialNumber1", getResultDto.getSerialNumber(), "Serial number should be \"serialNumber1\""),
                () -> assertEquals("AVAILABLE", getResultDto.getAvailability(), "Availability should be \"AVAILABLE\"")
        );
    }

    @Test
    void shouldNotGetProductItemBecauseOfWrongId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/dummyId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Product which id: dummyId not exist")));

        assertEquals(0, productItemReadRepository.findAll().size());
    }

    @Test
    void shouldReserveProductsByIds() throws Exception {
        ProductDto productAndReturnResponse = createProductAndReturnResponse();

        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber1")
                .build();

        ProductItemDto itemDto = addItemAndReturnResponse(productAndReturnResponse, productItemDto);
        ProductItemDto itemDto2 = addItemAndReturnResponse(productAndReturnResponse, productItemDto);

        ReserveItemsDto reserveItemsDto = ReserveItemsDto.builder()
                .ids(List.of(itemDto.getId().toString(), itemDto2.getId().toString()))
                .build();

        MvcResult getProductActionResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/product/items/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserveItemsDto)))
                .andReturn();

        assertAll(
                "Verify reservation",
                () -> assertEquals("success", getProductActionResult.getResponse().getContentAsString(), "Response should be \"success\""),
                () -> assertTrue(productItemReadRepository.findById(itemDto.getId().toString()).isPresent(), "Product item should exist"),
                () -> assertEquals(Availability.RESERVED, productItemReadRepository.findById(itemDto.getId().toString()).orElse(ProductItem.builder().build()).getAvailability(), "Availability should be \"RESERVED\""),
                () -> assertTrue(productItemReadRepository.findById(itemDto2.getId().toString()).isPresent(), "Product item should exist"),
                () -> assertEquals(Availability.RESERVED, productItemReadRepository.findById(itemDto2.getId().toString()).orElse(ProductItem.builder().build()).getAvailability(), "Availability should be \"RESERVED\"")
        );
    }

    @Test
    void shouldReserveProductsBySerialNumber() throws Exception {
        ProductDto productAndReturnResponse = createProductAndReturnResponse();

        ProductItemDto productItemDto = ProductItemDto.builder()
                .serialNumber("serialNumber1")
                .build();

        ProductItemDto itemDto = addItemAndReturnResponse(productAndReturnResponse, productItemDto);
        ProductItemDto itemDto2 = addItemAndReturnResponse(productAndReturnResponse, productItemDto);

        ReserveItemsDto reserveItemsDto = ReserveItemsDto.builder()
                .serialNumbers(List.of(itemDto.getSerialNumber(), itemDto2.getSerialNumber()))
                .build();

        MvcResult getProductActionResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/product/items/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserveItemsDto)))
                .andReturn();

        assertAll(
                "Verify reservation",
                () -> assertEquals("success", getProductActionResult.getResponse().getContentAsString(), "Response should be \"success\""),
                () -> assertTrue(productItemReadRepository.findById(itemDto.getId().toString()).isPresent(), "Product item should exist"),
                () -> assertEquals(Availability.RESERVED, productItemReadRepository.findById(itemDto.getId().toString()).orElse(ProductItem.builder().build()).getAvailability(), "Availability should be \"RESERVED\""),
                () -> assertTrue(productItemReadRepository.findById(itemDto2.getId().toString()).isPresent(), "Product item should exist"),
                () -> assertEquals(Availability.RESERVED, productItemReadRepository.findById(itemDto2.getId().toString()).orElse(ProductItem.builder().build()).getAvailability(), "Availability should be \"RESERVED\"")
        );
    }

    @Test
    void shouldNotReserveBecauseOfEmptyRequest() throws Exception {
        ReserveItemsDto reserveItemsDto = ReserveItemsDto.builder()
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product/items/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserveItemsDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("To reserve the products the ids or serial numbers need to be provided")));
    }

    private ProductDto createProductAndReturnResponse() throws Exception {
        ProductDto productRequest = ProductDto.builder()
                .name("name")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build();

        String productRequestString = objectMapper.writeValueAsString(productRequest);
        MvcResult createAction = mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andReturn();
        return objectMapper.readValue(createAction.getResponse().getContentAsString(), ProductDto.class);
    }

    private ProductItemDto addItemAndReturnResponse(ProductDto productAndReturnResponse, ProductItemDto productItemDto) throws Exception {
        MvcResult addProductActionResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/product/" + productAndReturnResponse.getId().toString() + "/add-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productItemDto)))
                .andReturn();

        return objectMapper.readValue(addProductActionResult.getResponse().getContentAsString(), ProductItemDto.class);
    }
}