package com.smartlogis.deliveryservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartlogis.deliveryservice.domain.entity.DeliveryHistory;
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistoryStatus;

public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, UUID>, DeliveryHistoryRepositoryCustom {
	Optional<DeliveryHistory> findByIdAndDeletedAtIsNull(UUID id);

	List<DeliveryHistory> findByDeliveryIdAndDeletedAtIsNullOrderBySequenceAsc(UUID deliveryId);

	Optional<DeliveryHistory> findByDeliveryIdAndSequenceAndDeletedAtIsNull(UUID deliveryId, Integer sequence);

	List<DeliveryHistory> findByStatusAndDeletedAtIsNull(DeliveryHistoryStatus status);

	List<DeliveryHistory> findByHubDeliveryManagerIdAndDeletedAtIsNull(UUID hubDeliveryManagerId);

	long countByDeliveryIdAndDeletedAtIsNull(UUID deliveryId);
}
