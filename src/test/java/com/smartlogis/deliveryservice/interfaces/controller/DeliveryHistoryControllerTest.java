package com.smartlogis.deliveryservice.interfaces.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistoryStatus;
import com.smartlogis.deliveryservice.interfaces.dto.response.DeliveryHistoryResponse;

@WebMvcTest(
	controllers = DeliveryHistoryController.class,
	properties = {
		"spring.cloud.config.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"eureka.client.enabled=false"
	})
@ContextConfiguration(classes = {TestApplication.class, DeliveryHistoryController.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DeliveryHistoryController API 테스트")
class DeliveryHistoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private DeliveryHistoryService deliveryHistoryService;

	@MockitoBean
	private DeliveryService deliveryService;

	@BeforeEach
	void setUp() {
		TestMessageResolver.initializeMessageResource();
	}

	@Test
	@DisplayName("배송 기록 단건 조회 성공 시 200 OK 반환")
	void getDeliveryHistory_Success() throws Exception {
		// given
		UUID deliveryHistoryId = UUID.randomUUID();
		DeliveryHistoryResponse response = DeliveryHistoryResponse.builder()
			.id(deliveryHistoryId)
			.sequence(1)
			.departureHubId(UUID.randomUUID())
			.destinationHubId(UUID.randomUUID())
			.expectedDistanceKm(new BigDecimal("100.50"))
			.expectedDurationMin(120)
			.status(DeliveryHistoryStatus.HUB_PENDING)
			.build();

		given(deliveryHistoryService.getDeliveryHistory(any(UUID.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(get("/v1/delivery-histories/{deliveryHistoryId}", deliveryHistoryId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(deliveryHistoryId.toString()))
			.andExpect(jsonPath("$.data.sequence").value(1))
			.andExpect(jsonPath("$.data.status").value("HUB_PENDING"));

		then(deliveryHistoryService).should(times(1))
			.getDeliveryHistory(any(UUID.class));
	}

	@Test
	@DisplayName("배송 기록 검색 성공 시 200 OK 반환")
	void searchDeliveryHistories_Success() throws Exception {
		// given
		DeliveryHistoryResponse history1 = DeliveryHistoryResponse.builder()
			.id(UUID.randomUUID())
			.sequence(1)
			.departureHubId(UUID.randomUUID())
			.destinationHubId(UUID.randomUUID())
			.expectedDistanceKm(new BigDecimal("100.50"))
			.expectedDurationMin(120)
			.status(DeliveryHistoryStatus.HUB_PENDING)
			.build();

		DeliveryHistoryResponse history2 = DeliveryHistoryResponse.builder()
			.id(UUID.randomUUID())
			.sequence(2)
			.departureHubId(UUID.randomUUID())
			.destinationHubId(UUID.randomUUID())
			.expectedDistanceKm(new BigDecimal("50.25"))
			.expectedDurationMin(60)
			.status(DeliveryHistoryStatus.HUB_MOVING)
			.build();

		PageResponse<DeliveryHistoryResponse> pageResponse = new PageResponse<>(
			List.of(history1, history2),
			0,
			10,
			2L
		);

		given(deliveryHistoryService.searchDeliveryHistories(
			any(), any(), any(), any(), any(), any(PageRequest.class)))
			.willReturn(pageResponse);

		// when & then
		mockMvc.perform(get("/v1/delivery-histories")
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

		then(deliveryHistoryService).should(times(1))
			.searchDeliveryHistories(any(), any(), any(), any(), any(), any(PageRequest.class));
	}

	@Test
	@DisplayName("배송 기록 삭제 성공 시 200 OK 반환")
	void deleteDeliveryHistory_Success() throws Exception {
		// given
		UUID deliveryHistoryId = UUID.randomUUID();

		willDoNothing().given(deliveryHistoryService)
			.deleteDeliveryHistory(any(UUID.class));

		// when & then
		mockMvc.perform(delete("/v1/delivery-histories/{deliveryHistoryId}", deliveryHistoryId))
			.andDo(print())
			.andExpect(status().isOk());

		then(deliveryHistoryService).should(times(1))
			.deleteDeliveryHistory(any(UUID.class));
	}
}
