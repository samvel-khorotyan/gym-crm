package com.gymcrm.configuration.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.configuration.messaging.metrics.SqsMetrics; // Changed from JmsMetrics
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterQueueSqsListener {
	private final SqsMetrics sqsMetrics; // Changed from JmsMetrics
	private final ObjectMapper objectMapper;

	@SqsListener("${aws.sqs.dead-letter-queue}")
	public void handleDeadLetterMessage(String message) {
		log.error("Received message in Dead Letter Queue: {}", message);
		sqsMetrics.recordMessageReceived(); // Changed from jmsMetrics
		sqsMetrics.recordMessageFailed(); // Changed from jmsMetrics

		try {
			// Attempt to log more details about the message
			Object jsonMessage = objectMapper.readValue(message, Object.class);
			log.error("Dead Letter Message details: {}", jsonMessage);
		} catch (Exception e) {
			log.error("Could not parse Dead Letter message: {}", e.getMessage());
		}
	}
}
