package com.smartlogis.deliveryservice.application.event;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartlogis.deliveryservice.TestMessageResolver;
import com.smartlogis.deliveryservice.application.dto.event.DeliveryRouteEvent;
import com.smartlogis.deliveryservice.application.dto.event.RouteInfo;
import com.smartlogis.deliveryservice.application.service.DeliveryHistoryService;
import com.smartlogis.deliveryservice.application.service.DeliveryService;
import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.repository.DeliveryRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeliveryEventListener 테스트")
class DeliveryEventListenerTest {

	@Mock
	private DeliveryService deliveryService;

	@Mock
	private DeliveryHistoryService deliveryHistoryService;

	@Mock
	private DeliveryRepository deliveryRepository;

	@InjectMocks
	private DeliveryEventListener deliveryEventListener;

	@BeforeEach
	void setUp() {
		TestMessageResolver.initializeMessageResource();
	}

	@Test
	@DisplayName("배송 경로 이벤트 수신 시 배송 및 배송 기록 생성")
	void handleDeliveryRouteEvent_Success() {
		// given
		UUID orderId = UUID.randomUUID();
		UUID departureHubId = UUID.randomUUID();
		UUID destinationHubId = UUID.randomUUID();
		String address = "서울시 강남구";
		UUID receiptUserId = UUID.randomUUID();

		RouteInfo route1 = RouteInfo.builder()
			.sequence(1)
			.departureHubId(departureHubId)
			.destinationHubId(UUID.randomUUID())
			.expectedDistanceKm(new BigDecimal("100.5"))
			.expectedDurationMin(120)
			.build();

		RouteInfo route2 = RouteInfo.builder()
			.sequence(2)
			.departureHubId(UUID.randomUUID())
			.destinationHubId(destinationHubId)
			.expectedDistanceKm(new BigDecimal("50.3"))
			.expectedDurationMin(60)
			.build();

		DeliveryRouteEvent event = DeliveryRouteEvent.builder()
			.orderId(orderId)
			.departureHubId(departureHubId)
			.destinationHubId(destinationHubId)
			.address(address)
			.receiptUserId(receiptUserId)
			.routes(List.of(route1, route2))
			.build();

		Delivery mockDelivery = Delivery.create(
			orderId,
			departureHubId,
			destinationHubId,
			address,
			receiptUserId
		);

		given(deliveryService.createDelivery(
			eq(orderId),
			eq(departureHubId),
			eq(destinationHubId),
			eq(address),
			eq(receiptUserId)
		)).willReturn(mockDelivery);

		willDoNothing().given(deliveryHistoryService)
			.createDeliveryHistories(eq(mockDelivery), eq(event.getRoutes()));

		given(deliveryRepository.save(any(Delivery.class))).willReturn(mockDelivery);

		// when
		deliveryEventListener.handleDeliveryRouteEvent(event);

		// then
		then(deliveryService).should(times(1))
			.createDelivery(orderId, departureHubId, destinationHubId, address, receiptUserId);

		then(deliveryHistoryService).should(times(1))
			.createDeliveryHistories(mockDelivery, event.getRoutes());

		then(deliveryRepository).should(times(1))
			.save(mockDelivery);
	}
}
