package com.gymcrm.common.metrics.application;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class ApiResponseTimeMetricService {
	private final Timer responseTimer;

	public ApiResponseTimeMetricService(MeterRegistry meterRegistry) {
		responseTimer = Timer.builder("custom_api_response_time")
		        .description("Custom API response time in milliseconds").tag("endpoint", "generic")
		        .tag("source", "custom_metric").publishPercentileHistogram().register(meterRegistry);
	}

	public void recordResponseTime(long durationMillis) {
		responseTimer.record(durationMillis, TimeUnit.MILLISECONDS);
	}
}
