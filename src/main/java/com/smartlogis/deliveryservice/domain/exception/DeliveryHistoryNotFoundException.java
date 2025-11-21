package com.smartlogis.deliveryservice.domain.exception;

import com.smartlogis.common.exception.AbstractException;

public class DeliveryHistoryNotFoundException extends AbstractException {
	public DeliveryHistoryNotFoundException(DeliveryMessageCode messageCode) {
		super(messageCode);
	}

	public DeliveryHistoryNotFoundException(DeliveryMessageCode messageCode, Object... messageArguments) {
		super(messageCode, messageArguments);
	}
}
