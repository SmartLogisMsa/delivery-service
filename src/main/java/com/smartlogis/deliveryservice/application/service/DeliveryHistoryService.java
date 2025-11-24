package com.smartlogis.deliveryservice.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartlogis.common.presentation.dto.PageRequest;
import com.smartlogis.common.presentation.dto.PageResponse;
import com.smartlogis.deliveryservice.application.dto.event.RouteInfo;
import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistory;
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistoryStatus;
import com.smartlogis.deliveryservice.domain.entity.DeliveryStatus;
import com.smartlogis.deliveryservice.domain.exception.DeliveryHistoryNotFoundException;
import com.smartlogis.deliveryservice.domain.exception.DeliveryMessageCode;
import com.smartlogis.deliveryservice.domain.repository.DeliveryHistoryRepository;
import com.smartlogis.deliveryservice.interfaces.dto.response.DeliveryHistoryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryHistoryService {

	private final DeliveryHistoryRepository deliveryHistoryRepository;

	@Transactional
	public void createDeliveryHistories(Delivery delivery, List<RouteInfo> routes) {
		for (RouteInfo route : routes) {
			DeliveryHistory history = DeliveryHistory.create(
				delivery,
				route.getSequence(),
				route.getDepartureHubId(),
				route.getDestinationHubId(),
				route.getExpectedDistanceKm(),
				route.getExpectedDurationMin()
			);

			delivery.addDeliveryHistory(history);
		}
	}

	@Transactional(readOnly = true)
	public DeliveryHistoryResponse getDeliveryHistory(UUID deliveryHistoryId) {
		DeliveryHistory deliveryHistory = deliveryHistoryRepository.findByIdAndDeletedAtIsNull(deliveryHistoryId)
			.orElseThrow(() -> new DeliveryHistoryNotFoundException(DeliveryMessageCode.DELIVERY_HISTORY_NOT_FOUND));

		return DeliveryHistoryResponse.from(deliveryHistory);
	}

	@Transactional(readOnly = true)
	public PageResponse<DeliveryHistoryResponse> searchDeliveryHistories(
		UUID deliveryId,
		UUID departureHubId,
		UUID destinationHubId,
		UUID hubDeliveryManagerId,
		DeliveryHistoryStatus status,
		PageRequest pageRequest
	) {
		Sort.Direction direction = Sort.Direction.fromString(
			pageRequest.getDirection() != null ? pageRequest.getDirection() : "DESC"
		);
		String sortBy = pageRequest.getSortBy() != null ? pageRequest.getSortBy() : "createdAt";

		Pageable pageable = org.springframework.data.domain.PageRequest.of(
			pageRequest.getPage(),
			pageRequest.getSize(),
			Sort.by(direction, sortBy)
		);

		Page<DeliveryHistory> deliveryHistories = deliveryHistoryRepository.searchDeliveryHistories(
			deliveryId, departureHubId, destinationHubId, hubDeliveryManagerId, status, pageable
		);

		return PageResponse.from(deliveryHistories.map(DeliveryHistoryResponse::from));
	}

	@Transactional
	public void deleteDeliveryHistory(UUID deliveryHistoryId) {
		DeliveryHistory deliveryHistory = deliveryHistoryRepository.findByIdAndDeletedAtIsNull(deliveryHistoryId)
			.orElseThrow(() -> new DeliveryHistoryNotFoundException(DeliveryMessageCode.DELIVERY_HISTORY_NOT_FOUND));

		deliveryHistory.delete();

		deliveryHistoryRepository.save(deliveryHistory);
	}

	@Transactional
	public void startHubDelivery(UUID deliveryHistoryId) {
		DeliveryHistory deliveryHistory = deliveryHistoryRepository.findByIdAndDeletedAtIsNull(deliveryHistoryId)
			.orElseThrow(() -> new DeliveryHistoryNotFoundException(DeliveryMessageCode.DELIVERY_HISTORY_NOT_FOUND));

		deliveryHistory.startHubDelivery();
		deliveryHistoryRepository.save(deliveryHistory);

		Delivery delivery = deliveryHistory.getDelivery();
		if (delivery.getStatus() != DeliveryStatus.HUB_MOVING) {
			delivery.startHubDelivery();
		}
	}

	@Transactional
	public void arriveAtDestinationHub(UUID deliveryHistoryId, java.math.BigDecimal actualDistance, Integer actualDuration) {
		DeliveryHistory deliveryHistory = deliveryHistoryRepository.findByIdAndDeletedAtIsNull(deliveryHistoryId)
			.orElseThrow(() -> new DeliveryHistoryNotFoundException(DeliveryMessageCode.DELIVERY_HISTORY_NOT_FOUND));

		deliveryHistory.arriveAtDestinationHub(actualDistance, actualDuration);
		deliveryHistoryRepository.save(deliveryHistory);

		Delivery delivery = deliveryHistory.getDelivery();
		boolean allHubStagesCompleted = delivery.getActiveDeliveryHistories().stream()
			.allMatch(history -> history.getStatus() == DeliveryHistoryStatus.DESTINATION_HUB_ARRIVED);

		if (allHubStagesCompleted && delivery.getStatus() == DeliveryStatus.HUB_MOVING) {
			delivery.arriveAtDestinationHub();
		}
	}

	@Transactional
	public void startCompanyDelivery(UUID deliveryHistoryId) {
		DeliveryHistory deliveryHistory = deliveryHistoryRepository.findByIdAndDeletedAtIsNull(deliveryHistoryId)
			.orElseThrow(() -> new DeliveryHistoryNotFoundException(DeliveryMessageCode.DELIVERY_HISTORY_NOT_FOUND));

		deliveryHistory.startCompanyDelivery();
		deliveryHistoryRepository.save(deliveryHistory);

		Delivery delivery = deliveryHistory.getDelivery();
		if (delivery.getStatus() != DeliveryStatus.COMPANY_MOVING) {
			delivery.startCompanyDelivery();
		}
	}

	@Transactional
	public void completeDelivery(UUID deliveryHistoryId, java.math.BigDecimal actualDistance, Integer actualDuration) {
		DeliveryHistory deliveryHistory = deliveryHistoryRepository.findByIdAndDeletedAtIsNull(deliveryHistoryId)
			.orElseThrow(() -> new DeliveryHistoryNotFoundException(DeliveryMessageCode.DELIVERY_HISTORY_NOT_FOUND));

		deliveryHistory.arriveAtCompany(actualDistance, actualDuration);
		deliveryHistoryRepository.save(deliveryHistory);

		Delivery delivery = deliveryHistory.getDelivery();
		boolean allHistoriesCompleted = delivery.getActiveDeliveryHistories().stream()
			.allMatch(history -> history.getStatus() == DeliveryHistoryStatus.DESTINATION_COMPANY_ARRIVED);

		if (allHistoriesCompleted) {
			delivery.completeDelivery();
		}
	}
}
