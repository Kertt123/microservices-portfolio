package com.serkowski.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serkowski.orderservice.dto.request.AddressRequestDto;
import com.serkowski.orderservice.dto.request.OrderItemRequestDto;
import com.serkowski.orderservice.dto.request.OrderRequest;
import com.serkowski.orderservice.dto.response.OrderResponse;
import com.serkowski.orderservice.repository.read.OrderReadRepository;
import com.serkowski.orderservice.repository.write.OrderWriteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("order-service");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderReadRepository orderReadRepository;
    @Autowired
    private OrderWriteRepository orderWriteRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void clean() {
        orderWriteRepository.deleteAll();
    }

    @Test
    void shouldCreateOrder() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(mapOrderItems());
        orderRequest.setAddressDto(mapAddress());
        String productRequestString = objectMapper.writeValueAsString(orderRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, orderReadRepository.findAll().size());
    }

    @Test
    void shouldFailDuringOrderCrateBecauseOfMissingItems() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAddressDto(mapAddress());
        String productRequestString = objectMapper.writeValueAsString(orderRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Order items can't be empty")));
    }

    @Test
    void shouldFailDuringOrderCrateBecauseOfMissingAddress() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(mapOrderItems());
        String productRequestString = objectMapper.writeValueAsString(orderRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Address information can't be empty")));
    }

    @Test
    void shouldFailDuringOrderCrateBecauseOfMissingInnerFields() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(OrderItemRequestDto.builder().build()));
        orderRequest.setAddressDto(AddressRequestDto.builder().build());
        String productRequestString = objectMapper.writeValueAsString(orderRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Item reference can't be empty")))
                .andExpect(content().string(containsString("Item name can't be empty")))
                .andExpect(content().string(containsString("Item reference can't be empty")))
                .andExpect(content().string(containsString("The address line of the address can't be empty")))
                .andExpect(content().string(containsString("The city of the address can't be empty")))
                .andExpect(content().string(containsString("The country of the address can't be empty")));
    }

    @Test
    void shouldUpdateOrder() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(mapOrderItems());
        orderRequest.setAddressDto(mapAddress());
        String productRequestString = objectMapper.writeValueAsString(orderRequest);
        MvcResult createAction = mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andReturn();
        OrderResponse response = objectMapper.readValue(createAction.getResponse().getContentAsString(), OrderResponse.class);
        orderRequest.getOrderItems().get(0).setItemName("afterUpdateName");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/order/draft")
                        .queryParam("orderNumber", response.getOderNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("afterUpdateName")));

        Assertions.assertEquals(1, orderReadRepository.findAll().size());
    }

    @Test
    void shouldNotUpdateOrderBecauseOfWrongOrderNumber() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(mapOrderItems());
        orderRequest.setAddressDto(mapAddress());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/order/draft")
                        .queryParam("orderNumber", "dummyOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Can't update order which is not exist for number: dummyOrder")));

        Assertions.assertEquals(0, orderReadRepository.findAll().size());
    }

    @Test
    void shouldGetOrder() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(mapOrderItems());
        orderRequest.setAddressDto(mapAddress());
        String productRequestString = objectMapper.writeValueAsString(orderRequest);
        MvcResult createAction = mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andReturn();
        OrderResponse response = objectMapper.readValue(createAction.getResponse().getContentAsString(), OrderResponse.class);

        MvcResult getAction = mockMvc.perform(MockMvcRequestBuilders.get("/api/order")
                        .queryParam("orderNumber", response.getOderNumber())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        OrderResponse responseGet = objectMapper.readValue(getAction.getResponse().getContentAsString(), OrderResponse.class);

        assertAll(
                "Map summary to response",
                () -> assertNotNull(responseGet.getOderNumber(), "Order number should not be null"),
                () -> assertEquals("DRAFT", responseGet.getState(), "State should be \"draft\""),
                () -> assertNotNull(responseGet.getOrderItems().get(0).getId(), "Item should return id"),
                () -> assertEquals(2, responseGet.getOrderItems().get(0).getCount(), "Count should be 2"),
                () -> assertEquals("name1", responseGet.getOrderItems().get(0).getItemName(), "Item name should be \"name1\""),
                () -> assertEquals("ref1", responseGet.getOrderItems().get(0).getItemRef(), "Item reference should be \"ref1\""),
                () -> assertNotNull(responseGet.getAddress().getId(), "Address should return id"),
                () -> assertEquals("line1", responseGet.getAddress().getAddressLine1(), "First address line is \"line1\""),
                () -> assertEquals("line2", responseGet.getAddress().getAddressLine2(), "Second address line is \"line2\""),
                () -> assertEquals("city", responseGet.getAddress().getCity(), "City is \"city\""),
                () -> assertEquals("country", responseGet.getAddress().getCountry(), "Country is \"country\"")
        );
    }

    @Test
    void shouldNotGetOrderBecauseOfWrongOrderNumber() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/order")
                        .queryParam("orderNumber", "dummyOrder")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Order which number: dummyOrder not exist")));

        Assertions.assertEquals(0, orderReadRepository.findAll().size());
    }

    @Test
    void shouldDeleteOrder() throws Exception {
        OrderResponse response = createOrderAndReturnResponse();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/order")
                        .queryParam("orderNumber", response.getOderNumber())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(0, orderReadRepository.findAll().size());
    }

    @Test
    void shouldNotDeleteOrderBecauseOfWrongOrderNumber() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/order")
                        .queryParam("orderNumber", "dummyOrder")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Order which number: dummyOrder not exist, so can't be deleted")));

        Assertions.assertEquals(0, orderReadRepository.findAll().size());
    }

    private OrderResponse createOrderAndReturnResponse() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(mapOrderItems());
        orderRequest.setAddressDto(mapAddress());
        String productRequestString = objectMapper.writeValueAsString(orderRequest);
        MvcResult createAction = mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andReturn();
        return objectMapper.readValue(createAction.getResponse().getContentAsString(), OrderResponse.class);
    }

    private List<OrderItemRequestDto> mapOrderItems() {
        return List.of(OrderItemRequestDto.builder()
                .count(2)
                .itemRef("ref1")
                .itemName("name1")
                .build());
    }

    private AddressRequestDto mapAddress() {
        return AddressRequestDto.builder()
                .addressLine1("line1")
                .addressLine2("line2")
                .city("city")
                .country("country")
                .build();

    }

}
