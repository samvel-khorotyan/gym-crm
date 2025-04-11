package com.gymcrm.trainer.adapter.input.consumer.listener;

import com.gymcrm.configuration.messaging.JmsConfig;
import com.gymcrm.configuration.messaging.metrics.JmsMetrics;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadResponseMessage;
import com.gymcrm.trainer.application.port.output.ReceiveTrainerWorkloadResponsePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadResponseListener {
	private final ReceiveTrainerWorkloadResponsePort receiveTrainerWorkloadResponsePort;
	private final JmsMetrics jmsMetrics;

	@JmsListener(destination = JmsConfig.TRAINER_WORKLOAD_RESPONSE_QUEUE)
	public void handleWorkloadResponse(TrainerWorkloadResponseMessage response) {
		log.info("Received workload response for transaction ID: {}", response.getTransactionId());

		long startTime = System.currentTimeMillis();
		try {
			jmsMetrics.recordMessageReceived();
			receiveTrainerWorkloadResponsePort.handleWorkloadResponse(response);
		} catch (Exception e) {
			jmsMetrics.recordMessageFailed();
			log.error("Error processing workload response: {}", e.getMessage(), e);
		} finally {
			long processingTime = System.currentTimeMillis() - startTime;
			jmsMetrics.recordProcessingTime(processingTime);
		}
	}
}
