package com.smartlogis.deliveryservice.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.smartlogis.common.domain.AbstractEntity;
import com.smartlogis.deliveryservice.domain.exception.DeliveryCancelFailedException;
import com.smartlogis.deliveryservice.domain.exception.DeliveryMessageCode;
import com.smartlogis.deliveryservice.domain.exception.InvalidDeliveryStatusException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_deliveries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Delivery extends AbstractEntity {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private DeliveryStatus status;

	@Column(name = "departure_hub_id", nullable = false)
	private UUID departureHubId;

	@Column(name = "destination_hub_id", nullable = false)
	private UUID destinationHubId;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "receipt_user_id", nullable = false)
	private UUID receiptUserId;

	@Column(name = "company_delivery_manager_id")
	private UUID companyDeliveryManagerId;

	@OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<DeliveryHistory> deliveryHistories = new ArrayList<>();

	@Builder
	public static Delivery create(
		UUID orderId,
		UUID departureHubId,
		UUID destinationHubId,
		String address,
		UUID receiptUserId
	) {
		Delivery delivery = new Delivery();
		delivery.id = UUID.randomUUID();
		delivery.orderId = orderId;
		delivery.status = DeliveryStatus.HUB_PENDING;
		delivery.departureHubId = departureHubId;
		delivery.destinationHubId = destinationHubId;
		delivery.address = address;
		delivery.receiptUserId = receiptUserId;
		delivery.deliveryHistories = new ArrayList<>();
		return delivery;
	}

	public void addDeliveryHistory(DeliveryHistory deliveryHistory) {
		this.deliveryHistories.add(deliveryHistory);
	}

	public void updateStatus(DeliveryStatus status) {
		this.status = status;
	}

	public void assignCompanyDeliveryManager(UUID companyDeliveryManagerId) {
		this.companyDeliveryManagerId = companyDeliveryManagerId;
	}

	public void startHubDelivery() {
		if (this.status != DeliveryStatus.HUB_PENDING) {
			throw new InvalidDeliveryStatusException(DeliveryMessageCode.DELIVERY_HUB_DELIVERY_NOT_AVAILABLE);
		}
		this.status = DeliveryStatus.HUB_MOVING;
	}

	public void arriveAtDestinationHub() {
		if (this.status != DeliveryStatus.HUB_MOVING) {
			throw new InvalidDeliveryStatusException(DeliveryMessageCode.DELIVERY_INVALID_STATUS_TRANSITION);
		}
		this.status = DeliveryStatus.COMPANY_PENDING;
	}

	public void startCompanyDelivery() {
		if (this.status != DeliveryStatus.COMPANY_PENDING) {
			throw new InvalidDeliveryStatusException(DeliveryMessageCode.DELIVERY_COMPANY_DELIVERY_NOT_AVAILABLE);
		}
		this.status = DeliveryStatus.COMPANY_MOVING;
	}

	public void completeDelivery() {
		if (this.status != DeliveryStatus.COMPANY_MOVING) {
			throw new InvalidDeliveryStatusException(DeliveryMessageCode.DELIVERY_COMPLETION_NOT_AVAILABLE);
		}
		this.status = DeliveryStatus.DELIVERED;
	}

	public void cancel() {
		if (this.status == DeliveryStatus.DELIVERED) {
			throw new DeliveryCancelFailedException(DeliveryMessageCode.DELIVERY_ALREADY_COMPLETED);
		}
		if (this.status == DeliveryStatus.CANCELED) {
			throw new DeliveryCancelFailedException(DeliveryMessageCode.DELIVERY_ALREADY_CANCELED);
		}
		this.status = DeliveryStatus.CANCELED;
	}

	public List<DeliveryHistory> getActiveDeliveryHistories() {
		return deliveryHistories.stream()
			.filter(history -> history.getDeletedAt() == null)
			.toList();
	}
}
