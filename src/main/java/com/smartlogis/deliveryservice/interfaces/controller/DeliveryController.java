package com.smartlogis.deliveryservice.interfaces.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.smartlogis.common.presentation.ApiResponse;
import com.smartlogis.common.presentation.dto.PageRequest;
import com.smartlogis.common.presentation.dto.PageResponse;
import com.smartlogis.deliveryservice.application.service.DeliveryService;
import com.smartlogis.deliveryservice.domain.entity.DeliveryStatus;
import com.smartlogis.deliveryservice.interfaces.dto.request.DeliveryStatusUpdateRequest;
import com.smartlogis.deliveryservice.interfaces.dto.response.DeliveryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Delivery API", description = "배송 관련 API")
public class DeliveryController {


	private final DeliveryService deliveryService;

	@GetMapping("/{deliveryId}")
	@PreAuthorize("isAuthenticated()")
	@Operation(
		summary = "배송 단건 조회",
		description = "삭제되지 않은 배송을 ID로 조회합니다."
	)
	public ResponseEntity<ApiResponse<DeliveryResponse>> getDelivery(
		@PathVariable
		@Parameter(description = "배송 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID deliveryId) {
		DeliveryResponse response = deliveryService.getDelivery(deliveryId);
		return ResponseEntity.ok(ApiResponse.successWithDataOnly(response));
	}

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	@Operation(
		summary = "배송 검색",
		description = "다양한 조건으로 배송 목록을 검색합니다. 모든 파라미터는 선택사항입니다."
	)
	public ResponseEntity<ApiResponse<PageResponse<DeliveryResponse>>> searchDeliveries(
		@RequestParam(required = false)
		@Parameter(description = "주문 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID orderId,
		@RequestParam(required = false)
		@Parameter(description = "배송 상태", example = "HUB_PENDING")
		DeliveryStatus status,
		@RequestParam(required = false)
		@Parameter(description = "출발 허브 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID departureHubId,
		@RequestParam(required = false)
		@Parameter(description = "도착 허브 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID destinationHubId,
		@RequestParam(required = false)
		@Parameter(description = "업체 배송 담당자 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID companyDeliveryManagerId,
		@ModelAttribute
		@Parameter(description = "페이지네이션 요청")
		PageRequest pageRequest) {
		PageResponse<DeliveryResponse> response = deliveryService.searchDeliveries(
			orderId, status, departureHubId, destinationHubId, companyDeliveryManagerId, pageRequest
		);
		return ResponseEntity.ok(ApiResponse.successWithDataOnly(response));
	}

	@PatchMapping("/{deliveryId}/status")
	@PreAuthorize("isAuthenticated()")
	@Operation(
		summary = "배송 상태 업데이트",
		description = "배송의 상태를 업데이트합니다."
	)
	public ResponseEntity<ApiResponse<Void>> updateDeliveryStatus(
		@PathVariable
		@Parameter(description = "배송 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID deliveryId,
		@RequestBody
		@Valid
		DeliveryStatusUpdateRequest request) {
		DeliveryStatus status = DeliveryStatus.valueOf(request.getStatus());
		deliveryService.updateDeliveryStatus(deliveryId, status);
		return ResponseEntity.ok(ApiResponse.successWithDataOnly(null));
	}

	@DeleteMapping("/{deliveryId}")
	@PreAuthorize("hasAnyRole('MASTER')")
	@Operation(
		summary = "배송 삭제 (논리적 삭제)",
		description = "배송을 논리적으로 삭제합니다. 삭제된 배송은 조회되지 않습니다."
	)
	public ResponseEntity<ApiResponse<Void>> deleteDelivery(
		@PathVariable
		@Parameter(description = "배송 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID deliveryId) {
		deliveryService.deleteDelivery(deliveryId);
		return ResponseEntity.ok(ApiResponse.successWithDataOnly(null));
	}
}
