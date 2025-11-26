package com.smartlogis.deliveryservice.infrastructure.client.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
	private UUID id;
	private UUID productId;
	private Integer quantity;
}
