package com.gymcrm.trainer.adapter.input.consumer.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.configuration.messaging.metrics.SqsMetrics;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadResponseMessage;
import com.gymcrm.trainer.application.port.output.ReceiveTrainerWorkloadResponsePort;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadSqsResponseListener {
	private final ReceiveTrainerWorkloadResponsePort receiveTrainerWorkloadResponsePort;
	private final SqsMetrics sqsMetrics; // Changed from JmsMetrics
	private final ObjectMapper objectMapper;

	@SqsListener("${aws.sqs.trainer-workload-response-queue}")
	public void handleWorkloadResponse(String message) {
		log.info("Received SQS message: {}", message);

		long startTime = System.currentTimeMillis();
		try {
			TrainerWorkloadResponseMessage response = objectMapper.readValue(message,
			        TrainerWorkloadResponseMessage.class);
			log.info("Received workload response for transaction ID: {}", response.getTransactionId());

			sqsMetrics.recordMessageReceived(); // Changed from jmsMetrics
			receiveTrainerWorkloadResponsePort.handleWorkloadResponse(response);
		} catch (Exception e) {
			sqsMetrics.recordMessageFailed(); // Changed from jmsMetrics
			log.error("Error processing workload response: {}", e.getMessage(), e);
		} finally {
			long processingTime = System.currentTimeMillis() - startTime;
			sqsMetrics.recordProcessingTime(processingTime); // Changed from jmsMetrics
		}
	}
}
