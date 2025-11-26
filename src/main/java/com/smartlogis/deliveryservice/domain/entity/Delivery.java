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
@Table(name = "p_delivery")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Delivery extends AbstractEntity {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	@Column(name = "product_id", nullable = false)
	private UUID productId;

	@Column(name = "product_name", nullable = false, length = 255)
	private String productName;

	@Column(name = "product_quantity", nullable = false)
	private Integer productQuantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private DeliveryStatus status;

	@Column(name = "departure_hub_id", nullable = false)
	private UUID departureHubId;

	@Column(name = "departure_hub_address", nullable = false, length = 255)
	private String departureHubAddress;

	@Column(name = "destination_hub_id", nullable = false)
	private UUID destinationHubId;

	@Column(name = "destination_hub_address", nullable = false, length = 255)
	private String destinationHubAddress;

	@Column(name = "hub_delivery_manager_name", length = 100)
	private String hubDeliveryManagerName;

	@Column(name = "hub_delivery_manager_email", length = 100)
	private String hubDeliveryManagerEmail;

	@Column(name = "hub_delivery_manager_slack_id", length = 100)
	private String hubDeliveryManagerSlackId;

	@Column(name = "company_delivery_manager_name", length = 100)
	private String companyDeliveryManagerName;

	@Column(name = "company_delivery_manager_email", length = 100)
	private String companyDeliveryManagerEmail;

	@Column(name = "company_delivery_manager_slack_id", length = 100)
	private String companyDeliveryManagerSlackId;

	@Column(name = "receipt_user_id", nullable = false)
	private UUID receiptUserId;

	@Column(name = "receipt_user_name", nullable = false, length = 100)
	private String receiptUserName;

	@Column(name = "receipt_user_email", nullable = false, length = 100)
	private String receiptUserEmail;

	@Column(name = "address", nullable = false, length = 255)
	private String address;

	@OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<DeliveryHistory> deliveryHistories = new ArrayList<>();

	@Builder
	public static Delivery create(
		UUID orderId,
		UUID productId,
		String productName,
		Integer productQuantity,
		UUID departureHubId,
		String departureHubAddress,
		UUID destinationHubId,
		String destinationHubAddress,
		UUID receiptUserId,
		String receiptUserName,
		String receiptUserEmail,
		String address
	) {
		Delivery delivery = new Delivery();
		delivery.id = UUID.randomUUID();
		delivery.orderId = orderId;
		delivery.productId = productId;
		delivery.productName = productName;
		delivery.productQuantity = productQuantity;
		delivery.status = DeliveryStatus.HUB_PENDING;
		delivery.departureHubId = departureHubId;
		delivery.departureHubAddress = departureHubAddress;
		delivery.destinationHubId = destinationHubId;
		delivery.destinationHubAddress = destinationHubAddress;
		delivery.receiptUserId = receiptUserId;
		delivery.receiptUserName = receiptUserName;
		delivery.receiptUserEmail = receiptUserEmail;
		delivery.address = address;
		delivery.deliveryHistories = new ArrayList<>();
		return delivery;
	}

	public void addDeliveryHistory(DeliveryHistory deliveryHistory) {
		this.deliveryHistories.add(deliveryHistory);
	}

	public void updateStatus(DeliveryStatus status) {
		this.status = status;
	}

	public void assignHubDeliveryManager(String name, String email, String slackId) {
		this.hubDeliveryManagerName = name;
		this.hubDeliveryManagerEmail = email;
		this.hubDeliveryManagerSlackId = slackId;
	}

	public void assignCompanyDeliveryManager(String name, String email, String slackId) {
		this.companyDeliveryManagerName = name;
		this.companyDeliveryManagerEmail = email;
		this.companyDeliveryManagerSlackId = slackId;
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
