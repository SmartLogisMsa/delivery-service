package com.smartlogis.deliveryservice.application.event;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.smartlogis.deliveryservice.application.dto.event.DeliveryRouteEvent;
import com.smartlogis.deliveryservice.application.service.DeliveryHistoryService;
import com.smartlogis.deliveryservice.application.service.DeliveryService;
import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.repository.DeliveryRepository;
import com.smartlogis.deliveryservice.infrastructure.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventListener {

	private final DeliveryService deliveryService;
	private final DeliveryHistoryService deliveryHistoryService;
	private final DeliveryRepository deliveryRepository;

	@RabbitListener(queues = RabbitMQConfig.DELIVERY_ROUTE_QUEUE)
	@Transactional
	public void handleDeliveryRouteEvent(DeliveryRouteEvent event) {
		Delivery delivery = deliveryService.createDelivery(
			event.getOrderId(),
			event.getDepartureHubId(),
			event.getDestinationHubId(),
			event.getAddress(),
			event.getReceiptUserId()
		);

		deliveryHistoryService.createDeliveryHistories(delivery, event.getRoutes());

		deliveryRepository.save(delivery);
	}
}
