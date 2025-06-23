package com.gymcrm.trainer.adapter.output.messaging;

import com.gymcrm.configuration.messaging.metrics.SqsMetrics;
import com.gymcrm.configuration.messaging.sqs.SqsMessageTemplate;
import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadMessage;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadResponseMessage;
import com.gymcrm.trainer.application.port.output.LoadTrainerWorkloadPort;
import com.gymcrm.trainer.application.port.output.ReceiveTrainerWorkloadResponsePort;
import com.gymcrm.trainer.application.port.output.ResponseCleanupPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerWorkloadPort;
import com.gymcrm.trainer.domain.ActionType;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.domain.Training;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadSqsAdapter
        implements
            ResponseCleanupPort,
            LoadTrainerWorkloadPort,
            UpdateTrainerWorkloadPort,
            ReceiveTrainerWorkloadResponsePort {
	private static final String TRAINER_WORKLOAD_SERVICE = "trainerWorkloadService";
	private static final int RESPONSE_TIMEOUT_SECONDS = 10;

	private final SqsMessageTemplate sqsMessageTemplate;
	private final SqsMetrics sqsMetrics; // Changed from JmsMetrics

	private final ConcurrentHashMap<String, CompletableFuture<TrainerWorkloadResponseMessage>> pendingResponses = new ConcurrentHashMap<>();

	@Override
	@Retry(name = TRAINER_WORKLOAD_SERVICE)
	@CircuitBreaker(name = TRAINER_WORKLOAD_SERVICE,fallbackMethod = "sendTrainerWorkloadFallback")
	public void sendTrainerWorkload(Training training, ActionType actionType) {
		String transactionId = ensureTransactionId();
		Trainer trainer = training.getTrainer();

		log.info("Transaction ID: {} - Sending trainer workload message for trainer: {}, action: {}", transactionId,
		        trainer.getUser().getUsername(), actionType);

		TrainerWorkloadMessage message = createTrainerWorkloadMessage(training, trainer, actionType, transactionId);
		sendMessage(message, transactionId);
	}

	@Override
	@Retry(name = TRAINER_WORKLOAD_SERVICE)
	@CircuitBreaker(name = TRAINER_WORKLOAD_SERVICE,fallbackMethod = "getTrainerMonthlyWorkloadFallback")
	public TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkload(String username, int year, int month) {
		String transactionId = ensureTransactionId();

		log.info("Transaction ID: {} - Getting monthly workload for trainer: {}, year: {}, month: {}", transactionId,
		        username, year, month);

		CompletableFuture<TrainerWorkloadResponseMessage> future = new CompletableFuture<>();
		pendingResponses.put(transactionId, future);

		long startTime = System.currentTimeMillis();
		try {
			sendGetWorkloadRequest(username, year, month, transactionId);
			TrainerWorkloadResponseMessage response = waitForResponse(future, transactionId);

			log.info(
			        "Transaction ID: {} - Successfully received monthly workload for trainer: {}, summary duration: {}",
			        transactionId, username, response.getSummaryDuration());

			return mapToWorkloadResponse(response);
		} catch (Exception e) {
			handleWorkloadRequestException(e, transactionId);
			throw new RuntimeException("Error getting trainer monthly workload", e);
		} finally {
			pendingResponses.remove(transactionId);
			sqsMetrics.recordProcessingTime(System.currentTimeMillis() - startTime); // Changed from jmsMetrics
		}
	}

	@Override
	public void handleWorkloadResponse(TrainerWorkloadResponseMessage response) {
		String transactionId = response.getTransactionId();
		CompletableFuture<TrainerWorkloadResponseMessage> future = pendingResponses.get(transactionId);

		if (future != null) {
			sqsMetrics.recordMessageReceived(); // Changed from jmsMetrics
			future.complete(response);
		} else {
			log.warn("Transaction ID: {} - Received response for unknown transaction ID: {}", transactionId,
			        transactionId);
		}
	}

	@Override
	public int cleanupExpiredResponses() {
		if (pendingResponses.isEmpty()) {
			log.debug("No pending responses to clean up");
			return 0;
		}

		int expiredCount = 0;
		for (Iterator<Map.Entry<String, CompletableFuture<TrainerWorkloadResponseMessage>>> it = pendingResponses
		        .entrySet().iterator(); it.hasNext();) {

			Map.Entry<String, CompletableFuture<TrainerWorkloadResponseMessage>> entry = it.next();
			String transactionId = entry.getKey();
			CompletableFuture<TrainerWorkloadResponseMessage> future = entry.getValue();

			if (future.isDone() || future.isCancelled() || future.isCompletedExceptionally()) {
				it.remove();
				expiredCount++;
				log.debug("Removed completed/cancelled future for transaction ID: {}", transactionId);
			}
		}

		return expiredCount;
	}

	// Fallback methods
	private void sendTrainerWorkloadFallback(Training training, ActionType actionType, Exception e) {
		String transactionId = MDC.get("transactionId");
		log.warn("Transaction ID: {} - Circuit breaker activated for sendTrainerWorkload. Error: {}", transactionId,
		        e.getMessage());
	}

	private TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkloadFallback(String username, int year, int month,
	        Exception e) {
		String transactionId = MDC.get("transactionId");
		log.warn("Transaction ID: {} - Circuit breaker activated for getTrainerMonthlyWorkload. Error: {}",
		        transactionId, e.getMessage());

		return TrainerMonthlyWorkloadResponse.builder().username(username).firstName("N/A").lastName("N/A")
		        .isActive(true).year(year).month(month).summaryDuration(0).build();
	}

	// Private helper methods
	private String ensureTransactionId() {
		String transactionId = MDC.get("transactionId");
		if (transactionId == null) {
			transactionId = UUID.randomUUID().toString();
			MDC.put("transactionId", transactionId);
		}
		return transactionId;
	}

	private TrainerWorkloadMessage createTrainerWorkloadMessage(Training training, Trainer trainer,
	        ActionType actionType, String transactionId) {

		return TrainerWorkloadMessage.builder().username(trainer.getUser().getUsername())
		        .firstName(trainer.getUser().getFirstName()).lastName(trainer.getUser().getLastName())
		        .isActive(trainer.getUser().getIsActive()).trainingDate(training.getTrainingDate())
		        .trainingDuration(training.getTrainingDuration()).actionType(actionType).transactionId(transactionId)
		        .build();
	}

	private void sendMessage(TrainerWorkloadMessage message, String transactionId) {
		long startTime = System.currentTimeMillis();
		try {
			sqsMessageTemplate.sendToTrainerWorkloadQueue(message);
			sqsMetrics.recordMessageSent(); // Changed from jmsMetrics
			log.info("Transaction ID: {} - Successfully sent trainer workload message", transactionId);
		} catch (Exception e) {
			sqsMetrics.recordMessageFailed(); // Changed from jmsMetrics
			log.error("Transaction ID: {} - Failed to send trainer workload message: {}", transactionId, e.getMessage(),
			        e);
			throw e;
		} finally {
			sqsMetrics.recordProcessingTime(System.currentTimeMillis() - startTime); // Changed from jmsMetrics
		}
	}

	private void sendGetWorkloadRequest(String username, int year, int month, String transactionId) {
		TrainerWorkloadMessage message = TrainerWorkloadMessage.builder().username(username).year(year).month(month)
		        .actionType(ActionType.GET).transactionId(transactionId).build();

		sqsMessageTemplate.sendToTrainerWorkloadQueue(message);
		sqsMetrics.recordMessageSent(); // Changed from jmsMetrics
	}

	private TrainerWorkloadResponseMessage waitForResponse(CompletableFuture<TrainerWorkloadResponseMessage> future,
	        String transactionId) throws Exception {

		try {
			return future.get(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			sqsMetrics.recordMessageFailed(); // Changed from jmsMetrics
			log.error("Transaction ID: {} - Timeout waiting for response", transactionId);
			throw e;
		}
	}

	private void handleWorkloadRequestException(Exception e, String transactionId) {
		sqsMetrics.recordMessageFailed(); // Changed from jmsMetrics
		if (e instanceof TimeoutException) {
			log.error("Transaction ID: {} - Timeout waiting for response", transactionId);
		} else {
			log.error("Transaction ID: {} - Error getting trainer monthly workload: {}", transactionId, e.getMessage(),
			        e);
		}
	}

	private TrainerMonthlyWorkloadResponse mapToWorkloadResponse(TrainerWorkloadResponseMessage response) {
		return TrainerMonthlyWorkloadResponse.builder().username(response.getUsername())
		        .firstName(response.getFirstName()).lastName(response.getLastName()).isActive(response.getIsActive())
		        .year(response.getYear()).month(response.getMonth()).summaryDuration(response.getSummaryDuration())
		        .build();
	}
}
