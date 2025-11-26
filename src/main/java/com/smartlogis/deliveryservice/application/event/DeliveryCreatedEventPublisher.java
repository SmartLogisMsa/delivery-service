package com.smartlogis.deliveryservice.application.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.smartlogis.deliveryservice.application.dto.event.DeliveryCreatedEvent;
import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistory;
import com.smartlogis.deliveryservice.infrastructure.config.RabbitMQConfig;
import com.smartlogis.deliveryservice.infrastructure.client.dto.OrderResponseWrapper;
import com.smartlogis.deliveryservice.infrastructure.client.dto.ProductInfoResponse;
import com.smartlogis.deliveryservice.infrastructure.client.dto.UserInfoResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeliveryCreatedEventPublisher {

	private final RabbitTemplate rabbitTemplate;

	public void publishDeliveryCreatedEvent(
		Delivery delivery,
		OrderResponseWrapper order,
		ProductInfoResponse product,
		UserInfoResponse user
	) {
		DeliveryCreatedEvent event = DeliveryCreatedEvent.builder()
			.orderId(delivery.getOrderId())
			.orderer(DeliveryCreatedEvent.Orderer.builder()
				.name(user.getUsername())
				.email(user.getEmail())
				.slackId(null)
				.build())
			.products(List.of(DeliveryCreatedEvent.Product.builder()
				.id(delivery.getProductId())
				.name(product.getProductName())
				.quantity(delivery.getProductQuantity())
				.build()))
			.orderDate(order.getCreatedAt())
			.orderMemo(order.getRequestDetails())
			.startHub(delivery.getDepartureHubAddress())
			.stopoverHub(extractStopoverHubAddresses(delivery))
			.arrivalHub(delivery.getDestinationHubAddress())
			.address(delivery.getAddress())
			.estimateTime(calculateEstimateTime(delivery))
			.staff(DeliveryCreatedEvent.Staff.builder()
				.name(delivery.getHubDeliveryManagerName())
				.email(delivery.getHubDeliveryManagerEmail())
				.slackId(delivery.getHubDeliveryManagerSlackId())
				.build())
			.build();

		rabbitTemplate.convertAndSend(
			RabbitMQConfig.DELIVERY_CREATED_EXCHANGE,
			RabbitMQConfig.DELIVERY_CREATED_ROUTING_KEY,
			event
		);
	}

	private List<String> extractStopoverHubAddresses(Delivery delivery) {
		List<String> stopoverAddresses = new ArrayList<>();
		List<DeliveryHistory> histories = delivery.getActiveDeliveryHistories();

		if (histories.size() <= 2) {
			return stopoverAddresses;
		}

		for (int i = 1; i < histories.size() - 1; i++) {
			stopoverAddresses.add(histories.get(i).getDestinationHubAddress());
		}

		return stopoverAddresses;
	}

	private LocalDateTime calculateEstimateTime(Delivery delivery) {
		List<DeliveryHistory> histories = delivery.getActiveDeliveryHistories();
		if (histories.isEmpty()) {
			return LocalDateTime.now();
		}

		int totalDurationMin = histories.stream()
			.mapToInt(h -> h.getExpectedDurationMin() != null ? h.getExpectedDurationMin() : 0)
			.sum();

		return LocalDateTime.now().plusMinutes(totalDurationMin);
	}
}
