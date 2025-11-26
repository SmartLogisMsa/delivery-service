package com.smartlogis.deliveryservice.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.entity.DeliveryStatus;

public interface DeliveryRepositoryCustom {
	Page<Delivery> searchDeliveries(
		UUID deliveryId,
		UUID orderId,
		DeliveryStatus status,
		UUID departureHubId,
		UUID destinationHubId,
		UUID companyDeliveryManagerId,
		Pageable pageable
	);
}
