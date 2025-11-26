package com.smartlogis.deliveryservice.application.event;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.smartlogis.deliveryservice.application.dto.event.DeliveryRouteEvent;
import com.smartlogis.deliveryservice.application.service.DeliveryHistoryService;
import com.smartlogis.deliveryservice.application.service.DeliveryService;
import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.repository.DeliveryRepository;
import com.smartlogis.deliveryservice.infrastructure.client.ProductServiceClient;
import com.smartlogis.deliveryservice.infrastructure.client.UserServiceClient;
import com.smartlogis.deliveryservice.infrastructure.client.dto.ProductInfoResponse;
import com.smartlogis.deliveryservice.infrastructure.client.dto.UserInfoResponse;
import com.smartlogis.deliveryservice.infrastructure.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeliveryEventListener {

	private final DeliveryService deliveryService;
	private final DeliveryHistoryService deliveryHistoryService;
	private final DeliveryRepository deliveryRepository;
	private final ProductServiceClient productServiceClient;
	private final UserServiceClient userServiceClient;

	@RabbitListener(queues = RabbitMQConfig.DELIVERY_ROUTE_QUEUE)
	@Transactional
	public void handleDeliveryRouteEvent(DeliveryRouteEvent event) {
		ProductInfoResponse product = productServiceClient.getProduct(event.getProductId()).getData();

		UserInfoResponse user = userServiceClient.getUser(event.getReceiptUserId()).getData();

		Delivery delivery = deliveryService.createDelivery(
			event.getOrderId(),
			event.getProductId(),
			product.getProductName(),
			product.getQuantity(),
			event.getDepartureHubId(),
			event.getDepartureHubAddress(),
			event.getDestinationHubId(),
			event.getDestinationHubAddress(),
			event.getReceiptUserId(),
			user.getUsername(),
			user.getEmail(),
			event.getAddress()
		);

		deliveryHistoryService.createDeliveryHistories(delivery, event.getRoutes());

		deliveryRepository.save(delivery);
	}
}
