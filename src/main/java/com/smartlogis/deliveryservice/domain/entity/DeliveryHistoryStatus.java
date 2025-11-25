package com.smartlogis.deliveryservice.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryHistoryStatus {
	HUB_PENDING("허브 이동 대기 중"),
	HUB_MOVING("허브 배송 중"),
	DESTINATION_HUB_ARRIVED("목적지 허브 도착"),
	COMPANY_PENDING("업체 배송 대기 중"),
	COMPANY_MOVING("업체 배송 중"),
	DESTINATION_COMPANY_ARRIVED("업체 도착");

	private final String description;
}
