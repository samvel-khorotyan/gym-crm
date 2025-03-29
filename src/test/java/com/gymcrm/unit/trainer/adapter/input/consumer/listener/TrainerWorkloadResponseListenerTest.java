package com.gymcrm.unit.trainer.adapter.input.consumer.listener;

import static org.mockito.Mockito.*;

import com.gymcrm.configuration.messaging.metrics.JmsMetrics;
import com.gymcrm.trainer.adapter.input.consumer.listener.TrainerWorkloadResponseListener;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadResponseMessage;
import com.gymcrm.trainer.application.port.output.ReceiveTrainerWorkloadResponsePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadResponseListenerTest {
	@Mock
	private ReceiveTrainerWorkloadResponsePort receiveTrainerWorkloadResponsePort;

	@Mock
	private JmsMetrics jmsMetrics;

	@InjectMocks
	private TrainerWorkloadResponseListener listener;

	private TrainerWorkloadResponseMessage responseMessage;

	@BeforeEach
	void setUp() {
		responseMessage = TrainerWorkloadResponseMessage.builder().transactionId("12345").username("trainer1")
		        .firstName("John").lastName("Doe").isActive(true).year(2025).month(3).summaryDuration(120).build();
	}

	@Test
	void handleWorkloadResponse_SuccessfulProcessing_ShouldProcessMessage() {
		listener.handleWorkloadResponse(responseMessage);

		verify(jmsMetrics).recordMessageReceived();
		verify(receiveTrainerWorkloadResponsePort).handleWorkloadResponse(responseMessage);
		verify(jmsMetrics).recordProcessingTime(anyLong());
		verify(jmsMetrics, never()).recordMessageFailed();
	}

	@Test
	void handleWorkloadResponse_WhenExceptionThrown_ShouldHandleException() {
		doThrow(new RuntimeException("Processing error")).when(receiveTrainerWorkloadResponsePort)
		        .handleWorkloadResponse(responseMessage);

		listener.handleWorkloadResponse(responseMessage);

		verify(jmsMetrics).recordMessageReceived();
		verify(receiveTrainerWorkloadResponsePort).handleWorkloadResponse(responseMessage);
		verify(jmsMetrics).recordMessageFailed();
		verify(jmsMetrics).recordProcessingTime(anyLong());
	}
}
