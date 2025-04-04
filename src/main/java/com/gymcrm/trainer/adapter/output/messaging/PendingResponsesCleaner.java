package com.gymcrm.trainer.adapter.output.messaging;

import com.gymcrm.trainer.application.port.output.ResponseCleanupPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingResponsesCleaner {
	private final ResponseCleanupPort responseCleanupPort;

	@Scheduled(fixedRate = 60000)
	public void cleanupExpiredPendingResponses() {
		log.debug("Starting cleanup of expired pending responses");

		int expiredCount = responseCleanupPort.cleanupExpiredResponses();

		log.info("Cleaned up {} expired pending responses", expiredCount);
	}
}
