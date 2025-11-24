package com.smartlogis.deliveryservice.application.dto.event;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteInfo {
	private Integer sequence;
	private UUID departureHubId;
	private UUID destinationHubId;
	private BigDecimal expectedDistanceKm;
	private Integer expectedDurationMin;
}
