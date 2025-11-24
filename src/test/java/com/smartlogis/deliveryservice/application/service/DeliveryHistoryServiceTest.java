package com.smartlogis.deliveryservice.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
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
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistory;
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistoryStatus;
import com.smartlogis.deliveryservice.domain.exception.DeliveryHistoryNotFoundException;
import com.smartlogis.deliveryservice.domain.repository.DeliveryHistoryRepository;
import com.smartlogis.deliveryservice.interfaces.dto.response.DeliveryHistoryResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeliveryHistoryService 단위 테스트")
class DeliveryHistoryServiceTest {

	@InjectMocks
	private DeliveryHistoryService deliveryHistoryService;

	@Mock
	private DeliveryHistoryRepository deliveryHistoryRepository;

	@BeforeEach
	void setUp() {
		TestMessageResolver.initializeMessageResource();
	}

	@Test
	@DisplayName("배송 기록 단건 조회 성공")
	void getDeliveryHistory_Success() {
		// given
		UUID deliveryHistoryId = UUID.randomUUID();
		Delivery delivery = Delivery.create(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			"서울시 강남구",
			UUID.randomUUID()
		);

		DeliveryHistory deliveryHistory = DeliveryHistory.create(
			delivery,
			1,
			UUID.randomUUID(),
			UUID.randomUUID(),
			new BigDecimal("100.50"),
			120
		);

		given(deliveryHistoryRepository.findByIdAndDeletedAtIsNull(deliveryHistoryId))
			.willReturn(Optional.of(deliveryHistory));

		// when
		DeliveryHistoryResponse response = deliveryHistoryService.getDeliveryHistory(deliveryHistoryId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(deliveryHistory.getId());
		assertThat(response.getSequence()).isEqualTo(deliveryHistory.getSequence());
		assertThat(response.getStatus()).isEqualTo(deliveryHistory.getStatus());

		then(deliveryHistoryRepository).should(times(1))
			.findByIdAndDeletedAtIsNull(deliveryHistoryId);
	}

	@Test
	@DisplayName("배송 기록 단건 조회 실패 - 존재하지 않는 배송 기록")
	void getDeliveryHistory_ThrowsException_WhenDeliveryHistoryNotFound() {
		// given
		UUID deliveryHistoryId = UUID.randomUUID();
		given(deliveryHistoryRepository.findByIdAndDeletedAtIsNull(deliveryHistoryId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> deliveryHistoryService.getDeliveryHistory(deliveryHistoryId))
			.isInstanceOf(DeliveryHistoryNotFoundException.class);

		then(deliveryHistoryRepository).should(times(1))
			.findByIdAndDeletedAtIsNull(deliveryHistoryId);
	}

	@Test
	@DisplayName("배송 기록 검색 성공")
	void searchDeliveryHistories_Success() {
		// given
		UUID deliveryId = UUID.randomUUID();
		UUID departureHubId = UUID.randomUUID();
		UUID destinationHubId = UUID.randomUUID();
		UUID hubDeliveryManagerId = UUID.randomUUID();
		DeliveryHistoryStatus status = DeliveryHistoryStatus.HUB_PENDING;

		com.smartlogis.common.presentation.dto.PageRequest pageRequest =
			new com.smartlogis.common.presentation.dto.PageRequest(0, 10, "createdAt", "DESC");

		Delivery delivery = Delivery.create(
			UUID.randomUUID(),
			departureHubId,
			destinationHubId,
			"서울시 강남구",
			UUID.randomUUID()
		);

		DeliveryHistory deliveryHistory = DeliveryHistory.create(
			delivery,
			1,
			departureHubId,
			destinationHubId,
			new BigDecimal("100.50"),
			120
		);

		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<DeliveryHistory> deliveryHistoryPage = new PageImpl<>(List.of(deliveryHistory), pageable, 1);

		given(deliveryHistoryRepository.searchDeliveryHistories(
			eq(deliveryId), eq(departureHubId), eq(destinationHubId), eq(hubDeliveryManagerId), eq(status), any(Pageable.class)
		)).willReturn(deliveryHistoryPage);

		// when
		PageResponse<DeliveryHistoryResponse> response = deliveryHistoryService.searchDeliveryHistories(
			deliveryId, departureHubId, destinationHubId, hubDeliveryManagerId, status, pageRequest
		);

		// then
		assertThat(response).isNotNull();
		assertThat(response.content()).hasSize(1);
		assertThat(response.total()).isEqualTo(1);

		then(deliveryHistoryRepository).should(times(1))
			.searchDeliveryHistories(
				eq(deliveryId), eq(departureHubId), eq(destinationHubId), eq(hubDeliveryManagerId), eq(status), any(Pageable.class)
			);
	}

	@Test
	@DisplayName("배송 기록 삭제 성공")
	void deleteDeliveryHistory_Success() {
		// given
		UUID deliveryHistoryId = UUID.randomUUID();
		Delivery delivery = Delivery.create(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			"서울시 강남구",
			UUID.randomUUID()
		);

		DeliveryHistory deliveryHistory = DeliveryHistory.create(
			delivery,
			1,
			UUID.randomUUID(),
			UUID.randomUUID(),
			new BigDecimal("100.50"),
			120
		);

		given(deliveryHistoryRepository.findByIdAndDeletedAtIsNull(deliveryHistoryId))
			.willReturn(Optional.of(deliveryHistory));

		// when
		deliveryHistoryService.deleteDeliveryHistory(deliveryHistoryId);

		// then
		then(deliveryHistoryRepository).should(times(1))
			.findByIdAndDeletedAtIsNull(deliveryHistoryId);
		then(deliveryHistoryRepository).should(times(1))
			.save(any(DeliveryHistory.class));
	}

	@Test
	@DisplayName("배송 기록 삭제 실패 - 존재하지 않는 배송 기록")
	void deleteDeliveryHistory_ThrowsException_WhenDeliveryHistoryNotFound() {
		// given
		UUID deliveryHistoryId = UUID.randomUUID();
		given(deliveryHistoryRepository.findByIdAndDeletedAtIsNull(deliveryHistoryId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> deliveryHistoryService.deleteDeliveryHistory(deliveryHistoryId))
			.isInstanceOf(DeliveryHistoryNotFoundException.class);

		then(deliveryHistoryRepository).should(times(1))
			.findByIdAndDeletedAtIsNull(deliveryHistoryId);
		then(deliveryHistoryRepository).should(never())
			.save(any(DeliveryHistory.class));
	}
}
