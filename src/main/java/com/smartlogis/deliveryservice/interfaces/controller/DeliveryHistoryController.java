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
import com.smartlogis.deliveryservice.application.service.DeliveryHistoryService;
import com.smartlogis.deliveryservice.domain.entity.DeliveryHistoryStatus;
import com.smartlogis.deliveryservice.interfaces.dto.request.DeliveryHistoryStatusUpdateRequest;
import com.smartlogis.deliveryservice.interfaces.dto.response.DeliveryHistoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/histories")
@RequiredArgsConstructor
@Tag(name = "Delivery History API", description = "배송 기록 관련 API")
public class DeliveryHistoryController {

	private final DeliveryHistoryService deliveryHistoryService;

	@GetMapping("/{deliveryHistoryId}")
	@PreAuthorize("isAuthenticated()")
	@Operation(
		summary = "배송 기록 단건 조회",
		description = "삭제되지 않은 배송 기록을 ID로 조회합니다."
	)
	public ResponseEntity<ApiResponse<DeliveryHistoryResponse>> getDeliveryHistory(
		@PathVariable
		@Parameter(description = "배송 기록 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID deliveryHistoryId) {
		DeliveryHistoryResponse response = deliveryHistoryService.getDeliveryHistory(deliveryHistoryId);
		return ResponseEntity.ok(ApiResponse.successWithDataOnly(response));
	}

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	@Operation(
		summary = "배송 기록 검색",
		description = "다양한 조건으로 배송 기록 목록을 검색합니다. 모든 파라미터는 선택사항입니다."
	)
	public ResponseEntity<ApiResponse<PageResponse<DeliveryHistoryResponse>>> searchDeliveryHistories(
		@RequestParam(required = false)
		@Parameter(description = "배송 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID deliveryId,
		@RequestParam(required = false)
		@Parameter(description = "출발 허브 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID departureHubId,
		@RequestParam(required = false)
		@Parameter(description = "도착 허브 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID destinationHubId,
		@RequestParam(required = false)
		@Parameter(description = "허브 배송 담당자 Slack ID", example = "U09LA4X869K")
		String hubDeliveryManagerId,
		@RequestParam(required = false)
		@Parameter(description = "배송 기록 상태", example = "HUB_PENDING")
		DeliveryHistoryStatus status,
		@ModelAttribute
		@Parameter(description = "페이지네이션 요청")
		PageRequest pageRequest) {
		PageResponse<DeliveryHistoryResponse> response = deliveryHistoryService.searchDeliveryHistories(
			deliveryId, departureHubId, destinationHubId, hubDeliveryManagerId, status, pageRequest
		);
		return ResponseEntity.ok(ApiResponse.successWithDataOnly(response));
	}

	@PatchMapping("/{deliveryHistoryId}/status")
	@PreAuthorize("isAuthenticated()")
	@Operation(
		summary = "배송 기록 상태 업데이트",
		description = "배송 기록의 상태를 업데이트합니다."
	)
	public ResponseEntity<ApiResponse<Void>> updateDeliveryHistoryStatus(
		@PathVariable
		@Parameter(description = "배송 기록 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID deliveryHistoryId,
		@RequestBody
		@Valid
		DeliveryHistoryStatusUpdateRequest request) {
		String status = request.getStatus();

		if ("HUB_MOVING".equals(status)) {
			deliveryHistoryService.startHubDelivery(deliveryHistoryId);
		} else if ("DESTINATION_HUB_ARRIVED".equals(status)) {
			deliveryHistoryService.arriveAtDestinationHub(
				deliveryHistoryId,
				request.getActualDistanceKm(),
				request.getActualDurationMin()
			);
		} else if ("COMPANY_MOVING".equals(status)) {
			deliveryHistoryService.startCompanyDelivery(deliveryHistoryId);
		} else if ("DESTINATION_COMPANY_ARRIVED".equals(status)) {
			deliveryHistoryService.completeDelivery(
				deliveryHistoryId,
				request.getActualDistanceKm(),
				request.getActualDurationMin()
			);
		}

		return ResponseEntity.ok(ApiResponse.successWithDataOnly(null));
	}

	@DeleteMapping("/{deliveryHistoryId}")
	@PreAuthorize("hasAnyRole('MASTER')")
	@Operation(
		summary = "배송 기록 삭제 (논리적 삭제)",
		description = "배송 기록을 논리적으로 삭제합니다. 삭제된 배송 기록은 조회되지 않습니다."
	)
	public ResponseEntity<ApiResponse<Void>> deleteDeliveryHistory(
		@PathVariable
		@Parameter(description = "배송 기록 ID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID deliveryHistoryId) {
		deliveryHistoryService.deleteDeliveryHistory(deliveryHistoryId);
		return ResponseEntity.ok(ApiResponse.successWithDataOnly(null));
	}
}
