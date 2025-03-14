package com.gymcrm.trainer.adapter.output.persistence;

import com.gymcrm.configuration.security.JwtTokenProvider;
import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.application.port.output.LoadTrainerWorkloadPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerWorkloadPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.domain.Training;
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

	@Autowired
	public TrainerWorkloadPersistenceAdapter(WebClient.Builder webClientBuilder, JwtTokenProvider jwtTokenProvider) {
		this.webClient = webClientBuilder.baseUrl("http://trainer-workload-service").build();
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void sendTrainerWorkload(Training training, String actionType) {
		String transactionId = MDC.get("transactionId");
		Trainer trainer = training.getTrainer();

		log.info("Transaction ID: {} - Sending trainer workload to secondary service for trainer: {}, action: {}",
		        transactionId, trainer.getUser().getUsername(), actionType);

		try {
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
		} catch (Exception e) {
			log.error("Transaction ID: {} - Failed to send trainer workload to secondary service: {}", transactionId,
			        e.getMessage(), e);
		}
	}

	@Override
	public TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkload(String username, int year, int month) {
		String transactionId = MDC.get("transactionId");
		log.info("Transaction ID: {} - Getting monthly workload for trainer: {}, year: {}, month: {}", transactionId,
		        username, year, month);

		try {
			TrainerMonthlyWorkloadResponse response = webClient.get()
			        .uri(uriBuilder -> uriBuilder.path("/api/v1/workload/{username}/{year}/{month}").build(username,
			                year, month))
			        .header("Authorization", "Bearer " + jwtTokenProvider.generateServiceToken())
			        .header("X-Transaction-ID", transactionId).retrieve()
			        .onStatus(status -> !status.is2xxSuccessful(),
			                clientResponse -> clientResponse.bodyToMono(String.class)
			                        .flatMap(errorBody -> Mono.error(
			                                new RuntimeException("Error from trainer workload service: " + errorBody))))
			        .bodyToMono(TrainerMonthlyWorkloadResponse.class).block();

			log.info(
			        "Transaction ID: {} - Successfully retrieved monthly workload for trainer: {}, summary duration: {}",
			        transactionId, username, response != null ? response.getSummaryDuration() : "null");

			return response;
		} catch (Exception e) {
			log.error("Transaction ID: {} - Failed to get monthly workload for trainer: {}, Error: {}", transactionId,
			        username, e.getMessage(), e);
			throw new RuntimeException("Failed to get trainer monthly workload", e);
		}
	}
}
