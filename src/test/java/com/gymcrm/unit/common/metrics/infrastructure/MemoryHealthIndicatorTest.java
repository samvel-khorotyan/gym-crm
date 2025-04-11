package com.gymcrm.unit.common.metrics.infrastructure;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.metrics.infrastructure.MemoryHealthIndicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

class MemoryHealthIndicatorTest {
	private MemoryHealthIndicator memoryHealthIndicator;
	private Runtime runtimeMock;

	@BeforeEach
	void setUp() {
		runtimeMock = mock(Runtime.class);
		memoryHealthIndicator = new MemoryHealthIndicator();
	}

	@Test
	void shouldReturnUpWhenMemoryUsageIsBelowThreshold() {
		long maxMemory = 1024L * 1024 * 1024;
		long usedMemory = (maxMemory * 75) / 100;
		long freeMemory = maxMemory - usedMemory;

		when(runtimeMock.maxMemory()).thenReturn(maxMemory);
		when(runtimeMock.totalMemory()).thenReturn(maxMemory);
		when(runtimeMock.freeMemory()).thenReturn(freeMemory);

		Health health = memoryHealthIndicator.health();

		assertEquals(Health.up().build().getStatus(), health.getStatus());
		assertTrue(health.getDetails().containsKey("Memory Usage"));
	}
}
