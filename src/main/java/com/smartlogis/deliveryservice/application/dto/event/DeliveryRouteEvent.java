package com.smartlogis.deliveryservice.application.dto.event;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRouteEvent {
	private UUID orderId;
	private UUID productId;
	private UUID departureHubId;
	private String departureHubAddress;
	private UUID destinationHubId;
	private String destinationHubAddress;
	private String address;
	private UUID receiptUserId;
	private List<RouteInfo> routes;
}
