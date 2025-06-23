package com.gymcrm.configuration.messaging.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsMessageTemplate {
	private final QueueMessagingTemplate queueMessagingTemplate;
	private final ObjectMapper objectMapper;

	@Value("${aws.sqs.trainer-workload-queue}")
	private String trainerWorkloadQueue;

	@Value("${aws.sqs.trainer-workload-response-queue}")
	private String trainerWorkloadResponseQueue;

	public void sendToTrainerWorkloadQueue(Object message) {
		try {
			String jsonMessage = objectMapper.writeValueAsString(message);
			queueMessagingTemplate.send(trainerWorkloadQueue, MessageBuilder.withPayload(jsonMessage).build());
			log.info("Successfully sent message to trainer workload queue");
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize message: {}", e.getMessage(), e);
			throw new RuntimeException("Message serialization failed", e);
		} catch (Exception e) {
			log.error("Failed to send message to SQS: {}", e.getMessage(), e);
			throw new RuntimeException("SQS send failed", e);
		}
	}

	public void sendToResponseQueue(Object message) {
		try {
			String jsonMessage = objectMapper.writeValueAsString(message);
			queueMessagingTemplate.send(trainerWorkloadResponseQueue, MessageBuilder.withPayload(jsonMessage).build());
			log.info("Successfully sent response message to queue");
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize response message: {}", e.getMessage(), e);
			throw new RuntimeException("Response message serialization failed", e);
		} catch (Exception e) {
			log.error("Failed to send response to SQS: {}", e.getMessage(), e);
			throw new RuntimeException("SQS response send failed", e);
		}
	}
}
