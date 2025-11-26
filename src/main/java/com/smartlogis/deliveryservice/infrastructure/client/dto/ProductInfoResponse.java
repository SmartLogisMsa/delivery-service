package com.smartlogis.deliveryservice.infrastructure.client.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInfoResponse {
	@JsonProperty("id")
	private UUID productId;

	@JsonProperty("name")
	private String productName;

	@JsonProperty("stock")
	private Integer quantity;
}
