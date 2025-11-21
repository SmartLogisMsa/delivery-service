package com.smartlogis.deliveryservice.domain.exception;

import com.smartlogis.common.exception.AbstractException;

public class DeliveryCancelFailedException extends AbstractException {
	public DeliveryCancelFailedException(DeliveryMessageCode messageCode) {
		super(messageCode);
	}

	public DeliveryCancelFailedException(DeliveryMessageCode messageCode, Object... messageArguments) {
		super(messageCode, messageArguments);
	}
}
