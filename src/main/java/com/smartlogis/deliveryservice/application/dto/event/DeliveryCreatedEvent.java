package com.smartlogis.deliveryservice.application.dto.event;

import java.time.LocalDateTime;
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
public class DeliveryCreatedEvent {
	private UUID orderId;
	private Orderer orderer;
	private List<Product> products;
	private LocalDateTime orderDate;
	private String orderMemo;
	private String startHub;
	private List<String> stopoverHub;
	private String arrivalHub;
	private String address;
	private LocalDateTime estimateTime;
	private Staff staff;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Orderer {
		private String name;
		private String email;
		private String slackId;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Product {
		private UUID id;
		private String name;
		private Integer quantity;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Staff {
		private String name;
		private String email;
		private String slackId;
	}
}
