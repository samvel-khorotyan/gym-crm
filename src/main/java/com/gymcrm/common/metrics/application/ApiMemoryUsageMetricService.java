package com.gymcrm.common.metrics.application;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class ApiMemoryUsageMetricService {
	private final Runtime runtime = Runtime.getRuntime();

	public ApiMemoryUsageMetricService(MeterRegistry meterRegistry) {
		Gauge.builder("custom_api_memory_usage", this, ApiMemoryUsageMetricService::getMemoryUsage)
		        .description("Custom API memory usage in bytes").tag("type", "heap").tag("source", "custom_metric")
		        .register(meterRegistry);
	}

	public long getMemoryUsage() {
		return runtime.totalMemory() - runtime.freeMemory();
	}
}
