package com.serkowski.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.repository.product.ProductReadRepository;
import com.serkowski.productservice.repository.product.ProductWriteRepository;
import org.junit.jupiter.api.*;
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
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductReadRepository productReadRepository;
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
        ProductDto productRequest = ProductDto.builder()
                .name("name")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build();
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$._links").exists());

        assertEquals(1, productReadRepository.findAll().size());
    }


    @Test
    void shouldFailDuringProductCrateBecauseOfMissingInnerFields() throws Exception {
        ProductDto productRequest = ProductDto.builder()
                .build();
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Product name can't be empty")))
                .andExpect(content().string(containsString("Product description can't be empty")))
                .andExpect(content().string(containsString("Product need category list can't be empty")))
                .andExpect(content().string(containsString("Product tag list can't be empty")))
                .andExpect(content().string(containsString("Product need to have a price")))
                .andExpect(content().string(containsString("Product specification should not be empty")));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ProductDto response = createProductAndReturnResponse(ProductDto.builder());
        response.setName("nameAfter");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("nameAfter")));

        assertEquals(1, productReadRepository.findAll().size());
    }

    @Test
    void shouldNotUpdateProductBecauseOfWrongId() throws Exception {
        ProductDto productRequest = ProductDto.builder()
                .id(UUID.randomUUID())
                .name("name")
                .price(BigDecimal.ONE)
                .tags(List.of("tag1"))
                .categories(List.of("category1"))
                .description("desc")
                .specification(Map.of("test1", "test2"))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Product which id: " + productRequest.getId().toString() + " not exist, so can't be updated")));

        assertEquals(0, productReadRepository.findAll().size());
    }

    @Test
    void shouldGetProduct() throws Exception {
        ProductDto response = createProductAndReturnResponse(ProductDto.builder()
                .id(UUID.randomUUID()));

        MvcResult getAction = mockMvc.perform(MockMvcRequestBuilders.get("/api/product/" + response.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links").exists())
                .andReturn();
        ProductDto responseGet = objectMapper.readValue(getAction.getResponse().getContentAsString(), ProductDto.class);

        assertAll(
                "Map summary to response",
                () -> assertNotNull(responseGet.getId(), "Product id should not be null"),
                () -> assertEquals("name", responseGet.getName(), "Name should be \"name\""),
                () -> assertEquals(BigDecimal.ONE, responseGet.getPrice(), "Price should be \"1.00\""),
                () -> assertEquals("tag1", responseGet.getTags().get(0), "Tag should be \"tag1\""),
                () -> assertEquals("category1", responseGet.getCategories().get(0), "Category should be \"category1\""),
                () -> assertEquals("desc", responseGet.getDescription(), "Description should be \"desc\""),
                () -> assertTrue(responseGet.getSpecification().containsKey("test1"), "Specification contains key \"test1\""),
                () -> assertEquals("test2", responseGet.getSpecification().get("test1"), "Specification contains key \"test1\" with value \"test2\"")
        );
    }

    @Test
    void shouldNotGetProductBecauseOfId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/dummyProduct")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Product which id: dummyProduct not exist")));

        Assertions.assertEquals(0, productReadRepository.findAll().size());
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        ProductDto response = createProductAndReturnResponse(ProductDto.builder());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product/" + response.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(0, productReadRepository.findAll().size());
    }

    @Test
    void shouldNotDeleteProductBecauseOfWrongId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/product/dummyId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Product which id: dummyId not exist, so can't be deleted")));

        Assertions.assertEquals(0, productReadRepository.findAll().size());
    }

    private ProductDto createProductAndReturnResponse(ProductDto.ProductDtoBuilder builder) throws Exception {
        ProductDto productRequest = builder
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