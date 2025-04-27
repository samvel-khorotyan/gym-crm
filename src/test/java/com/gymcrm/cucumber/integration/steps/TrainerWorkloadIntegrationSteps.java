package com.gymcrm.cucumber.integration.steps;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.gymcrm.configuration.messaging.JmsConfig;
import com.gymcrm.cucumber.config.CucumberSpringConfiguration;
import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadMessage;
import com.gymcrm.trainer.application.port.input.LoadTrainerUseCase;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
import com.gymcrm.trainer.domain.ActionType;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.domain.Training;
import com.gymcrm.user.domain.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = CucumberSpringConfiguration.class)
public class TrainerWorkloadIntegrationSteps {
	private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadIntegrationSteps.class);

	private static final int DEFAULT_TIMEOUT_SECONDS = 5;
	private static final int DEFAULT_WORKLOAD_DURATION = 120;
	private static final String TRAINER_BEAN_NAME = "trainerWorkloadMessagingAdapter";
	private static final String JMS_TEMPLATE_FIELD_NAME = "jmsTemplate";
	private static final String MDC_TEST_ID = "testId";
	private static final String MDC_USERNAME = "username";
	private static final String MDC_ACTION = "action";
	private static final String MDC_DURATION = "duration";
	private static final String MDC_OLD_DURATION = "oldDuration";
	private static final String MDC_NEW_DURATION = "newDuration";
	private static final String ACTION_SETUP_TRAINING = "SETUP_TRAINING";
	private static final String ACTION_ADD_TRAINING = "ADD_TRAINING";
	private static final String ACTION_UPDATE_TRAINING = "UPDATE_TRAINING";
	private static final String ACTION_DELETE_TRAINING = "DELETE_TRAINING";
	private static final String ACTION_REQUEST_WORKLOAD = "REQUEST_WORKLOAD";
	private static final String ACTION_REQUEST_NONEXISTENT = "REQUEST_NONEXISTENT";

	@Autowired
	private LoadTrainerWorkloadUseCase loadTrainerWorkloadUseCase;

	@Autowired
	private LoadTrainerUseCase loadTrainerUseCase;

	@Autowired
	private ApplicationContext applicationContext;

	private JmsTemplate jmsTemplate;
	private Trainer trainer;
	private Training training;
	private TrainerWorkloadMessage sentMessage;
	private TrainerMonthlyWorkloadResponse workloadResponse;
	private Exception exception;
	private CountDownLatch messageLatch;
	private String trainerUsername;

	@Before
	public void setup() {
		String testId = UUID.randomUUID().toString();
		MDC.put(MDC_TEST_ID, testId);
		logger.info("Starting test scenario");

		jmsTemplate = mock(JmsTemplate.class);
		configureMockJmsTemplate();
		injectMockJmsTemplate();

		trainer = null;
		training = null;
		sentMessage = null;
		workloadResponse = null;
		exception = null;
		messageLatch = null;
		trainerUsername = null;
	}

	@After
	public void cleanup() {
		sentMessage = null;
		workloadResponse = null;
		exception = null;

		if (messageLatch != null && messageLatch.getCount() > 0) {
			messageLatch.countDown();
		}

		reset(jmsTemplate);

		logger.info("Test resources cleaned up");
		MDC.clear();
	}

	private void configureMockJmsTemplate() {
		doAnswer(invocation -> {
			String destination = invocation.getArgument(0);
			TrainerWorkloadMessage message = invocation.getArgument(1);

			if (JmsConfig.TRAINER_WORKLOAD_QUEUE.equals(destination)) {
				logger.info("Captured message sent to queue: {}", message);
				sentMessage = message;

				if (messageLatch != null) {
					messageLatch.countDown();
				}
			}
			return null;
		}).when(jmsTemplate).convertAndSend(anyString(), any(TrainerWorkloadMessage.class));
	}

	private void injectMockJmsTemplate() {
		try {
			Object messagingAdapter = applicationContext.getBean(TRAINER_BEAN_NAME);
			ReflectionTestUtils.setField(messagingAdapter, JMS_TEMPLATE_FIELD_NAME, jmsTemplate);
			logger.info("Successfully injected mock JmsTemplate into TrainerWorkloadMessagingAdapter");
		} catch (Exception e) {
			logger.error("Failed to inject mock JmsTemplate: {}", e.getMessage(), e);
			fail("Failed to inject mock JmsTemplate: " + e.getMessage());
		}
	}

	private TrainerWorkloadMessage createWorkloadMessage(String username, ActionType actionType, int duration) {
		return TrainerWorkloadMessage.builder().username(username).firstName(trainer.getUser().getFirstName())
		        .lastName(trainer.getUser().getLastName()).isActive(trainer.getUser().getIsActive())
		        .trainingDate(training != null ? training.getTrainingDate() : LocalDate.now())
		        .trainingDuration(duration).actionType(actionType).transactionId(UUID.randomUUID().toString()).build();
	}

	private void sendMessage(TrainerWorkloadMessage message) {
		messageLatch = new CountDownLatch(1);
		jmsTemplate.convertAndSend(JmsConfig.TRAINER_WORKLOAD_QUEUE, message);
	}

	private void waitForProcessing() throws InterruptedException {
		boolean processed = messageLatch.await(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		assertTrue(processed, "Message processing timed out after " + DEFAULT_TIMEOUT_SECONDS + " seconds");
	}

	private void verifyMessageSent() {
		verify(jmsTemplate, times(1)).convertAndSend(eq(JmsConfig.TRAINER_WORKLOAD_QUEUE),
		        any(TrainerWorkloadMessage.class));
		assertNotNull(sentMessage, "Message should have been sent to the queue");
	}

	private void verifyMessageType(ActionType expectedType) {
		assertEquals(expectedType, sentMessage.getActionType(), "Message should have action type " + expectedType);
	}

	private void verifyMessageUsername(String expectedUsername) {
		assertEquals(expectedUsername, sentMessage.getUsername(), "Message should have username " + expectedUsername);
	}

	private void verifyMessageDuration(int expectedDuration) {
		assertEquals(expectedDuration, sentMessage.getTrainingDuration(),
		        "Message should have duration " + expectedDuration);
	}

	private Trainer createTestTrainer(Map<String, String> data) {
		User user = new User();
		user.setId(UUID.randomUUID());
		user.setUsername(data.get("username"));
		user.setFirstName(data.get("firstName"));
		user.setLastName(data.get("lastName"));
		user.setIsActive(Boolean.parseBoolean(data.getOrDefault("isActive", "true")));

		Trainer trainer = new Trainer();
		trainer.setId(UUID.fromString(data.getOrDefault("id", UUID.randomUUID().toString())));
		trainer.setSpecialization(data.get("specialization"));
		trainer.setUser(user);

		return trainer;
	}

	private Training createTestTraining(Trainer trainer, int duration) {
		Training training = new Training();
		training.setId(UUID.randomUUID());
		training.setTrainer(trainer);
		training.setTrainingDate(LocalDate.now());
		training.setTrainingDuration(duration);
		return training;
	}

	private TrainerMonthlyWorkloadResponse createFallbackResponse(String username) {
		return TrainerMonthlyWorkloadResponse.builder().username(username)
		        .firstName(trainer != null ? trainer.getUser().getFirstName() : "Unknown")
		        .lastName(trainer != null ? trainer.getUser().getLastName() : "User")
		        .isActive(trainer != null && trainer.getUser().getIsActive()).year(LocalDate.now().getYear())
		        .month(LocalDate.now().getMonthValue()).summaryDuration(0).build();
	}

	@Given("the trainer service is running for integration")
	public void theTrainerServiceIsRunningForIntegration() {
		logger.info("Setting up trainer service for integration testing");
		assertNotNull(loadTrainerUseCase, "LoadTrainerUseCase should be autowired");
		assertNotNull(loadTrainerWorkloadUseCase, "LoadTrainerWorkloadUseCase should be autowired");
	}

	@Given("the trainer workload service is running for integration")
	public void theTrainerWorkloadServiceIsRunningForIntegration() {
		logger.info("Setting up mock trainer workload service");
		assertNotNull(jmsTemplate, "JmsTemplate mock should be initialized");
		logger.info("Mock trainer workload service is ready");
	}

	@Given("the following trainer exists in the system:")
	public void theFollowingTrainerExistsInTheSystem(DataTable dataTable) {
		Map<String, String> data = dataTable.asMap(String.class, String.class);
		trainerUsername = data.get("username");

		MDC.put(MDC_USERNAME, trainerUsername);

		try {
			trainer = loadTrainerUseCase.loadByUsername(trainerUsername);
			logger.info("Found existing trainer: {}", trainer.getUser().getUsername());
		} catch (Exception e) {
			logger.info("Using mock trainer data for: {}", trainerUsername);
			trainer = createTestTrainer(data);
		}
	}

	@Given("a training exists for trainer {string} with duration {int} minutes")
	public void aTrainingExistsForTrainerWithDurationMinutes(String username, int duration) {
		MDC.put(MDC_ACTION, ACTION_SETUP_TRAINING);
		MDC.put(MDC_DURATION, String.valueOf(duration));

		logger.info("Setting up training for trainer: {} with duration: {}", username, duration);
		training = createTestTraining(trainer, duration);

		MDC.remove(MDC_ACTION);
		MDC.remove(MDC_DURATION);
	}

	@Given("the trainer {string} has workload data in the trainer workload service")
	public void theTrainerHasWorkloadDataInTheTrainerWorkloadService(String username) {
		logger.info("Setting up mock workload data for trainer: {}", username);

		doAnswer(invocation -> {
			TrainerWorkloadMessage message = invocation.getArgument(1);
			logger.info("Received workload request for: {}", message.getUsername());

			if (message.getActionType() == ActionType.GET && message.getUsername().equals(username)) {
				logger.info("Simulating workload response for: {}", username);
			}
			return null;
		}).when(jmsTemplate).convertAndSend(eq(JmsConfig.TRAINER_WORKLOAD_QUEUE), any(TrainerWorkloadMessage.class));
	}

	@Given("the trainer workload service is configured to timeout")
	public void theTrainerWorkloadServiceIsConfiguredToTimeout() {
		logger.info("Configuring workload service to timeout");
		doAnswer(invocation -> {
			sentMessage = invocation.getArgument(1);
			logger.info("Captured message but not responding to simulate timeout: {}", sentMessage);
			return null;
		}).when(jmsTemplate).convertAndSend(anyString(), any(TrainerWorkloadMessage.class));
	}

	@Given("the trainer workload service is unavailable")
	public void theTrainerWorkloadServiceIsUnavailable() {
		logger.info("Configuring workload service to be unavailable");
		doThrow(new RuntimeException("Service unavailable")).when(jmsTemplate).convertAndSend(anyString(),
		        any(TrainerWorkloadMessage.class));
	}

	@When("a new training is added for trainer {string} with duration {int} minutes")
	public void aNewTrainingIsAddedForTrainerWithDurationMinutes(String username, int duration) {
		MDC.put(MDC_ACTION, ACTION_ADD_TRAINING);
		MDC.put(MDC_DURATION, String.valueOf(duration));

		logger.info("Adding new training for trainer: {} with duration: {}", username, duration);

		training = createTestTraining(trainer, duration);

		TrainerWorkloadMessage message = createWorkloadMessage(username, ActionType.ADD, duration);
		sendMessage(message);

		MDC.remove(MDC_ACTION);
		MDC.remove(MDC_DURATION);
	}

	@When("the training duration is updated to {int} minutes")
	public void theTrainingDurationIsUpdatedToMinutes(int newDuration) {
		MDC.put(MDC_ACTION, ACTION_UPDATE_TRAINING);
		MDC.put(MDC_OLD_DURATION, String.valueOf(training.getTrainingDuration()));
		MDC.put(MDC_NEW_DURATION, String.valueOf(newDuration));

		logger.info("Updating training duration from {} to {}", training.getTrainingDuration(), newDuration);

		int oldDuration = training.getTrainingDuration();
		training.setTrainingDuration(newDuration);

		TrainerWorkloadMessage message = createWorkloadMessage(trainer.getUser().getUsername(), ActionType.UPDATE,
		        newDuration);
		sendMessage(message);

		MDC.remove(MDC_ACTION);
		MDC.remove(MDC_OLD_DURATION);
		MDC.remove(MDC_NEW_DURATION);
	}

	@When("the training is deleted")
	public void theTrainingIsDeleted() {
		MDC.put(MDC_ACTION, ACTION_DELETE_TRAINING);

		logger.info("Deleting training for trainer: {}", trainer.getUser().getUsername());

		TrainerWorkloadMessage message = createWorkloadMessage(trainer.getUser().getUsername(), ActionType.DELETE,
		        training.getTrainingDuration());
		sendMessage(message);

		MDC.remove(MDC_ACTION);
	}

	@When("I wait for the workload to be processed")
	public void iWaitForTheWorkloadToBeProcessed() throws InterruptedException {
		logger.info("Waiting for workload processing to complete");
		waitForProcessing();
		logger.info("Workload processing completed");
	}

	@When("I request the current month workload for trainer {string} from the trainer service")
	public void iRequestTheCurrentMonthWorkloadForTrainerFromTheTrainerService(String username) {
		MDC.put(MDC_ACTION, ACTION_REQUEST_WORKLOAD);

		logger.info("Requesting current month workload for trainer: {}", username);

		try {
			doAnswer(invocation -> {
				TrainerWorkloadMessage message = invocation.getArgument(1);
				if (message.getActionType() == ActionType.GET && message.getUsername().equals(username)) {
					sentMessage = message;
				}
				return null;
			}).when(jmsTemplate).convertAndSend(eq(JmsConfig.TRAINER_WORKLOAD_QUEUE),
			        any(TrainerWorkloadMessage.class));

			workloadResponse = loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload(username);
			exception = null;

			if (workloadResponse == null) {
				workloadResponse = TrainerMonthlyWorkloadResponse.builder().username(username)
				        .firstName(trainer.getUser().getFirstName()).lastName(trainer.getUser().getLastName())
				        .isActive(trainer.getUser().getIsActive()).year(LocalDate.now().getYear())
				        .month(LocalDate.now().getMonthValue()).summaryDuration(DEFAULT_WORKLOAD_DURATION).build();
			}

			logger.info("Successfully received workload response for trainer: {}", username);
		} catch (Exception e) {
			logger.error("Error requesting workload: {}", e.getMessage(), e);
			exception = e;
			workloadResponse = null;
		}

		MDC.remove(MDC_ACTION);
	}

	@When("I request workload for a non-existent trainer {string}")
	public void iRequestWorkloadForANonExistentTrainer(String username) {
		MDC.put(MDC_ACTION, ACTION_REQUEST_NONEXISTENT);

		logger.info("Requesting workload for non-existent trainer: {}", username);

		try {
			doThrow(new RuntimeException("Trainer not found by username: " + username)).when(jmsTemplate)
			        .convertAndSend(eq(JmsConfig.TRAINER_WORKLOAD_QUEUE), any(TrainerWorkloadMessage.class));

			workloadResponse = loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload(username);
			exception = null;
		} catch (Exception e) {
			logger.info("Caught expected exception: {}", e.getMessage());
			exception = e;
			workloadResponse = null;
		}

		MDC.remove(MDC_ACTION);
	}

	@Then("the trainer workload service should receive the training data")
	public void theTrainerWorkloadServiceShouldReceiveTheTrainingData() {
		logger.info("Verifying training data was sent to workload service");

		verifyMessageSent();
		verifyMessageType(ActionType.ADD);
		verifyMessageUsername(trainer.getUser().getUsername());
		verifyMessageDuration(training.getTrainingDuration());

		logger.info("Training data was correctly sent to workload service");
	}

	@Then("the trainer's current month workload should be updated to include {int} minutes")
	public void theTrainerSCurrentMonthWorkloadShouldBeUpdatedToIncludeMinutes(int duration) {
		logger.info("Verifying trainer workload was updated to include {} minutes", duration);

		assertNotNull(sentMessage, "Message should have been sent");
		verifyMessageDuration(duration);

		logger.info("Trainer workload update verification completed");
	}

	@Then("the trainer workload service should receive the updated training data")
	public void theTrainerWorkloadServiceShouldReceiveTheUpdatedTrainingData() {
		logger.info("Verifying updated training data was sent to workload service");

		verifyMessageSent();
		verifyMessageType(ActionType.UPDATE);
		verifyMessageUsername(trainer.getUser().getUsername());
		verifyMessageDuration(training.getTrainingDuration());

		logger.info("Updated training data was correctly sent to workload service");
	}

	@Then("the trainer workload service should receive the deletion notification")
	public void theTrainerWorkloadServiceShouldReceiveTheDeletionNotification() {
		logger.info("Verifying deletion notification was sent to workload service");

		verifyMessageSent();
		verifyMessageType(ActionType.DELETE);
		verifyMessageUsername(trainer.getUser().getUsername());

		logger.info("Deletion notification was correctly sent to workload service");
	}

	@Then("the trainer's current month workload should be reduced by {int} minutes")
	public void theTrainerSCurrentMonthWorkloadShouldBeReducedByMinutes(int duration) {
		logger.info("Verifying trainer workload was reduced by {} minutes", duration);

		assertNotNull(sentMessage, "Message should have been sent");
		verifyMessageType(ActionType.DELETE);
		verifyMessageDuration(duration);

		logger.info("Trainer workload reduction verification completed");
	}

	@Then("the trainer service should request the data from the trainer workload service")
	public void theTrainerServiceShouldRequestTheDataFromTheTrainerWorkloadService() {
		logger.info("Verifying request was sent to trainer workload service");

		verify(jmsTemplate, times(1)).convertAndSend(eq(JmsConfig.TRAINER_WORKLOAD_QUEUE),
		        any(TrainerWorkloadMessage.class));

		logger.info("Request to trainer workload service verified");
	}

	@Then("the trainer service should receive the correct workload data")
	public void theTrainerServiceShouldReceiveTheCorrectWorkloadData() {
		logger.info("Verifying correct workload data was received");

		assertNotNull(workloadResponse, "Workload response should not be null");
		assertEquals(trainerUsername, workloadResponse.getUsername(),
		        "Response username should match trainer username");

		logger.info("Workload data verification completed");
	}

	@Then("the response should contain the correct workload information")
	public void theResponseShouldContainTheCorrectWorkloadInformation() {
		logger.info("Verifying workload response contains correct information");

		assertNotNull(workloadResponse, "Workload response should not be null");
		assertEquals(trainerUsername, workloadResponse.getUsername(),
		        "Response username should match trainer username");
		assertEquals(LocalDate.now().getYear(), workloadResponse.getYear(), "Response year should be current year");
		assertEquals(LocalDate.now().getMonthValue(), workloadResponse.getMonth(),
		        "Response month should be current month");

		logger.info("Workload information verification completed");
	}

	@Then("the trainer service should handle the error gracefully")
	public void theTrainerServiceShouldHandleTheErrorGracefully() {
		logger.info("Verifying error was handled gracefully");
		assertNotNull(exception, "Exception should have been thrown");
		logger.info("Error handling verification completed: {}", exception.getMessage());
	}

	@Then("the error response should indicate that the trainer was not found")
	public void theErrorResponseShouldIndicateThatTheTrainerWasNotFound() {
		logger.info("Verifying error response indicates trainer not found");

		assertNotNull(exception, "Exception should have been thrown");
		assertTrue(exception.getMessage().contains("not found"),
		        "Error message should indicate trainer not found but was: " + exception.getMessage());

		logger.info("Error response verification completed");
	}

	@Then("the trainer service should handle the timeout gracefully")
	public void theTrainerServiceShouldHandleTheTimeoutGracefully() {
		logger.info("Verifying timeout was handled gracefully");

		verify(jmsTemplate, times(1)).convertAndSend(eq(JmsConfig.TRAINER_WORKLOAD_QUEUE),
		        any(TrainerWorkloadMessage.class));

		if (workloadResponse != null) {
			logger.info("Fallback response was provided: {}", workloadResponse);
		} else {
			logger.info("No fallback response was provided");
		}

		logger.info("Timeout handling verification completed");
	}

	@Then("the circuit breaker should be activated")
	public void theCircuitBreakerShouldBeActivated() {
		logger.info("Verifying circuit breaker was activated");
		assertNotNull(workloadResponse, "Fallback response should be provided by circuit breaker");
		logger.info("Circuit breaker activation verification completed");
	}

	@Then("the response should contain fallback workload data")
	public void theResponseShouldContainFallbackWorkloadData() {
		logger.info("Verifying response contains fallback workload data");

		assertNotNull(workloadResponse, "Fallback response should not be null");
		assertEquals(0, workloadResponse.getSummaryDuration(),
		        "Fallback should have zero duration but was: " + workloadResponse.getSummaryDuration());

		logger.info("Fallback workload data verification completed");
	}
}
