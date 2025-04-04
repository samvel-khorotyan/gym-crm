package com.gymcrm.unit.trainer.adapter.output.messaging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.gymcrm.configuration.messaging.JmsConfig;
import com.gymcrm.configuration.messaging.metrics.JmsMetrics;
import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.adapter.output.messaging.TrainerWorkloadMessagingAdapter;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadMessage;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadResponseMessage;
import com.gymcrm.trainer.domain.ActionType;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.domain.Training;
import com.gymcrm.user.domain.User;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadMessagingAdapterTest {
	@Mock
	private JmsTemplate jmsTemplate;

	@Mock
	private JmsMetrics jmsMetrics;

	@InjectMocks
	private TrainerWorkloadMessagingAdapter adapter;

	private Training training;
	private Trainer trainer;
	private String transactionId;
	private TrainerWorkloadMessage message;
	private TrainerWorkloadResponseMessage responseMessage;

	@BeforeEach
	void setUp() {
		transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		trainer = new Trainer();
		trainer.setId(UUID.randomUUID());
		trainer.setUser(new User());
		trainer.getUser().setUsername("trainer1");
		trainer.getUser().setFirstName("John");
		trainer.getUser().setLastName("Doe");
		trainer.getUser().setIsActive(true);

		training = new Training();
		training.setId(UUID.randomUUID());
		training.setTrainingName("Strength Training");
		training.setTrainer(trainer);
		training.setTrainingDate(LocalDate.now());
		training.setTrainingDuration(60);

		message = TrainerWorkloadMessage.builder().username("trainer1").firstName("John").lastName("Doe").isActive(true)
		        .trainingDate(LocalDate.now()).trainingDuration(60).actionType(ActionType.ADD)
		        .transactionId(transactionId).build();

		responseMessage = TrainerWorkloadResponseMessage.builder().username("trainer1").firstName("John")
		        .lastName("Doe").isActive(true).year(2025).month(3).summaryDuration(120).transactionId(transactionId)
		        .build();
	}

	@Test
	void sendTrainerWorkload_SuccessfulSend_ShouldSendMessage() {
		adapter.sendTrainerWorkload(training, ActionType.ADD);

		verify(jmsTemplate).convertAndSend(eq(JmsConfig.TRAINER_WORKLOAD_QUEUE), any(TrainerWorkloadMessage.class));
		verify(jmsMetrics).recordMessageSent();
		verify(jmsMetrics).recordProcessingTime(anyLong());
	}

	@Test
	void handleWorkloadResponse_WhenFutureDoesNotExist_ShouldLogWarning() {
		adapter.handleWorkloadResponse(responseMessage);
		verify(jmsMetrics, never()).recordMessageReceived();
	}

	@Test
	void ensureTransactionId_WhenTransactionIdExists_ShouldReturnExistingId() throws Exception {
		Method method = TrainerWorkloadMessagingAdapter.class.getDeclaredMethod("ensureTransactionId");
		method.setAccessible(true);

		String result = (String) method.invoke(adapter);

		assertEquals(transactionId, result);
	}

	@Test
	void ensureTransactionId_WhenTransactionIdDoesNotExist_ShouldGenerateNewId() throws Exception {
		MDC.clear();

		Method method = TrainerWorkloadMessagingAdapter.class.getDeclaredMethod("ensureTransactionId");
		method.setAccessible(true);

		String result = (String) method.invoke(adapter);

		assertNotNull(result);
		assertEquals(result, MDC.get("transactionId"));
	}

	@Test
	void createTrainerWorkloadMessage_ShouldCreateMessage() throws Exception {
		Method method = TrainerWorkloadMessagingAdapter.class.getDeclaredMethod("createTrainerWorkloadMessage",
		        Training.class, Trainer.class, ActionType.class, String.class);
		method.setAccessible(true);

		TrainerWorkloadMessage result = (TrainerWorkloadMessage) method.invoke(adapter, training, trainer,
		        ActionType.ADD, transactionId);

		assertEquals("trainer1", result.getUsername());
		assertEquals("John", result.getFirstName());
		assertEquals("Doe", result.getLastName());
		assertTrue(result.getIsActive());
		assertEquals(training.getTrainingDate(), result.getTrainingDate());
		assertEquals(training.getTrainingDuration(), result.getTrainingDuration());
		assertEquals(ActionType.ADD, result.getActionType());
		assertEquals(transactionId, result.getTransactionId());
	}

	@Test
	void sendMessage_SuccessfulSend_ShouldSendMessage() throws Exception {
		Method method = TrainerWorkloadMessagingAdapter.class.getDeclaredMethod("sendMessage",
		        TrainerWorkloadMessage.class, String.class);
		method.setAccessible(true);

		method.invoke(adapter, message, transactionId);

		verify(jmsTemplate).convertAndSend(eq(JmsConfig.TRAINER_WORKLOAD_QUEUE), eq(message));
		verify(jmsMetrics).recordMessageSent();
		verify(jmsMetrics).recordProcessingTime(anyLong());
	}

	@Test
	void sendGetWorkloadRequest_ShouldSendMessage() throws Exception {
		Method method = TrainerWorkloadMessagingAdapter.class.getDeclaredMethod("sendGetWorkloadRequest", String.class,
		        int.class, int.class, String.class);
		method.setAccessible(true);

		method.invoke(adapter, "trainer1", 2025, 3, transactionId);

		verify(jmsTemplate).convertAndSend(eq(JmsConfig.TRAINER_WORKLOAD_QUEUE), any(TrainerWorkloadMessage.class));
		verify(jmsMetrics).recordMessageSent();
	}

	@Test
	void waitForResponse_SuccessfulResponse_ShouldReturnResponse() throws Exception {
		CompletableFuture<TrainerWorkloadResponseMessage> future = new CompletableFuture<>();
		future.complete(responseMessage);

		Method method = TrainerWorkloadMessagingAdapter.class.getDeclaredMethod("waitForResponse",
		        CompletableFuture.class, String.class);
		method.setAccessible(true);

		TrainerWorkloadResponseMessage result = (TrainerWorkloadResponseMessage) method.invoke(adapter, future,
		        transactionId);

		assertEquals(responseMessage, result);
	}

	@Test
	void handleWorkloadRequestException_WithTimeoutException_ShouldRecordFailure() throws Exception {
		TimeoutException e = new TimeoutException("Timeout");

		Method method = TrainerWorkloadMessagingAdapter.class.getDeclaredMethod("handleWorkloadRequestException",
		        Exception.class, String.class);
		method.setAccessible(true);

		method.invoke(adapter, e, transactionId);

		verify(jmsMetrics).recordMessageFailed();
	}

	@Test
	void handleWorkloadRequestException_WithOtherException_ShouldRecordFailure() throws Exception {
		Exception e = new Exception("Other error");

		Method method = TrainerWorkloadMessagingAdapter.class.getDeclaredMethod("handleWorkloadRequestException",
		        Exception.class, String.class);
		method.setAccessible(true);

		method.invoke(adapter, e, transactionId);

		verify(jmsMetrics).recordMessageFailed();
	}

	@Test
	void mapToWorkloadResponse_ShouldMapResponse() throws Exception {
		Method method = TrainerWorkloadMessagingAdapter.class.getDeclaredMethod("mapToWorkloadResponse",
		        TrainerWorkloadResponseMessage.class);
		method.setAccessible(true);

		TrainerMonthlyWorkloadResponse result = (TrainerMonthlyWorkloadResponse) method.invoke(adapter,
		        responseMessage);

		assertEquals("trainer1", result.getUsername());
		assertEquals("John", result.getFirstName());
		assertEquals("Doe", result.getLastName());
		assertTrue(result.getIsActive());
		assertEquals(2025, result.getYear());
		assertEquals(3, result.getMonth());
		assertEquals(120, result.getSummaryDuration());
	}
}
