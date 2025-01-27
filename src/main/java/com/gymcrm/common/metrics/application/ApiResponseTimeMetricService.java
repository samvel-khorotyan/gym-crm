package com.gymcrm.common.metrics.application;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class ApiResponseTimeMetricService {
	private final Timer responseTimer;

	public ApiResponseTimeMetricService(MeterRegistry meterRegistry) {
		responseTimer = Timer.builder("api_response_time").description("API response time in milliseconds")
		        .tag("endpoint", "generic").publishPercentileHistogram().register(meterRegistry);
	}

	public void recordResponseTime(long durationMillis) {
		responseTimer.record(durationMillis, TimeUnit.MILLISECONDS);
	}
}
