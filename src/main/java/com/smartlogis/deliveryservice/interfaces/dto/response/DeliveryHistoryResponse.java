package com.smartlogis.deliveryservice.interfaces.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.smartlogis.deliveryservice.domain.entity.DeliveryHistory;
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistoryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryHistoryResponse {
	private UUID id;
	private Integer sequence;
	private UUID departureHubId;
	private UUID destinationHubId;
	private BigDecimal expectedDistanceKm;
	private Integer expectedDurationMin;
	private BigDecimal actualDistanceKm;
	private Integer actualDurationMin;
	private DeliveryHistoryStatus status;
	private String hubDeliveryManagerId;
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;

	public static DeliveryHistoryResponse from(DeliveryHistory deliveryHistory) {
		return DeliveryHistoryResponse.builder()
			.id(deliveryHistory.getId())
			.sequence(deliveryHistory.getSequence())
			.departureHubId(deliveryHistory.getDepartureHubId())
			.destinationHubId(deliveryHistory.getDestinationHubId())
			.expectedDistanceKm(deliveryHistory.getExpectedDistanceKm())
			.expectedDurationMin(deliveryHistory.getExpectedDurationMin())
			.actualDistanceKm(deliveryHistory.getActualDistanceKm())
			.actualDurationMin(deliveryHistory.getActualDurationMin())
			.status(deliveryHistory.getStatus())
			.hubDeliveryManagerId(deliveryHistory.getHubDeliveryManagerId())
			.createdAt(deliveryHistory.getCreatedAt())
			.createdBy(deliveryHistory.getCreatedBy())
			.updatedAt(deliveryHistory.getUpdatedAt())
			.updatedBy(deliveryHistory.getUpdatedBy())
			.build();
	}
}
