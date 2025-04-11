package com.gymcrm.trainer.adapter.output.messaging;

import com.gymcrm.configuration.messaging.JmsConfig;
import com.gymcrm.configuration.messaging.metrics.JmsMetrics;
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
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadMessagingAdapter
        implements
            ResponseCleanupPort,
            LoadTrainerWorkloadPort,
            UpdateTrainerWorkloadPort,
            ReceiveTrainerWorkloadResponsePort {
	private static final String TRAINER_WORKLOAD_SERVICE = "trainerWorkloadService";
	private static final int RESPONSE_TIMEOUT_SECONDS = 10;

	private static final String LOG_SENDING_WORKLOAD = "Sending trainer workload message for trainer: {}, action: {}";
	private static final String LOG_SENT_SUCCESSFULLY = "Successfully sent trainer workload message";
	private static final String LOG_SEND_FAILED = "Failed to send trainer workload message: {}";
	private static final String LOG_GETTING_WORKLOAD = "Getting monthly workload for trainer: {}, year: {}, month: {}";
	private static final String LOG_RECEIVED_WORKLOAD = "Successfully received monthly workload for trainer: {}, summary duration: {}";
	private static final String LOG_TIMEOUT = "Timeout waiting for response";
	private static final String LOG_CIRCUIT_BREAKER_SEND = "Circuit breaker activated for sendTrainerWorkload. Error: {}";
	private static final String LOG_CIRCUIT_BREAKER_GET = "Circuit breaker activated for getTrainerMonthlyWorkload. Error: {}";
	private static final String LOG_UNKNOWN_TRANSACTION = "Received response for unknown transaction ID: {}";
	private static final String LOG_ERROR_GETTING_WORKLOAD = "Error getting trainer monthly workload: {}";

	private final JmsTemplate jmsTemplate;
	private final JmsMetrics jmsMetrics;

	private final ConcurrentHashMap<String, CompletableFuture<TrainerWorkloadResponseMessage>> pendingResponses = new ConcurrentHashMap<>();

	@Override
	@Retry(name = TRAINER_WORKLOAD_SERVICE)
	@CircuitBreaker(name = TRAINER_WORKLOAD_SERVICE,fallbackMethod = "sendTrainerWorkloadFallback")
	public void sendTrainerWorkload(Training training, ActionType actionType) {
		String transactionId = ensureTransactionId();
		Trainer trainer = training.getTrainer();

		log.info("Transaction ID: {} - " + LOG_SENDING_WORKLOAD, transactionId, trainer.getUser().getUsername(),
		        actionType);

		TrainerWorkloadMessage message = createTrainerWorkloadMessage(training, trainer, actionType, transactionId);
		sendMessage(message, transactionId);
	}

	@Override
	@Retry(name = TRAINER_WORKLOAD_SERVICE)
	@CircuitBreaker(name = TRAINER_WORKLOAD_SERVICE,fallbackMethod = "getTrainerMonthlyWorkloadFallback")
	public TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkload(String username, int year, int month) {
		String transactionId = ensureTransactionId();

		log.info("Transaction ID: {} - " + LOG_GETTING_WORKLOAD, transactionId, username, year, month);

		CompletableFuture<TrainerWorkloadResponseMessage> future = new CompletableFuture<>();
		pendingResponses.put(transactionId, future);

		long startTime = System.currentTimeMillis();
		try {
			sendGetWorkloadRequest(username, year, month, transactionId);
			TrainerWorkloadResponseMessage response = waitForResponse(future, transactionId);

			log.info("Transaction ID: {} - " + LOG_RECEIVED_WORKLOAD, transactionId, username,
			        response.getSummaryDuration());

			return mapToWorkloadResponse(response);
		} catch (Exception e) {
			handleWorkloadRequestException(e, transactionId);
			throw new RuntimeException("Error getting trainer monthly workload", e);
		} finally {
			pendingResponses.remove(transactionId);
			jmsMetrics.recordProcessingTime(System.currentTimeMillis() - startTime);
		}
	}

	@Override
	public void handleWorkloadResponse(TrainerWorkloadResponseMessage response) {
		String transactionId = response.getTransactionId();
		CompletableFuture<TrainerWorkloadResponseMessage> future = pendingResponses.get(transactionId);

		if (future != null) {
			jmsMetrics.recordMessageReceived();
			future.complete(response);
		} else {
			log.warn("Transaction ID: {} - " + LOG_UNKNOWN_TRANSACTION, transactionId, transactionId);
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

	private void sendTrainerWorkloadFallback(Exception e) {
		String transactionId = MDC.get("transactionId");
		log.warn("Transaction ID: {} - " + LOG_CIRCUIT_BREAKER_SEND, transactionId, e.getMessage());
	}

	private TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkloadFallback(String username, int year, int month,
	        Exception e) {
		String transactionId = MDC.get("transactionId");
		log.warn("Transaction ID: {} - " + LOG_CIRCUIT_BREAKER_GET, transactionId, e.getMessage());

		return TrainerMonthlyWorkloadResponse.builder().username(username).firstName("N/A").lastName("N/A")
		        .isActive(true).year(year).month(month).summaryDuration(0).build();
	}

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
			jmsTemplate.convertAndSend(JmsConfig.TRAINER_WORKLOAD_QUEUE, message);
			jmsMetrics.recordMessageSent();
			log.info("Transaction ID: {} - " + LOG_SENT_SUCCESSFULLY, transactionId);
		} catch (JmsException e) {
			jmsMetrics.recordMessageFailed();
			log.error("Transaction ID: {} - " + LOG_SEND_FAILED, transactionId, e.getMessage(), e);
			throw e;
		} finally {
			jmsMetrics.recordProcessingTime(System.currentTimeMillis() - startTime);
		}
	}

	private void sendGetWorkloadRequest(String username, int year, int month, String transactionId) {
		TrainerWorkloadMessage message = TrainerWorkloadMessage.builder().username(username).year(year).month(month)
		        .actionType(ActionType.GET).transactionId(transactionId).build();

		jmsTemplate.convertAndSend(JmsConfig.TRAINER_WORKLOAD_QUEUE, message);
		jmsMetrics.recordMessageSent();
	}

	private TrainerWorkloadResponseMessage waitForResponse(CompletableFuture<TrainerWorkloadResponseMessage> future,
	        String transactionId) throws Exception {

		try {
			return future.get(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			jmsMetrics.recordMessageFailed();
			log.error("Transaction ID: {} - " + LOG_TIMEOUT, transactionId);
			throw e;
		}
	}

	private void handleWorkloadRequestException(Exception e, String transactionId) {
		jmsMetrics.recordMessageFailed();
		if (e instanceof TimeoutException) {
			log.error("Transaction ID: - {} - " + LOG_TIMEOUT, transactionId);
		} else {
			log.error("Transaction ID: - {} - " + LOG_ERROR_GETTING_WORKLOAD, transactionId, e.getMessage(), e);
		}
	}

	private TrainerMonthlyWorkloadResponse mapToWorkloadResponse(TrainerWorkloadResponseMessage response) {
		return TrainerMonthlyWorkloadResponse.builder().username(response.getUsername())
		        .firstName(response.getFirstName()).lastName(response.getLastName()).isActive(response.getIsActive())
		        .year(response.getYear()).month(response.getMonth()).summaryDuration(response.getSummaryDuration())
		        .build();
	}
}
