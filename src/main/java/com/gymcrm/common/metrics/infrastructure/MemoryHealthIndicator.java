package com.gymcrm.common.metrics.infrastructure;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MemoryHealthIndicator implements HealthIndicator {
	private static final long MAX_MEMORY_THRESHOLD = Runtime.getRuntime().maxMemory() * 80 / 100; // 80% of max memory

	@Override
	public Health health() {
		long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		if (usedMemory < MAX_MEMORY_THRESHOLD) {
			return Health.up().withDetail("Memory Usage", usedMemory / (1024 * 1024) + " MB").build();
		} else {
			return Health.down().withDetail("Memory Usage", usedMemory / (1024 * 1024) + " MB")
			        .withDetail("Warning", "High memory usage").build();
		}
	}
}
