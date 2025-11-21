package com.smartlogis.deliveryservice.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smartlogis.deliveryservice.domain.entity.DeliveryHistory;
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistoryStatus;

public interface DeliveryHistoryRepositoryCustom {
	Page<DeliveryHistory> searchDeliveryHistories(
		UUID deliveryId,
		UUID departureHubId,
		UUID destinationHubId,
		UUID hubDeliveryManagerId,
		DeliveryHistoryStatus status,
		Pageable pageable
	);
}
