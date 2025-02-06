package com.gymcrm.unit.common.metrics.infrastructure;

import com.gymcrm.common.metrics.application.ApiMemoryUsageMetricService;
import com.gymcrm.common.metrics.infrastructure.ApiMemoryUsageAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Aspect
class ApiMemoryUsageAspectTest {
    @Mock
    private ApiMemoryUsageMetricService apiMemoryUsageMetricService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    private ApiMemoryUsageAspect apiMemoryUsageAspect;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldMeasureMemoryUsageAndProceed() throws Throwable {
        long beforeMemory = 100000L;
        long afterMemory = 120000L;
        long expectedMemoryUsed = afterMemory - beforeMemory;

        when(apiMemoryUsageMetricService.getMemoryUsage()).thenReturn(beforeMemory, afterMemory);
        when(proceedingJoinPoint.proceed()).thenReturn("Success");

        Object result = apiMemoryUsageAspect.measureMemoryUsage(proceedingJoinPoint);

        assertEquals("Success", result);
        verify(apiMemoryUsageMetricService, times(2)).getMemoryUsage();
        verify(proceedingJoinPoint, times(1)).proceed();
        System.out.println("Memory Usage Captured: " + expectedMemoryUsed + " bytes");
    }
}
