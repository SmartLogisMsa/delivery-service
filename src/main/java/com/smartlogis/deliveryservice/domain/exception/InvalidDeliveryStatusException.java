package com.smartlogis.deliveryservice.domain.exception;

import com.smartlogis.common.exception.AbstractException;

public class InvalidDeliveryStatusException extends AbstractException {
	public InvalidDeliveryStatusException(DeliveryMessageCode messageCode) {
		super(messageCode);
	}

	public InvalidDeliveryStatusException(DeliveryMessageCode messageCode, Object... messageArguments) {
		super(messageCode, messageArguments);
	}
}
