package com.smartlogis.deliveryservice.domain.exception;

import com.smartlogis.common.exception.AbstractException;

public class DeliveryNotFoundException extends AbstractException {
	public DeliveryNotFoundException(DeliveryMessageCode messageCode) {
		super(messageCode);
	}

	public DeliveryNotFoundException(DeliveryMessageCode messageCode, Object... messageArguments) {
		super(messageCode, messageArguments);
	}
}
