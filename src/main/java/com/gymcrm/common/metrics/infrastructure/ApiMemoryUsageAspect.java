package com.gymcrm.common.metrics.infrastructure;

import com.gymcrm.common.metrics.application.ApiMemoryUsageMetricService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ApiMemoryUsageAspect {
	private final ApiMemoryUsageMetricService apiMemoryUsageMetricService;

	@Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || "
	        + "@annotation(org.springframework.web.bind.annotation.PostMapping) || "
	        + "@annotation(org.springframework.web.bind.annotation.PutMapping) || "
	        + "@annotation(org.springframework.web.bind.annotation.PatchMapping) || "
	        + "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
	public Object measureMemoryUsage(ProceedingJoinPoint joinPoint) throws Throwable {
		long beforeMemory = apiMemoryUsageMetricService.getMemoryUsage();
		try {
			return joinPoint.proceed();
		} finally {
			long afterMemory = apiMemoryUsageMetricService.getMemoryUsage();
			long memoryUsed = afterMemory - beforeMemory;
			System.out.println("Custom API memory usage recorded: " + memoryUsed + " bytes");
		}
	}
}
