package com.smartlogis.deliveryservice.infrastructure.client.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseWrapper {
	private UUID id;
	private String status;
	private UUID receiptCompanyId;
	private UUID deliveryId;
	private String requestDetails;
	private List<OrderItemResponse> orderItems;
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;
}
