package com.smartlogis.deliveryservice.interfaces.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryHistoryStatusUpdateRequest {
	@NotBlank(message = "상태는 필수입니다")
	private String status;

	private BigDecimal actualDistanceKm;

	private Integer actualDurationMin;
}
