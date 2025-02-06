package com.gymcrm.unit.common.metrics.application;

import com.gymcrm.common.metrics.application.ApiMemoryUsageMetricService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiMemoryUsageMetricServiceTest {
    private MeterRegistry meterRegistry;
    private ApiMemoryUsageMetricService apiMemoryUsageMetricService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        apiMemoryUsageMetricService = new ApiMemoryUsageMetricService(meterRegistry);
    }

    @Test
    void shouldReturnCorrectMemoryUsage() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long expectedUsage = totalMemory - freeMemory;

        long actualUsage = apiMemoryUsageMetricService.getMemoryUsage();

        assertTrue(actualUsage >= 0, "Memory usage should be non-negative");
        assertEquals(expectedUsage, actualUsage, "Memory usage calculation is incorrect");
    }

    @Test
    void shouldRegisterCustomGaugeMetric() {
        Gauge gauge = meterRegistry.find("custom_api_memory_usage").gauge();

        assertNotNull(gauge, "Gauge should be registered");
        assertEquals("custom_api_memory_usage", gauge.getId().getName());
        assertEquals(2, gauge.getId().getTags().size());
    }
}
