package com.smartlogis.deliveryservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartlogis.common.presentation.dto.PageRequest;
import com.smartlogis.common.presentation.dto.PageResponse;
import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.entity.DeliveryStatus;
import com.smartlogis.deliveryservice.domain.exception.DeliveryMessageCode;
import com.smartlogis.deliveryservice.domain.exception.DeliveryNotFoundException;
import com.smartlogis.deliveryservice.domain.repository.DeliveryRepository;
import com.smartlogis.deliveryservice.interfaces.dto.response.DeliveryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

	private final DeliveryRepository deliveryRepository;

	@Transactional
	public Delivery createDelivery(
		UUID orderId,
		UUID productId,
		UUID departureHubId,
		UUID destinationHubId,
		String address,
		UUID receiptUserId
	) {
		Delivery delivery = Delivery.create(
			orderId,
			productId,
			departureHubId,
			destinationHubId,
			address,
			receiptUserId
		);

		return deliveryRepository.save(delivery);
	}

	@Transactional(readOnly = true)
	public DeliveryResponse getDelivery(UUID deliveryId) {
		Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
			.orElseThrow(() -> new DeliveryNotFoundException(DeliveryMessageCode.DELIVERY_NOT_FOUND));

		return DeliveryResponse.from(delivery);
	}

	@Transactional(readOnly = true)
	public PageResponse<DeliveryResponse> searchDeliveries(
		UUID orderId,
		DeliveryStatus status,
		UUID departureHubId,
		UUID destinationHubId,
		UUID companyDeliveryManagerId,
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

		Page<Delivery> deliveries = deliveryRepository.searchDeliveries(
			orderId, status, departureHubId, destinationHubId, companyDeliveryManagerId, pageable
		);

		return PageResponse.from(deliveries.map(DeliveryResponse::from));
	}

	@Transactional
	public void updateDeliveryStatus(UUID deliveryId, DeliveryStatus status) {
		Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
			.orElseThrow(() -> new DeliveryNotFoundException(DeliveryMessageCode.DELIVERY_NOT_FOUND));

		switch (status) {
			case HUB_MOVING:
				delivery.startHubDelivery();
				break;
			case COMPANY_PENDING:
				delivery.arriveAtDestinationHub();
				break;
			case COMPANY_MOVING:
				delivery.startCompanyDelivery();
				break;
			case DELIVERED:
				delivery.completeDelivery();
				break;
			case CANCELED:
				delivery.cancel();
				break;
			default:
				break;
		}

		deliveryRepository.save(delivery);
	}

	@Transactional
	public void deleteDelivery(UUID deliveryId) {
		Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
			.orElseThrow(() -> new DeliveryNotFoundException(DeliveryMessageCode.DELIVERY_NOT_FOUND));

		delivery.delete();

		deliveryRepository.save(delivery);
	}
}
