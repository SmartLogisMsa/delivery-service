package com.smartlogis.deliveryservice.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.smartlogis.common.domain.AbstractEntity;
import com.smartlogis.deliveryservice.domain.exception.DeliveryMessageCode;
import com.smartlogis.deliveryservice.domain.exception.InvalidDeliveryHistoryStatusException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_delivery_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryHistory extends AbstractEntity {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_id", nullable = false)
	private Delivery delivery;

	@Column(name = "sequence", nullable = false)
	private Integer sequence;

	@Column(name = "departure_hub_id", nullable = false)
	private UUID departureHubId;

	@Column(name = "destination_hub_id", nullable = false)
	private UUID destinationHubId;

	@Column(name = "expected_distance_km", precision = 10, scale = 2, nullable = false)
	private BigDecimal expectedDistanceKm;

	@Column(name = "expected_duration_min", nullable = false)
	private Integer expectedDurationMin;

	@Column(name = "actual_distance_km", precision = 10, scale = 2, nullable = true)
	private BigDecimal actualDistanceKm;

	@Column(name = "actual_duration_min", nullable = true)
	private Integer actualDurationMin;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private DeliveryHistoryStatus status;

	@Column(name = "hub_delivery_manager_id")
	private UUID hubDeliveryManagerId;

	@Builder
	public static DeliveryHistory create(
		Delivery delivery,
		Integer sequence,
		UUID departureHubId,
		UUID destinationHubId,
		BigDecimal expectedDistance,
		Integer expectedDuration
	) {
		DeliveryHistory history = new DeliveryHistory();
		history.id = UUID.randomUUID();
		history.delivery = delivery;
		history.sequence = sequence;
		history.departureHubId = departureHubId;
		history.destinationHubId = destinationHubId;
		history.expectedDistanceKm = expectedDistance;
		history.expectedDurationMin = expectedDuration;
		history.status = DeliveryHistoryStatus.HUB_PENDING;
		return history;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public void assignHubDeliveryManager(UUID hubDeliveryManagerId) {
		this.hubDeliveryManagerId = hubDeliveryManagerId;
	}

	public void startHubDelivery() {
		if (this.status != DeliveryHistoryStatus.HUB_PENDING) {
			throw new InvalidDeliveryHistoryStatusException(DeliveryMessageCode.DELIVERY_HISTORY_INVALID_STATUS);
		}
		this.status = DeliveryHistoryStatus.HUB_MOVING;
	}

	public void arriveAtDestinationHub(BigDecimal actualDistance, Integer actualDuration) {
		if (this.status != DeliveryHistoryStatus.HUB_MOVING) {
			throw new InvalidDeliveryHistoryStatusException(DeliveryMessageCode.DELIVERY_HISTORY_INVALID_STATUS);
		}
		this.status = DeliveryHistoryStatus.DESTINATION_HUB_ARRIVED;
		this.actualDistanceKm = actualDistance;
		this.actualDurationMin = actualDuration;
	}

	public void updateStatus(DeliveryHistoryStatus status) {
		this.status = status;
	}

	public void updateActualDeliveryInfo(BigDecimal actualDistance, Integer actualDuration) {
		this.actualDistanceKm = actualDistance;
		this.actualDurationMin = actualDuration;
	}

	public void startCompanyDelivery() {
		if (this.status != DeliveryHistoryStatus.COMPANY_PENDING) {
			throw new InvalidDeliveryHistoryStatusException(DeliveryMessageCode.DELIVERY_HISTORY_INVALID_STATUS);
		}
		this.status = DeliveryHistoryStatus.COMPANY_MOVING;
	}

	public void arriveAtCompany(BigDecimal actualDistance, Integer actualDuration) {
		if (this.status != DeliveryHistoryStatus.COMPANY_MOVING) {
			throw new InvalidDeliveryHistoryStatusException(DeliveryMessageCode.DELIVERY_HISTORY_INVALID_STATUS);
		}
		this.status = DeliveryHistoryStatus.DESTINATION_COMPANY_ARRIVED;
		this.actualDistanceKm = actualDistance;
		this.actualDurationMin = actualDuration;
	}
}
