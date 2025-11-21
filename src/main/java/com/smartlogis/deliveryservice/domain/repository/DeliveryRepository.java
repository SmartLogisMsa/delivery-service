package com.smartlogis.deliveryservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.entity.DeliveryStatus;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID>, DeliveryRepositoryCustom {
	Optional<Delivery> findByIdAndDeletedAtIsNull(UUID id);

	Optional<Delivery> findByOrderIdAndDeletedAtIsNull(UUID orderId);

	Page<Delivery> findByStatusAndDeletedAtIsNull(DeliveryStatus status, Pageable pageable);

	Page<Delivery> findByDepartureHubIdAndDeletedAtIsNull(UUID departureHubId, Pageable pageable);

	Page<Delivery> findByDestinationHubIdAndDeletedAtIsNull(UUID destinationHubId, Pageable pageable);

	Page<Delivery> findByCompanyDeliveryManagerIdAndDeletedAtIsNull(UUID companyDeliveryManagerId, Pageable pageable);

	Page<Delivery> findByDeletedAtIsNull(Pageable pageable);
}
