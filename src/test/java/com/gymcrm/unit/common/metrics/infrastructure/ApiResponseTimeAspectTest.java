package com.gymcrm.unit.common.metrics.infrastructure;

import static org.mockito.Mockito.*;

import com.gymcrm.common.metrics.application.ApiResponseTimeMetricService;
import com.gymcrm.common.metrics.infrastructure.ApiResponseTimeAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApiResponseTimeAspectTest {
	@Mock
	private ApiResponseTimeMetricService apiResponseTimeMetricService;

	@Mock
	private ProceedingJoinPoint joinPoint;

	@InjectMocks
	private ApiResponseTimeAspect apiResponseTimeAspect;

	@Test
  void testMeasureResponseTime_VerifyDurationRecorded() throws Throwable {
    when(joinPoint.proceed())
        .thenAnswer(
            invocation -> {
              Thread.sleep(100);
              return null;
            });

    apiResponseTimeAspect.measureResponseTime(joinPoint);

    verify(apiResponseTimeMetricService, times(1))
        .recordResponseTime(Mockito.longThat(duration -> duration >= 100 && duration < 200));
  }
}
