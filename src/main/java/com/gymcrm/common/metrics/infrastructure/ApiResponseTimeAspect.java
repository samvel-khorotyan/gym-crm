package com.gymcrm.common.metrics.infrastructure;

import com.gymcrm.common.metrics.application.ApiResponseTimeMetricService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ApiResponseTimeAspect {
	private final ApiResponseTimeMetricService apiResponseTimeMetricService;

	@Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || "
	        + "@annotation(org.springframework.web.bind.annotation.PostMapping) || "
	        + "@annotation(org.springframework.web.bind.annotation.PutMapping) || "
	        + "@annotation(org.springframework.web.bind.annotation.PatchMapping) || "
	        + "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
	public Object measureResponseTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			return joinPoint.proceed();
		} finally {
			long duration = System.currentTimeMillis() - start;
			apiResponseTimeMetricService.recordResponseTime(duration);
			System.out.println("Custom API response time recorded: " + duration + "ms");
		}
	}
}
