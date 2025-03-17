package com.gymcrm.trainer.adapter.output.persistence;

import com.gymcrm.configuration.security.JwtTokenProvider;
import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.application.port.output.LoadTrainerWorkloadPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerWorkloadPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.domain.Training;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class TrainerWorkloadPersistenceAdapter implements UpdateTrainerWorkloadPort, LoadTrainerWorkloadPort {
	private final WebClient webClient;
	private final JwtTokenProvider jwtTokenProvider;

	private static final String TRAINER_WORKLOAD_SERVICE = "trainerWorkloadService";

	@Autowired
	public TrainerWorkloadPersistenceAdapter(WebClient.Builder webClientBuilder, JwtTokenProvider jwtTokenProvider) {
		this.webClient = webClientBuilder.baseUrl("http://trainer-workload-service").build();
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	@CircuitBreaker(name = TRAINER_WORKLOAD_SERVICE,fallbackMethod = "sendTrainerWorkloadFallback")
	@Retry(name = TRAINER_WORKLOAD_SERVICE)
	public void sendTrainerWorkload(Training training, String actionType) {
		String transactionId = MDC.get("transactionId");
		Trainer trainer = training.getTrainer();

		log.info("Transaction ID: {} - Sending trainer workload to secondary service for trainer: {}, action: {}",
		        transactionId, trainer.getUser().getUsername(), actionType);

		webClient.post().uri("/api/v1/workload")
		        .header("Authorization", "Bearer " + jwtTokenProvider.generateServiceToken())
		        .header("X-Transaction-ID", transactionId)
		        .bodyValue(Map.of("username", trainer.getUser().getUsername(), "first_name",
		                trainer.getUser().getFirstName(), "last_name", trainer.getUser().getLastName(), "is_active",
		                trainer.getUser().getIsActive(), "training_date", training.getTrainingDate(),
		                "training_duration", training.getTrainingDuration(), "action_type", actionType))
		        .retrieve()
		        .onStatus(status -> status != HttpStatus.OK,
		                clientResponse -> clientResponse.bodyToMono(String.class)
		                        .flatMap(errorBody -> Mono.error(
		                                new RuntimeException("Error from trainer workload service: " + errorBody))))
		        .bodyToMono(Void.class).block();

		log.info("Transaction ID: {} - Successfully sent trainer workload to secondary service", transactionId);
	}

	public void sendTrainerWorkloadFallback(Training training, String actionType, Exception e) {
		String transactionId = MDC.get("transactionId");
		log.warn("Transaction ID: {} - Circuit breaker activated for sendTrainerWorkload. Error: {}", transactionId,
		        e.getMessage());
	}

	@Override
	@CircuitBreaker(name = TRAINER_WORKLOAD_SERVICE,fallbackMethod = "getTrainerMonthlyWorkloadFallback")
	@Retry(name = TRAINER_WORKLOAD_SERVICE)
	public TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkload(String username, int year, int month) {
		String transactionId = MDC.get("transactionId");
		log.info("Transaction ID: {} - Getting monthly workload for trainer: {}, year: {}, month: {}", transactionId,
		        username, year, month);

		TrainerMonthlyWorkloadResponse response = webClient.get()
		        .uri(uriBuilder -> uriBuilder.path("/api/v1/workload/{username}/{year}/{month}").build(username, year,
		                month))
		        .header("Authorization", "Bearer " + jwtTokenProvider.generateServiceToken())
		        .header("X-Transaction-ID", transactionId).retrieve()
		        .onStatus(status -> !status.is2xxSuccessful(),
		                clientResponse -> clientResponse.bodyToMono(String.class)
		                        .flatMap(errorBody -> Mono.error(
		                                new RuntimeException("Error from trainer workload service: " + errorBody))))
		        .bodyToMono(TrainerMonthlyWorkloadResponse.class).block();

		log.info("Transaction ID: {} - Successfully retrieved monthly workload for trainer: {}, summary duration: {}",
		        transactionId, username, response != null ? response.getSummaryDuration() : "null");

		return response;
	}

	public TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkloadFallback(String username, int year, int month,
	        Exception e) {
		String transactionId = MDC.get("transactionId");
		log.warn("Transaction ID: {} - Circuit breaker activated for getTrainerMonthlyWorkload. Error: {}",
		        transactionId, e.getMessage());

		return TrainerMonthlyWorkloadResponse.builder().username(username).firstName("N/A").lastName("N/A")
		        .isActive(true).year(year).month(month).summaryDuration(0).build();
	}
}
