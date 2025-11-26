package com.smartlogis.deliveryservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.smartlogis.deliveryservice.infrastructure.client.dto.ApiResponseWrapper;
import com.smartlogis.deliveryservice.infrastructure.client.dto.UserInfoResponse;

@FeignClient(name = "user-service")
public interface UserServiceClient {

	@GetMapping("/v1/users/{userId}")
	ApiResponseWrapper<UserInfoResponse> getUser(@PathVariable UUID userId);
}
