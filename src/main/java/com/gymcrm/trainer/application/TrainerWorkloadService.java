package com.gymcrm.trainer.application;

import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
import com.gymcrm.trainer.application.port.output.LoadTrainerWorkloadPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadService implements LoadTrainerWorkloadUseCase {
	private final LoadTrainerWorkloadPort loadTrainerWorkloadPort;

	@Override
	@CircuitBreaker(name = "trainerWorkloadService",fallbackMethod = "getTrainerMonthlyWorkloadFallback")
	public TrainerMonthlyWorkloadResponse loadTrainerMonthlyWorkload(String username, int year, int month) {
		return loadTrainerWorkloadPort.getTrainerMonthlyWorkload(username, year, month);
	}

	@Override
	public TrainerMonthlyWorkloadResponse loadTrainerCurrentMonthWorkload(String username) {
		LocalDate now = LocalDate.now();
		return loadTrainerMonthlyWorkload(username, now.getYear(), now.getMonthValue());
	}

	private TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkloadFallback(String username, int year, int month,
	        Throwable t) {
		log.error(
		        "Circuit breaker triggered for getTrainerMonthlyWorkload. Failed to get workload for username: {}. Error: {}",
		        username, t.getMessage());

		return TrainerMonthlyWorkloadResponse.builder().username(username).year(year).month(month).summaryDuration(0)
		        .build();
	}
}
