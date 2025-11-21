package com.smartlogis.deliveryservice.domain.exception;

import com.smartlogis.common.exception.AbstractException;

public class InvalidDeliveryHistoryStatusException extends AbstractException {
	public InvalidDeliveryHistoryStatusException(DeliveryMessageCode messageCode) {
		super(messageCode);
	}

	public InvalidDeliveryHistoryStatusException(DeliveryMessageCode messageCode, Object... messageArguments) {
		super(messageCode, messageArguments);
	}
}
