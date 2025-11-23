package com.smartlogis.deliveryservice.interfaces.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogis.common.presentation.dto.PageRequest;
import com.smartlogis.common.presentation.dto.PageResponse;
import com.smartlogis.deliveryservice.TestApplication;
import com.smartlogis.deliveryservice.TestMessageResolver;
import com.smartlogis.deliveryservice.application.service.DeliveryService;
import com.smartlogis.deliveryservice.application.service.DeliveryHistoryService;
import com.smartlogis.deliveryservice.domain.entity.DeliveryStatus;
import com.smartlogis.deliveryservice.interfaces.dto.response.DeliveryResponse;

@WebMvcTest(
	controllers = DeliveryController.class,
	properties = {
		"spring.cloud.config.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"eureka.client.enabled=false"
	})
@ContextConfiguration(classes = {TestApplication.class, DeliveryController.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DeliveryController API 테스트")
class DeliveryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private DeliveryService deliveryService;

	@MockitoBean
	private DeliveryHistoryService deliveryHistoryService;

	@BeforeEach
	void setUp() {
		TestMessageResolver.initializeMessageResource();
	}

	@Test
	@DisplayName("배송 단건 조회 성공 시 200 OK 반환")
	void getDelivery_Success() throws Exception {
		// given
		UUID deliveryId = UUID.randomUUID();
		DeliveryResponse response = DeliveryResponse.builder()
			.id(deliveryId)
			.orderId(UUID.randomUUID())
			.status(DeliveryStatus.HUB_PENDING)
			.departureHubId(UUID.randomUUID())
			.destinationHubId(UUID.randomUUID())
			.address("서울시 강남구")
			.receiptUserId(UUID.randomUUID())
			.build();

		given(deliveryService.getDelivery(any(UUID.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(get("/v1/deliveries/{deliveryId}", deliveryId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(deliveryId.toString()))
			.andExpect(jsonPath("$.data.address").value("서울시 강남구"))
			.andExpect(jsonPath("$.data.status").value("HUB_PENDING"));

		then(deliveryService).should(times(1))
			.getDelivery(any(UUID.class));
	}

	@Test
	@DisplayName("배송 검색 성공 시 200 OK 반환")
	void searchDeliveries_Success() throws Exception {
		// given
		DeliveryResponse delivery1 = DeliveryResponse.builder()
			.id(UUID.randomUUID())
			.orderId(UUID.randomUUID())
			.status(DeliveryStatus.HUB_PENDING)
			.departureHubId(UUID.randomUUID())
			.destinationHubId(UUID.randomUUID())
			.address("서울시")
			.receiptUserId(UUID.randomUUID())
			.build();

		DeliveryResponse delivery2 = DeliveryResponse.builder()
			.id(UUID.randomUUID())
			.orderId(UUID.randomUUID())
			.status(DeliveryStatus.HUB_MOVING)
			.departureHubId(UUID.randomUUID())
			.destinationHubId(UUID.randomUUID())
			.address("부산시")
			.receiptUserId(UUID.randomUUID())
			.build();

		PageResponse<DeliveryResponse> pageResponse = new PageResponse<>(
			List.of(delivery1, delivery2),
			0,
			10,
			2L
		);

		given(deliveryService.searchDeliveries(
			any(), any(), any(), any(), any(), any(PageRequest.class)))
			.willReturn(pageResponse);

		// when & then
		mockMvc.perform(get("/v1/deliveries")
				.param("page", "0")
				.param("size", "10")
				.param("sortBy", "createdAt")
				.param("direction", "DESC"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content.length()").value(2))
			.andExpect(jsonPath("$.data.page").value(0))
			.andExpect(jsonPath("$.data.size").value(10))
			.andExpect(jsonPath("$.data.total").value(2));

		then(deliveryService).should(times(1))
			.searchDeliveries(any(), any(), any(), any(), any(), any(PageRequest.class));
	}

	@Test
	@DisplayName("배송 삭제 성공 시 200 OK 반환")
	void deleteDelivery_Success() throws Exception {
		// given
		UUID deliveryId = UUID.randomUUID();

		willDoNothing().given(deliveryService)
			.deleteDelivery(any(UUID.class));

		// when & then
		mockMvc.perform(delete("/v1/deliveries/{deliveryId}", deliveryId))
			.andDo(print())
			.andExpect(status().isOk());

		then(deliveryService).should(times(1))
			.deleteDelivery(any(UUID.class));
	}
}
