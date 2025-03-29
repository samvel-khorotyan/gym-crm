package com.gymcrm.trainer.adapter.output.messaging;

import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadResponseMessage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingResponsesCleaner {
	private final TrainerWorkloadMessagingAdapter messagingAdapter;

	@Scheduled(fixedRate = 60000)
	public void cleanupExpiredPendingResponses() {
		log.debug("Starting cleanup of expired pending responses");

		ConcurrentHashMap<String, CompletableFuture<TrainerWorkloadResponseMessage>> pendingResponses = messagingAdapter
		        .getPendingResponses();

		if (pendingResponses.isEmpty()) {
			log.debug("No pending responses to clean up");
			return;
		}

		int expiredCount = 0;
		for (Map.Entry<String, CompletableFuture<TrainerWorkloadResponseMessage>> entry : pendingResponses.entrySet()) {
			String transactionId = entry.getKey();
			CompletableFuture<TrainerWorkloadResponseMessage> future = entry.getValue();

			if (future.isDone() || future.isCancelled() || future.isCompletedExceptionally()) {
				pendingResponses.remove(transactionId);
				expiredCount++;
				log.debug("Removed completed/cancelled future for transaction ID: {}", transactionId);
			}
		}

		log.info("Cleaned up {} expired pending responses", expiredCount);
	}
}
