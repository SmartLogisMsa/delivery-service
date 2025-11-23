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
	private DeliveryStatus status;
	private UUID departureHubId;
	private UUID destinationHubId;
	private String address;
	private UUID receiptUserId;
	private UUID companyDeliveryManagerId;
	private List<DeliveryHistoryResponse> deliveryHistories;
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;

	public static DeliveryResponse from(Delivery delivery) {
		return DeliveryResponse.builder()
			.id(delivery.getId())
			.orderId(delivery.getOrderId())
			.status(delivery.getStatus())
			.departureHubId(delivery.getDepartureHubId())
			.destinationHubId(delivery.getDestinationHubId())
			.address(delivery.getAddress())
			.receiptUserId(delivery.getReceiptUserId())
			.companyDeliveryManagerId(delivery.getCompanyDeliveryManagerId())
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
