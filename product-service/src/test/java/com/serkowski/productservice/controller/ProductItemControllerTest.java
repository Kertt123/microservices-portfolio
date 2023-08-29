package com.serkowski.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.dto.ProductItemDto;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import com.serkowski.productservice.repository.product.item.ProductItemReadRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(status().isCreated());
//                .andExpect(jsonPath("$._links").exists());

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

}