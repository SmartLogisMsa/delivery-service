package com.smartlogis.deliveryservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.smartlogis.deliveryservice.infrastructure.client.dto.ApiResponseWrapper;
import com.smartlogis.deliveryservice.infrastructure.client.dto.ProductInfoResponse;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

	@GetMapping("/{productId}")
	ApiResponseWrapper<ProductInfoResponse> getProduct(@PathVariable UUID productId);
}
