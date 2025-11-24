package com.smartlogis.deliveryservice.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.smartlogis.common.presentation.dto.PageResponse;
import com.smartlogis.deliveryservice.TestMessageResolver;
import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.entity.DeliveryStatus;
import com.smartlogis.deliveryservice.domain.exception.DeliveryNotFoundException;
import com.smartlogis.deliveryservice.domain.repository.DeliveryRepository;
import com.smartlogis.deliveryservice.interfaces.dto.response.DeliveryResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeliveryService 단위 테스트")
class DeliveryServiceTest {

	@InjectMocks
	private DeliveryService deliveryService;

	@Mock
	private DeliveryRepository deliveryRepository;

	@BeforeEach
	void setUp() {
		TestMessageResolver.initializeMessageResource();
	}

	@Test
	@DisplayName("배송 단건 조회 성공")
	void getDelivery_Success() {
		// given
		UUID deliveryId = UUID.randomUUID();
		Delivery delivery = Delivery.create(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			"서울시 강남구",
			UUID.randomUUID()
		);

		given(deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId))
			.willReturn(Optional.of(delivery));

		// when
		DeliveryResponse response = deliveryService.getDelivery(deliveryId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(delivery.getId());
		assertThat(response.getOrderId()).isEqualTo(delivery.getOrderId());
		assertThat(response.getStatus()).isEqualTo(delivery.getStatus());
		assertThat(response.getAddress()).isEqualTo(delivery.getAddress());

		then(deliveryRepository).should(times(1))
			.findByIdAndDeletedAtIsNull(deliveryId);
	}

	@Test
	@DisplayName("배송 단건 조회 실패 - 존재하지 않는 배송")
	void getDelivery_ThrowsException_WhenDeliveryNotFound() {
		// given
		UUID deliveryId = UUID.randomUUID();
		given(deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> deliveryService.getDelivery(deliveryId))
			.isInstanceOf(DeliveryNotFoundException.class);

		then(deliveryRepository).should(times(1))
			.findByIdAndDeletedAtIsNull(deliveryId);
	}

	@Test
	@DisplayName("배송 검색 성공")
	void searchDeliveries_Success() {
		// given
		UUID orderId = UUID.randomUUID();
		DeliveryStatus status = DeliveryStatus.HUB_PENDING;
		UUID departureHubId = UUID.randomUUID();
		UUID destinationHubId = UUID.randomUUID();
		UUID companyDeliveryManagerId = UUID.randomUUID();

		com.smartlogis.common.presentation.dto.PageRequest pageRequest =
			new com.smartlogis.common.presentation.dto.PageRequest(0, 10, "createdAt", "DESC");

		Delivery delivery = Delivery.create(
			orderId,
			departureHubId,
			destinationHubId,
			"서울시 강남구",
			UUID.randomUUID()
		);

		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Delivery> deliveryPage = new PageImpl<>(List.of(delivery), pageable, 1);

		given(deliveryRepository.searchDeliveries(
			eq(orderId), eq(status), eq(departureHubId), eq(destinationHubId), eq(companyDeliveryManagerId), any(Pageable.class)
		)).willReturn(deliveryPage);

		// when
		PageResponse<DeliveryResponse> response = deliveryService.searchDeliveries(
			orderId, status, departureHubId, destinationHubId, companyDeliveryManagerId, pageRequest
		);

		// then
		assertThat(response).isNotNull();
		assertThat(response.content()).hasSize(1);
		assertThat(response.total()).isEqualTo(1);

		then(deliveryRepository).should(times(1))
			.searchDeliveries(
				eq(orderId), eq(status), eq(departureHubId), eq(destinationHubId), eq(companyDeliveryManagerId), any(Pageable.class)
			);
	}

	@Test
	@DisplayName("배송 삭제 성공")
	void deleteDelivery_Success() {
		// given
		UUID deliveryId = UUID.randomUUID();
		Delivery delivery = Delivery.create(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			"서울시 강남구",
			UUID.randomUUID()
		);

		given(deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId))
			.willReturn(Optional.of(delivery));

		// when
		deliveryService.deleteDelivery(deliveryId);

		// then
		then(deliveryRepository).should(times(1))
			.findByIdAndDeletedAtIsNull(deliveryId);
		then(deliveryRepository).should(times(1))
			.save(any(Delivery.class));
	}

	@Test
	@DisplayName("배송 삭제 실패 - 존재하지 않는 배송")
	void deleteDelivery_ThrowsException_WhenDeliveryNotFound() {
		// given
		UUID deliveryId = UUID.randomUUID();
		given(deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> deliveryService.deleteDelivery(deliveryId))
			.isInstanceOf(DeliveryNotFoundException.class);

		then(deliveryRepository).should(times(1))
			.findByIdAndDeletedAtIsNull(deliveryId);
		then(deliveryRepository).should(never())
			.save(any(Delivery.class));
	}
}
