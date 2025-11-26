package com.smartlogis.deliveryservice.interfaces.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.entity.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
	private UUID id;
	private UUID orderId;
	private UUID productId;
	private String productName;
	private Integer productQuantity;
	private DeliveryStatus status;
	private UUID departureHubId;
	private String departureHubAddress;
	private UUID destinationHubId;
	private String destinationHubAddress;
	private String hubDeliveryManagerName;
	private String hubDeliveryManagerEmail;
	private String hubDeliveryManagerSlackId;
	private String companyDeliveryManagerName;
	private String companyDeliveryManagerEmail;
	private String companyDeliveryManagerSlackId;
	private UUID receiptUserId;
	private String receiptUserName;
	private String receiptUserEmail;
	private String address;
	private List<DeliveryHistoryResponse> deliveryHistories;
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;

	public static DeliveryResponse from(Delivery delivery) {
		return DeliveryResponse.builder()
			.id(delivery.getId())
			.orderId(delivery.getOrderId())
			.productId(delivery.getProductId())
			.productName(delivery.getProductName())
			.productQuantity(delivery.getProductQuantity())
			.status(delivery.getStatus())
			.departureHubId(delivery.getDepartureHubId())
			.departureHubAddress(delivery.getDepartureHubAddress())
			.destinationHubId(delivery.getDestinationHubId())
			.destinationHubAddress(delivery.getDestinationHubAddress())
			.hubDeliveryManagerName(delivery.getHubDeliveryManagerName())
			.hubDeliveryManagerEmail(delivery.getHubDeliveryManagerEmail())
			.hubDeliveryManagerSlackId(delivery.getHubDeliveryManagerSlackId())
			.companyDeliveryManagerName(delivery.getCompanyDeliveryManagerName())
			.companyDeliveryManagerEmail(delivery.getCompanyDeliveryManagerEmail())
			.companyDeliveryManagerSlackId(delivery.getCompanyDeliveryManagerSlackId())
			.receiptUserId(delivery.getReceiptUserId())
			.receiptUserName(delivery.getReceiptUserName())
			.receiptUserEmail(delivery.getReceiptUserEmail())
			.address(delivery.getAddress())
			.deliveryHistories(
				delivery.getActiveDeliveryHistories().stream()
					.map(DeliveryHistoryResponse::from)
					.collect(Collectors.toList())
			)
			.createdAt(delivery.getCreatedAt())
			.createdBy(delivery.getCreatedBy())
			.updatedAt(delivery.getUpdatedAt())
			.updatedBy(delivery.getUpdatedBy())
			.build();
	}
}
