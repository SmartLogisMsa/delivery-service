package com.smartlogis.deliveryservice.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
	HUB_PENDING("허브 이동 대기 중"),
	HUB_MOVING("허브 배송 중"),
	COMPANY_PENDING("목적지 허브 도착"),
	COMPANY_MOVING("업체 배송 중"),
	DELIVERED("배송 완료"),
	CANCELED("배송 취소");

	private final String description;
}
