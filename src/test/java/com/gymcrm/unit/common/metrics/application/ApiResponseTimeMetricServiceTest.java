package com.gymcrm.unit.common.metrics.application;

import com.gymcrm.common.metrics.application.ApiResponseTimeMetricService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTimeMetricServiceTest {
    private MeterRegistry meterRegistry;
    private ApiResponseTimeMetricService apiResponseTimeMetricService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        apiResponseTimeMetricService = new ApiResponseTimeMetricService(meterRegistry);
    }

    @Test
    void shouldRecordResponseTime() {
        long durationMillis = 250;

        apiResponseTimeMetricService.recordResponseTime(durationMillis);

        Timer timer = meterRegistry.find("custom_api_response_time").timer();
        assertNotNull(timer, "Timer should be registered");
        assertEquals(1, timer.count(), "Timer should record exactly 1 measurement");
        assertTrue(timer.totalTime(TimeUnit.MILLISECONDS) >= durationMillis, "Timer should correctly accumulate response time");
    }

    @Test
    void shouldRegisterCustomTimerMetric() {
        Timer timer = meterRegistry.find("custom_api_response_time").timer();

        assertNotNull(timer, "Timer should be registered");
        assertEquals("custom_api_response_time", timer.getId().getName());
        assertEquals(2, timer.getId().getTags().size());
    }
}
