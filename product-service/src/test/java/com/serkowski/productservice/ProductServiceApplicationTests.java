package com.serkowski.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serkowski.productservice.dto.ProductDto;
import com.serkowski.productservice.repository.ProductReadRepository;
import com.serkowski.productservice.repository.ProductWriteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

		@Test
		void shouldCreateOrder() throws Exception {
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

			Assertions.assertEquals(1, productReadRepository.findAll().size());
		}
//
//		@Test
//		void shouldFailDuringOrderCrateBecauseOfMissingItems() throws Exception {
//			OrderRequest orderRequest = new OrderRequest();
//			orderRequest.setAddressDto(mapAddress());
//			String productRequestString = objectMapper.writeValueAsString(orderRequest);
//
//			mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
//							.contentType(MediaType.APPLICATION_JSON)
//							.content(productRequestString))
//					.andExpect(status().isBadRequest())
//					.andExpect(content().string(containsString("Order items can't be empty")));
//		}
//
//		@Test
//		void shouldFailDuringOrderCrateBecauseOfMissingAddress() throws Exception {
//			OrderRequest orderRequest = new OrderRequest();
//			orderRequest.setOrderItems(mapOrderItems());
//			String productRequestString = objectMapper.writeValueAsString(orderRequest);
//
//			mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
//							.contentType(MediaType.APPLICATION_JSON)
//							.content(productRequestString))
//					.andExpect(status().isBadRequest())
//					.andExpect(content().string(containsString("Address information can't be empty")));
//		}
//
//		@Test
//		void shouldFailDuringOrderCrateBecauseOfMissingInnerFields() throws Exception {
//			OrderRequest orderRequest = new OrderRequest();
//			orderRequest.setOrderItems(List.of(OrderItemRequestDto.builder().build()));
//			orderRequest.setAddressDto(AddressRequestDto.builder().build());
//			String productRequestString = objectMapper.writeValueAsString(orderRequest);
//
//			mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
//							.contentType(MediaType.APPLICATION_JSON)
//							.content(productRequestString))
//					.andExpect(status().isBadRequest())
//					.andExpect(content().string(containsString("Item reference can't be empty")))
//					.andExpect(content().string(containsString("Item name can't be empty")))
//					.andExpect(content().string(containsString("Item reference can't be empty")))
//					.andExpect(content().string(containsString("The address line of the address can't be empty")))
//					.andExpect(content().string(containsString("The city of the address can't be empty")))
//					.andExpect(content().string(containsString("The country of the address can't be empty")));
//		}
//
//		@Test
//		void shouldUpdateOrder() throws Exception {
//			OrderRequest orderRequest = new OrderRequest();
//			orderRequest.setOrderItems(mapOrderItems());
//			orderRequest.setAddressDto(mapAddress());
//			String productRequestString = objectMapper.writeValueAsString(orderRequest);
//			MvcResult createAction = mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
//							.contentType(MediaType.APPLICATION_JSON)
//							.content(productRequestString))
//					.andReturn();
//			OrderResponse response = objectMapper.readValue(createAction.getResponse().getContentAsString(), OrderResponse.class);
//			orderRequest.getOrderItems().get(0).setItemName("afterUpdateName");
//
//			mockMvc.perform(MockMvcRequestBuilders.put("/api/order/draft/" + response.getOrderNumber())
//							.contentType(MediaType.APPLICATION_JSON)
//							.content(objectMapper.writeValueAsString(orderRequest)))
//					.andExpect(status().isOk())
//					.andExpect(content().string(containsString("afterUpdateName")));
//
//			Assertions.assertEquals(1, orderReadRepository.findAll().size());
//		}
//
//		@Test
//		void shouldNotUpdateOrderBecauseOfWrongOrderNumber() throws Exception {
//			OrderRequest orderRequest = new OrderRequest();
//			orderRequest.setOrderItems(mapOrderItems());
//			orderRequest.setAddressDto(mapAddress());
//
//			mockMvc.perform(MockMvcRequestBuilders.put("/api/order/draft/dummyOrder")
//							.contentType(MediaType.APPLICATION_JSON)
//							.content(objectMapper.writeValueAsString(orderRequest)))
//					.andExpect(status().isNotFound())
//					.andExpect(content().string(containsString("Can't update order which is not exist for number: dummyOrder")));
//
//			Assertions.assertEquals(0, orderReadRepository.findAll().size());
//		}
//
//		@Test
//		void shouldGetOrder() throws Exception {
//			OrderRequest orderRequest = new OrderRequest();
//			orderRequest.setOrderItems(mapOrderItems());
//			orderRequest.setAddressDto(mapAddress());
//			String productRequestString = objectMapper.writeValueAsString(orderRequest);
//			MvcResult createAction = mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
//							.contentType(MediaType.APPLICATION_JSON)
//							.content(productRequestString))
//					.andReturn();
//			OrderResponse response = objectMapper.readValue(createAction.getResponse().getContentAsString(), OrderResponse.class);
//
//			MvcResult getAction = mockMvc.perform(MockMvcRequestBuilders.get("/api/order/" + response.getOrderNumber())
//							.contentType(MediaType.APPLICATION_JSON))
//					.andExpect(status().isOk())
//					.andExpect(jsonPath("$._links").exists())
//					.andReturn();
//			OrderResponse responseGet = objectMapper.readValue(getAction.getResponse().getContentAsString(), OrderResponse.class);
//
//			assertAll(
//					"Map summary to response",
//					() -> assertNotNull(responseGet.getOrderNumber(), "Order number should not be null"),
//					() -> assertEquals("DRAFT", responseGet.getState(), "State should be \"draft\""),
//					() -> assertNotNull(responseGet.getOrderItems().get(0).getId(), "Item should return id"),
//					() -> assertEquals(2, responseGet.getOrderItems().get(0).getCount(), "Count should be 2"),
//					() -> assertEquals("name1", responseGet.getOrderItems().get(0).getItemName(), "Item name should be \"name1\""),
//					() -> assertEquals("ref1", responseGet.getOrderItems().get(0).getItemRef(), "Item reference should be \"ref1\""),
//					() -> assertNotNull(responseGet.getAddress().getId(), "Address should return id"),
//					() -> assertEquals("line1", responseGet.getAddress().getAddressLine1(), "First address line is \"line1\""),
//					() -> assertEquals("line2", responseGet.getAddress().getAddressLine2(), "Second address line is \"line2\""),
//					() -> assertEquals("city", responseGet.getAddress().getCity(), "City is \"city\""),
//					() -> assertEquals("country", responseGet.getAddress().getCountry(), "Country is \"country\"")
//			);
//		}
//
//		@Test
//		void shouldNotGetOrderBecauseOfWrongOrderNumber() throws Exception {
//			mockMvc.perform(MockMvcRequestBuilders.get("/api/order/dummyOrder")
//							.contentType(MediaType.APPLICATION_JSON))
//					.andExpect(status().isNotFound())
//					.andExpect(content().string(containsString("Order which number: dummyOrder not exist")));
//
//			Assertions.assertEquals(0, orderReadRepository.findAll().size());
//		}
//
//		@Test
//		void shouldDeleteOrder() throws Exception {
//			OrderResponse response = createOrderAndReturnResponse();
//
//			mockMvc.perform(MockMvcRequestBuilders.delete("/api/order/" + response.getOrderNumber())
//							.contentType(MediaType.APPLICATION_JSON))
//					.andExpect(status().isOk());
//
//			Assertions.assertEquals(0, orderReadRepository.findAll().size());
//		}
//
//		@Test
//		void shouldNotDeleteOrderBecauseOfWrongOrderNumber() throws Exception {
//			mockMvc.perform(MockMvcRequestBuilders.delete("/api/order/dummyOrder")
//							.contentType(MediaType.APPLICATION_JSON))
//					.andExpect(status().isNotFound())
//					.andExpect(content().string(containsString("Order which number: dummyOrder not exist, so can't be deleted")));
//
//			Assertions.assertEquals(0, orderReadRepository.findAll().size());
//		}
//
//		private OrderResponse createOrderAndReturnResponse() throws Exception {
//			OrderRequest orderRequest = new OrderRequest();
//			orderRequest.setOrderItems(mapOrderItems());
//			orderRequest.setAddressDto(mapAddress());
//			String productRequestString = objectMapper.writeValueAsString(orderRequest);
//			MvcResult createAction = mockMvc.perform(MockMvcRequestBuilders.post("/api/order/draft")
//							.contentType(MediaType.APPLICATION_JSON)
//							.content(productRequestString))
//					.andReturn();
//			return objectMapper.readValue(createAction.getResponse().getContentAsString(), OrderResponse.class);
//		}
//
//		private List<OrderItemRequestDto> mapOrderItems() {
//			return List.of(OrderItemRequestDto.builder()
//					.count(2)
//					.itemRef("ref1")
//					.itemName("name1")
//					.build());
//		}
//
//		private AddressRequestDto mapAddress() {
//			return AddressRequestDto.builder()
//					.addressLine1("line1")
//					.addressLine2("line2")
//					.city("city")
//					.country("country")
//					.build();
//
//		}
//	}
//	}
}