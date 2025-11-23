package com.smartlogis.deliveryservice;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Controller 테스트 전용 Application 클래스
 * QueryDslConfig를 Import하지 않아 @WebMvcTest에서 EntityManager 의존성 문제가 발생하지 않음
 */
@SpringBootApplication
public class TestApplication {
	// Controller tests only - no QueryDSL configuration needed
}
