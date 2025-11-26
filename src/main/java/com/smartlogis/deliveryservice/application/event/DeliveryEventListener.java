package com.smartlogis.deliveryservice.application.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.smartlogis.deliveryservice.application.dto.event.DeliveryRouteEvent;
import com.smartlogis.deliveryservice.application.service.DeliveryHistoryService;
import com.smartlogis.deliveryservice.application.service.DeliveryService;
import com.smartlogis.deliveryservice.domain.entity.Delivery;
import com.smartlogis.deliveryservice.domain.repository.DeliveryRepository;
import com.smartlogis.deliveryservice.domain.exception.DeliveryMessageCode;
import com.smartlogis.deliveryservice.domain.exception.DeliveryNotFoundException;
import com.smartlogis.deliveryservice.infrastructure.client.OrderServiceClient;
import com.smartlogis.deliveryservice.infrastructure.client.ProductServiceClient;
import com.smartlogis.deliveryservice.infrastructure.client.UserServiceClient;
import com.smartlogis.deliveryservice.infrastructure.client.dto.ProductInfoResponse;
import com.smartlogis.deliveryservice.infrastructure.client.dto.UserInfoResponse;
import com.smartlogis.deliveryservice.infrastructure.config.RabbitMQConfig;

import feign.FeignException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeliveryEventListener {

	private static final Logger logger = LoggerFactory.getLogger(DeliveryEventListener.class);

	private final DeliveryService deliveryService;
	private final DeliveryHistoryService deliveryHistoryService;
	private final DeliveryRepository deliveryRepository;
	private final ProductServiceClient productServiceClient;
	private final UserServiceClient userServiceClient;
	private final OrderServiceClient orderServiceClient;

	@RabbitListener(queues = RabbitMQConfig.DELIVERY_ROUTE_QUEUE)
	@Transactional
	public void handleDeliveryRouteEvent(DeliveryRouteEvent event) {
		logger.info("DeliveryRouteEvent 수신: orderId={}, productId={}", event.getOrderId(), event.getProductId());

		try {
			logger.info("Product Service 호출 시작: productId={}", event.getProductId());
			ProductInfoResponse product = productServiceClient.getProduct(event.getProductId()).getData();
			logger.info("Product Service 호출 성공: productName={}", product.getProductName());

			logger.info("Order Service 호출 시작: orderId={}", event.getOrderId());
			com.smartlogis.deliveryservice.infrastructure.client.dto.OrderResponseWrapper order =
				orderServiceClient.getOrder(event.getOrderId()).getData();
			logger.info("Order Service 호출 성공: orderItems={}", order.getOrderItems().size());

			Integer productQuantity = order.getOrderItems().stream()
				.filter(item -> item.getProductId().equals(event.getProductId()))
				.map(item -> item.getQuantity())
				.findFirst()
				.orElseThrow(() -> new DeliveryNotFoundException(DeliveryMessageCode.DELIVERY_ORDER_ITEM_NOT_FOUND, event.getProductId()));
			logger.info("주문에서 상품 수량 조회 성공: quantity={}", productQuantity);

			logger.info("User Service 호출 시작: userId={}", event.getReceiptUserId());
			UserInfoResponse user = userServiceClient.getUser(event.getReceiptUserId()).getData();
			logger.info("User Service 호출 성공: username={}, email={}", user.getUsername(), user.getEmail());

			Delivery delivery = deliveryService.createDelivery(
				event.getOrderId(),
				event.getProductId(),
				product.getProductName(),
				productQuantity,
				event.getDepartureHubId(),
				event.getDepartureHubAddress(),
				event.getDestinationHubId(),
				event.getDestinationHubAddress(),
				event.getReceiptUserId(),
				user.getUsername(),
				user.getEmail(),
				event.getAddress()
			);

			logger.info("Delivery 생성 성공: deliveryId={}", delivery.getId());

			deliveryRepository.save(delivery);
			logger.info("Delivery 저장 완료: deliveryId={}", delivery.getId());

			deliveryHistoryService.createDeliveryHistories(delivery, event.getRoutes());

			deliveryRepository.save(delivery);
			logger.info("DeliveryHistory 저장 완료: orderId={}", event.getOrderId());

		} catch (FeignException e) {
			logger.error("DeliveryRouteEvent 처리 중 외부 서비스 호출 오류: orderId={}", event.getOrderId(), e);
			throw new DeliveryNotFoundException(DeliveryMessageCode.DELIVERY_EXTERNAL_SERVICE_ERROR, e.getMessage());
		} catch (DeliveryNotFoundException e) {
			logger.error("DeliveryRouteEvent 처리 중 배송 관련 오류: orderId={}", event.getOrderId(), e);
			throw e;
		} catch (Exception e) {
			logger.error("DeliveryRouteEvent 처리 중 예상 외 오류: orderId={}", event.getOrderId(), e);
			throw new DeliveryNotFoundException(DeliveryMessageCode.DELIVERY_EXTERNAL_SERVICE_ERROR, e.getMessage());
		}
	}
}
