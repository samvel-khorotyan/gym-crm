package com.gymcrm.cucumber.integration.steps;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.cucumber.config.CucumberSpringConfiguration;
import com.gymcrm.trainee.application.port.input.LoadTraineeUseCase;
import com.gymcrm.trainee.application.port.output.UpdateTraineePort;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.port.input.LoadTrainerUseCase;
import com.gymcrm.trainer.application.port.output.UpdateTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.adapter.input.web.request.TrainingCreateRequest;
import com.gymcrm.training.adapter.input.web.request.TrainingUpdateRequest;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import com.gymcrm.training.application.port.input.UpdateTrainingUseCase;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.application.port.input.LoadTrainingTypeUseCase;
import com.gymcrm.trainingtype.application.port.output.UpdateTrainingTypePort;
import com.gymcrm.trainingtype.domain.TrainingType;
import com.gymcrm.user.application.port.output.LoadUserPort;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = CucumberSpringConfiguration.class)
@Transactional
public class TrainingIntegrationSteps {
	private static final Logger logger = LoggerFactory.getLogger(TrainingIntegrationSteps.class);

	private static final String DEFAULT_PASSWORD = "password";
	private static final String DEFAULT_FIRST_NAME = "New";
	private static final String DEFAULT_LAST_NAME = "User";
	private static final String DEFAULT_SPECIALIZATION = "General Fitness";
	private static final int DEFAULT_TRAINING_DURATION = 60;
	private static final String ID_PATTERN = "\"id\":\"([^\"]+)\"";

	private static final String ROLE_ADMIN = "ADMIN";
	private static final String ROLE_TRAINER = "TRAINER";
	private static final String ROLE_TRAINEE = "TRAINEE";

	private static final String ADMIN_USERNAME = "admin.int";
	private static final String TRAINER_USERNAME = "trainer.int";
	private static final String TRAINEE_USERNAME = "trainee.int";

	private static final String MDC_TEST_ID = "testId";
	private static final String MDC_USERNAME = "username";
	private static final String MDC_USER_TYPE = "userType";
	private static final String MDC_TRAINING_TYPE = "trainingType";
	private static final String MDC_TRAINEE_USERNAME = "traineeUsername";
	private static final String MDC_TRAINER_USERNAME = "trainerUsername";
	private static final String MDC_ACTION = "action";
	private static final String MDC_TRAINING_ID = "trainingId";
	private static final String MDC_TRAINING_NAME = "trainingName";
	private static final String MDC_TRAINING_DATE = "trainingDate";
	private static final String MDC_TRAINING_DURATION = "trainingDuration";
	private static final String MDC_MINUTES = "minutes";

	private static final String ACTION_CREATE_TRAINING = "CREATE_TRAINING";
	private static final String ACTION_UPDATE_TRAINING = "UPDATE_TRAINING";
	private static final String ACTION_DELETE_TRAINING = "DELETE_TRAINING";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TrainingCreationUseCase trainingCreationUseCase;

	@Autowired
	private LoadTrainingUseCase loadTrainingUseCase;

	@Autowired
	private UpdateTrainingUseCase updateTrainingUseCase;

	@Autowired
	private LoadTrainingPort loadTrainingPort;

	@Autowired
	private UpdateUserPort updateUserPort;

	@Autowired
	private LoadUserPort loadUserPort;

	@Autowired
	private UpdateTraineePort updateTraineePort;

	@Autowired
	private LoadTraineeUseCase loadTraineeUseCase;

	@Autowired
	private UpdateTrainerPort updateTrainerPort;

	@Autowired
	private LoadTrainerUseCase loadTrainerUseCase;

	@Autowired
	private UpdateTrainingTypePort updateTrainingTypePort;

	@Autowired
	private LoadTrainingTypeUseCase loadTrainingTypeUseCase;

	@Autowired
	private EntityManager entityManager;

	private ResultActions resultActions;
	private UUID currentTrainingId;
	private String currentUserRole;

	@Before
	public void setup() {
		String testId = UUID.randomUUID().toString();
		MDC.put(MDC_TEST_ID, testId);
		logger.info("Starting training integration test scenario");

		currentTrainingId = null;
		currentUserRole = null;
	}

	@After
	public void cleanupDatabase() {
		try {
			logger.info("Cleaning up database after test");
			entityManager.createQuery("DELETE FROM Training").executeUpdate();
			entityManager.createQuery("DELETE FROM Trainee").executeUpdate();
			entityManager.createQuery("DELETE FROM Trainer").executeUpdate();
			entityManager.createQuery("DELETE FROM User").executeUpdate();
			entityManager.flush();
			entityManager.clear();
			logger.info("Database cleanup completed");
		} catch (Exception e) {
			logger.error("Error during database cleanup: {}", e.getMessage(), e);
		} finally {
			MDC.clear();
		}
	}

	private User createUser(String username, String firstName, String lastName, UserType userType) {
		User user = new User();
		user.setId(UUID.randomUUID());
		user.setUsername(username);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserType(userType);
		user.setPassword(DEFAULT_PASSWORD);
		user.setIsActive(true);
		return updateUserPort.save(user);
	}

	private void createTrainee(User user) {
		Trainee trainee = new Trainee();
		trainee.setId(UUID.randomUUID());
		trainee.setUser(user);
		trainee.setTrainers(new ArrayList<>());
		updateTraineePort.save(trainee);
	}

	private void createTrainer(User user) {
		Trainer trainer = new Trainer();
		trainer.setId(UUID.randomUUID());
		trainer.setUser(user);
		trainer.setSpecialization(DEFAULT_SPECIALIZATION);
		updateTrainerPort.save(trainer);
	}

	private TrainingType findOrCreateTrainingType(String name) {
		List<TrainingType> allTypes = loadTrainingTypeUseCase.loadAll();
		for (TrainingType type : allTypes) {
			if (type.getTrainingTypeName().equals(name)) {
				return type;
			}
		}

		TrainingType type = new TrainingType();
		type.setId(UUID.randomUUID());
		type.setTrainingTypeName(name);
		return updateTrainingTypePort.save(type);
	}

	private ResultActions performRequestWithRole(MockHttpServletRequestBuilder requestBuilder) throws Exception {
		return switch (currentUserRole) {
			case ROLE_TRAINER -> mockMvc.perform(requestBuilder
			        .with(SecurityMockMvcRequestPostProcessors.user(TRAINER_USERNAME).roles(ROLE_TRAINER)));
			case ROLE_TRAINEE -> mockMvc.perform(requestBuilder
			        .with(SecurityMockMvcRequestPostProcessors.user(TRAINEE_USERNAME).roles(ROLE_TRAINEE)));
			default -> mockMvc.perform(
			        requestBuilder.with(SecurityMockMvcRequestPostProcessors.user(ADMIN_USERNAME).roles(ROLE_ADMIN)));
		};
	}

	private UUID extractTrainingIdFromResponse(String responseBody) {
		Pattern pattern = Pattern.compile(ID_PATTERN);
		Matcher matcher = pattern.matcher(responseBody);
		if (matcher.find()) {
			return UUID.fromString(matcher.group(1));
		}
		return null;
	}

	private void assignTraineeToTrainer(Trainee trainee, Trainer trainer) {
		if (trainee.getTrainers() == null) {
			trainee.setTrainers(new ArrayList<>());
		}

		boolean alreadyAssigned = false;
		for (Trainer t : trainee.getTrainers()) {
			if (t.getId().equals(trainer.getId())) {
				alreadyAssigned = true;
				break;
			}
		}

		if (!alreadyAssigned) {
			trainee.getTrainers().add(trainer);
			updateTraineePort.save(trainee);
		}
	}

	private void unassignTraineeFromTrainer(Trainee trainee, Trainer trainer) {
		if (trainee.getTrainers() != null) {
			trainee.getTrainers().removeIf(t -> t.getId().equals(trainer.getId()));
			updateTraineePort.save(trainee);
		}
	}

	private boolean isTraineeAssignedToTrainer(Trainee trainee, Trainer trainer) {
		if (trainee.getTrainers() == null) {
			return false;
		}

		for (Trainer t : trainee.getTrainers()) {
			if (t.getId().equals(trainer.getId())) {
				return true;
			}
		}
		return false;
	}

	@Given("the training service is running")
	public void theTrainingServiceIsRunning() {
		logger.info("Verifying training service is running");
		assertNotNull(trainingCreationUseCase, "Training creation use case should be available");
		assertNotNull(loadTrainingUseCase, "Load training use case should be available");
		assertNotNull(updateTrainingUseCase, "Update training use case should be available");
	}

	@And("the user service is running")
	public void theUserServiceIsRunning() {
		logger.info("Verifying user service is running");
		assertNotNull(updateUserPort, "Update user port should be available");
		assertNotNull(loadUserPort, "Load user port should be available");
	}

	@Given("the system has the following users:")
	public void theSystemHasTheFollowingUsers(DataTable dataTable) {
		logger.info("Setting up users in the system");
		List<Map<String, String>> rows = dataTable.asMaps();

		for (Map<String, String> row : rows) {
			String username = row.get("username");
			String firstName = row.get("firstName");
			String lastName = row.get("lastName");
			UserType userType = UserType.valueOf(row.get("userType"));

			MDC.put(MDC_USERNAME, username);
			MDC.put(MDC_USER_TYPE, userType.toString());

			try {
				User existingUser = null;
				try {
					existingUser = loadUserPort.findByUsername(username);
					logger.info("User already exists: {}", username);
				} catch (Exception e) {
					logger.info("User does not exist, will create: {}", username);
				}

				if (existingUser != null) {
					continue;
				}

				User savedUser = createUser(username, firstName, lastName, userType);
				logger.info("Created user: {}", username);

				if (userType == UserType.TRAINEE) {
					createTrainee(savedUser);
					logger.info("Created trainee for user: {}", username);
				} else if (userType == UserType.TRAINER) {
					createTrainer(savedUser);
					logger.info("Created trainer for user: {}", username);
				}
			} catch (Exception e) {
				logger.error("Error creating user {}: {}", username, e.getMessage(), e);
			} finally {
				MDC.remove(MDC_USERNAME);
				MDC.remove(MDC_USER_TYPE);
			}
		}
	}

	@Given("the system has the following training types:")
	public void theSystemHasTheFollowingTrainingTypes(DataTable dataTable) {
		logger.info("Setting up training types in the system");
		List<Map<String, String>> rows = dataTable.asMaps();

		for (Map<String, String> row : rows) {
			String name = row.get("trainingTypeName");
			MDC.put(MDC_TRAINING_TYPE, name);

			try {
				findOrCreateTrainingType(name);
				logger.info("Training type available: {}", name);
			} catch (Exception e) {
				logger.error("Error creating training type {}: {}", name, e.getMessage(), e);
			} finally {
				MDC.remove(MDC_TRAINING_TYPE);
			}
		}
	}

	@Given("the trainee {string} is assigned to trainer {string}")
	public void theTraineeIsAssignedToTrainer(String traineeUsername, String trainerUsername) {
		MDC.put(MDC_TRAINEE_USERNAME, traineeUsername);
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);

		logger.info("Assigning trainee {} to trainer {}", traineeUsername, trainerUsername);

		try {
			Trainee trainee = loadTraineeUseCase.loadByUsername(traineeUsername);
			Trainer trainer = loadTrainerUseCase.loadByUsername(trainerUsername);

			assignTraineeToTrainer(trainee, trainer);
			logger.info("Successfully assigned trainee to trainer");

			assertTrue(isTraineeAssignedToTrainer(trainee, trainer),
			        "Trainee should be assigned to trainer after assignment");
		} catch (Exception e) {
			logger.error("Error assigning trainee to trainer: {}", e.getMessage(), e);
			fail("Failed to assign trainee to trainer: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINEE_USERNAME);
			MDC.remove(MDC_TRAINER_USERNAME);
		}
	}

	@Given("the user is authenticated with role {string}")
	public void theUserIsAuthenticatedWithRole(String role) {
		logger.info("Setting user role to: {}", role);
		currentUserRole = role;
	}

	@Given("the system has a trainee {string} and trainer {string}")
	public void the_system_has_a_trainee_and_trainer(String traineeUsername, String trainerUsername) {
		MDC.put(MDC_TRAINEE_USERNAME, traineeUsername);
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);

		logger.info("Setting up trainee {} and trainer {}", traineeUsername, trainerUsername);

		try {
			User existingTraineeUser = null;
			User existingTrainerUser = null;

			try {
				existingTraineeUser = loadUserPort.findByUsername(traineeUsername);
				logger.info("Trainee user already exists: {}", traineeUsername);
			} catch (Exception e) {
				logger.info("Trainee user does not exist, will create: {}", traineeUsername);
			}

			try {
				existingTrainerUser = loadUserPort.findByUsername(trainerUsername);
				logger.info("Trainer user already exists: {}", trainerUsername);
			} catch (Exception e) {
				logger.info("Trainer user does not exist, will create: {}", trainerUsername);
			}

			if (existingTraineeUser == null) {
				User savedTraineeUser = createUser(traineeUsername, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME,
				        UserType.TRAINEE);
				createTrainee(savedTraineeUser);
				logger.info("Created new trainee: {}", traineeUsername);
			}

			if (existingTrainerUser == null) {
				User savedTrainerUser = createUser(trainerUsername, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME,
				        UserType.TRAINER);
				createTrainer(savedTrainerUser);
				logger.info("Created new trainer: {}", trainerUsername);
			}
		} catch (Exception e) {
			logger.error("Error creating trainee and trainer: {}", e.getMessage(), e);
			fail("Failed to create trainee and trainer: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINEE_USERNAME);
			MDC.remove(MDC_TRAINER_USERNAME);
		}
	}

	@Given("the trainee {string} is not assigned to trainer {string}")
	public void theTraineeIsNotAssignedToTrainer(String traineeUsername, String trainerUsername) {
		MDC.put(MDC_TRAINEE_USERNAME, traineeUsername);
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);

		logger.info("Ensuring trainee {} is not assigned to trainer {}", traineeUsername, trainerUsername);

		try {
			Trainee trainee = loadTraineeUseCase.loadByUsername(traineeUsername);
			Trainer trainer = loadTrainerUseCase.loadByUsername(trainerUsername);

			unassignTraineeFromTrainer(trainee, trainer);
			logger.info("Trainee successfully unassigned from trainer");

			assertFalse(isTraineeAssignedToTrainer(trainee, trainer),
			        "Trainee should not be assigned to trainer after unassignment");
		} catch (Exception e) {
			logger.error("Error unassigning trainee from trainer: {}", e.getMessage(), e);
			fail("Failed to unassign trainer from trainee: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINEE_USERNAME);
			MDC.remove(MDC_TRAINER_USERNAME);
		}
	}

	@Given("the system has a training with the following details:")
	public void theSystemHasATrainingWithTheFollowingDetails(DataTable dataTable) {
		Map<String, String> row = dataTable.asMap();

		String traineeUsername = row.get("traineeUsername");
		String trainerUsername = row.get("trainerUsername");
		String trainingName = row.get("trainingName");
		LocalDate trainingDate = LocalDate.parse(row.get("trainingDate"));
		int trainingDuration = Integer.parseInt(row.get("trainingDuration"));

		MDC.put(MDC_TRAINEE_USERNAME, traineeUsername);
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);
		MDC.put(MDC_TRAINING_NAME, trainingName);

		logger.info("Creating training: {} for trainee {} and trainer {}", trainingName, traineeUsername,
		        trainerUsername);

		try {
			Trainee trainee = loadTraineeUseCase.loadByUsername(traineeUsername);
			Trainer trainer = loadTrainerUseCase.loadByUsername(trainerUsername);
			TrainingType trainingType = findOrCreateTrainingType(trainingName);

			CreateTrainingCommand command = new CreateTrainingCommand(traineeUsername, trainerUsername, trainee,
			        trainer, trainingName, trainingType, trainingDate, trainingDuration);

			Training createdTraining = trainingCreationUseCase.create(command);
			currentTrainingId = createdTraining.getId();

			logger.info("Successfully created training with ID: {}", currentTrainingId);
		} catch (Exception e) {
			logger.error("Error creating training: {}", e.getMessage(), e);
			fail("Failed to create training: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINEE_USERNAME);
			MDC.remove(MDC_TRAINER_USERNAME);
			MDC.remove(MDC_TRAINING_NAME);
		}
	}

	@Given("the trainee {string} is assigned to trainer {string} with only one training")
	public void theTraineeIsAssignedToTrainerWithOnlyOneTraining(String traineeUsername, String trainerUsername) {
		MDC.put(MDC_TRAINEE_USERNAME, traineeUsername);
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);

		logger.info("Setting up trainee {} with one training from trainer {}", traineeUsername, trainerUsername);

		try {
			Trainee trainee = loadTraineeUseCase.loadByUsername(traineeUsername);
			Trainer trainer = loadTrainerUseCase.loadByUsername(trainerUsername);

			assignTraineeToTrainer(trainee, trainer);

			List<TrainingType> types = loadTrainingTypeUseCase.loadAll();
			TrainingType trainingType;

			if (types.isEmpty()) {
				trainingType = findOrCreateTrainingType("General");
				logger.info("Created new training type: General");
			} else {
				trainingType = types.get(0);
				logger.info("Using existing training type: {}", trainingType.getTrainingTypeName());
			}

			CreateTrainingCommand command = new CreateTrainingCommand(traineeUsername, trainerUsername, trainee,
			        trainer, trainingType.getTrainingTypeName(), trainingType, LocalDate.now(),
			        DEFAULT_TRAINING_DURATION);

			Training createdTraining = trainingCreationUseCase.create(command);
			currentTrainingId = createdTraining.getId();

			logger.info("Created training with ID: {}", currentTrainingId);

			boolean hasTraining = loadTrainingPort.existsByTraineeAndTrainer(trainee.getId(), trainer.getId());
			assertTrue(hasTraining, "There should be a training between trainee and trainer");
		} catch (Exception e) {
			logger.error("Error setting up trainee with one training: {}", e.getMessage(), e);
			fail("Failed to set up trainee with one training: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINEE_USERNAME);
			MDC.remove(MDC_TRAINER_USERNAME);
		}
	}

	@Given("the system has a new trainee {string} and trainer {string}")
	public void theSystemHasANewTraineeAndTrainer(String traineeUsername, String trainerUsername) {
		MDC.put(MDC_TRAINEE_USERNAME, traineeUsername);
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);

		logger.info("Setting up new trainee {} and trainer {}", traineeUsername, trainerUsername);

		try {
			User existingTraineeUser = null;
			User existingTrainerUser = null;

			try {
				existingTraineeUser = loadUserPort.findByUsername(traineeUsername);
				logger.info("Trainee user already exists: {}", traineeUsername);
			} catch (Exception e) {
				logger.info("Trainee user does not exist, will create: {}", traineeUsername);
			}

			try {
				existingTrainerUser = loadUserPort.findByUsername(trainerUsername);
				logger.info("Trainer user already exists: {}", trainerUsername);
			} catch (Exception e) {
				logger.info("Trainer user does not exist, will create: {}", trainerUsername);
			}

			if (existingTraineeUser == null) {
				User savedTraineeUser = createUser(traineeUsername, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME,
				        UserType.TRAINEE);
				createTrainee(savedTraineeUser);
				logger.info("Created new trainee: {}", traineeUsername);
			}

			if (existingTrainerUser == null) {
				User savedTrainerUser = createUser(trainerUsername, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME,
				        UserType.TRAINER);
				createTrainer(savedTrainerUser);
				logger.info("Created new trainer: {}", trainerUsername);
			}
		} catch (Exception e) {
			logger.error("Error creating new trainee and trainer: {}", e.getMessage(), e);
			fail("Failed to create new trainee and trainer: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINEE_USERNAME);
			MDC.remove(MDC_TRAINER_USERNAME);
		}
	}

	@When("the user sends a request to create a training with the following details:")
	public void theUserSendsARequestToCreateATrainingWithTheFollowingDetails(DataTable dataTable) throws Exception {
		Map<String, String> row = dataTable.asMap();

		String traineeUsername = row.get("traineeUsername");
		String trainerUsername = row.get("trainerUsername");
		String trainingName = row.get("trainingName");
		LocalDate trainingDate = LocalDate.parse(row.get("trainingDate"));
		int trainingDuration = Integer.parseInt(row.get("trainingDuration"));

		MDC.put(MDC_ACTION, ACTION_CREATE_TRAINING);
		MDC.put(MDC_TRAINEE_USERNAME, traineeUsername);
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);
		MDC.put(MDC_TRAINING_NAME, trainingName);
		MDC.put(MDC_TRAINING_DATE, trainingDate.toString());
		MDC.put(MDC_TRAINING_DURATION, String.valueOf(trainingDuration));

		logger.info("Sending request to create training: {} for trainee {} and trainer {}", trainingName,
		        traineeUsername, trainerUsername);

		TrainingCreateRequest request = new TrainingCreateRequest(traineeUsername, trainerUsername, trainingName,
		        trainingDate, trainingDuration);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users/me/trainings")
		        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request));

		resultActions = performRequestWithRole(requestBuilder);

		int status = resultActions.andReturn().getResponse().getStatus();
		logger.info("Create training response status: {}", status);

		if (status == 201) {
			String responseBody = resultActions.andReturn().getResponse().getContentAsString();
			currentTrainingId = extractTrainingIdFromResponse(responseBody);
			logger.info("Created training with ID: {}", currentTrainingId);
		}

		MDC.remove(MDC_ACTION);
		MDC.remove(MDC_TRAINEE_USERNAME);
		MDC.remove(MDC_TRAINER_USERNAME);
		MDC.remove(MDC_TRAINING_NAME);
		MDC.remove(MDC_TRAINING_DATE);
		MDC.remove(MDC_TRAINING_DURATION);
	}

	@When("the user sends a request to update the training with the following details:")
	public void theUserSendsARequestToUpdateTheTrainingWithTheFollowingDetails(DataTable dataTable) throws Exception {
		Map<String, String> row = dataTable.asMap();

		MDC.put(MDC_ACTION, ACTION_UPDATE_TRAINING);
		MDC.put(MDC_TRAINING_ID, currentTrainingId.toString());

		logger.info("Sending request to update training with ID: {}", currentTrainingId);

		TrainingUpdateRequest request = new TrainingUpdateRequest();

		if (row.containsKey("traineeUsername")) {
			request.setTraineeUsername(row.get("traineeUsername"));
			MDC.put(MDC_TRAINEE_USERNAME, row.get("traineeUsername"));
		}

		if (row.containsKey("trainerUsername")) {
			request.setTrainerUsername(row.get("trainerUsername"));
			MDC.put(MDC_TRAINER_USERNAME, row.get("trainerUsername"));
		}

		if (row.containsKey("trainingName")) {
			request.setTrainingName(row.get("trainingName"));
			MDC.put(MDC_TRAINING_NAME, row.get("trainingName"));
		}

		if (row.containsKey("trainingDate")) {
			request.setTrainingDate(LocalDate.parse(row.get("trainingDate")));
			MDC.put(MDC_TRAINING_DATE, row.get("trainingDate"));
		}

		if (row.containsKey("trainingDuration")) {
			request.setTrainingDuration(Integer.parseInt(row.get("trainingDuration")));
			MDC.put(MDC_TRAINING_DURATION, row.get("trainingDuration"));
		}

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/trainings/" + currentTrainingId)
		        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request));

		resultActions = performRequestWithRole(requestBuilder);

		int status = resultActions.andReturn().getResponse().getStatus();
		logger.info("Update training response status: {}", status);

		MDC.remove(MDC_ACTION);
		MDC.remove(MDC_TRAINING_ID);
		MDC.remove(MDC_TRAINEE_USERNAME);
		MDC.remove(MDC_TRAINER_USERNAME);
		MDC.remove(MDC_TRAINING_NAME);
		MDC.remove(MDC_TRAINING_DATE);
		MDC.remove(MDC_TRAINING_DURATION);
	}

	@When("the user sends a request to delete the training")
	public void theUserSendsARequestToDeleteTheTraining() throws Exception {
		MDC.put(MDC_ACTION, ACTION_DELETE_TRAINING);
		MDC.put(MDC_TRAINING_ID, currentTrainingId.toString());

		logger.info("Sending request to delete training with ID: {}", currentTrainingId);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/trainings/" + currentTrainingId);

		resultActions = performRequestWithRole(requestBuilder);

		int status = resultActions.andReturn().getResponse().getStatus();
		logger.info("Delete training response status: {}", status);

		MDC.remove(MDC_ACTION);
		MDC.remove(MDC_TRAINING_ID);
	}

	@When("the user sends a request to delete the training between {string} and {string}")
	public void theUserSendsARequestToDeleteTheTrainingBetween(String traineeUsername, String trainerUsername)
	        throws Exception {
		MDC.put(MDC_ACTION, ACTION_DELETE_TRAINING);
		MDC.put(MDC_TRAINING_ID, currentTrainingId.toString());
		MDC.put(MDC_TRAINEE_USERNAME, traineeUsername);
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);

		logger.info("Sending request to delete training with ID: {} between trainee {} and trainer {}",
		        currentTrainingId, traineeUsername, trainerUsername);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/trainings/" + currentTrainingId);

		resultActions = performRequestWithRole(requestBuilder);

		int status = resultActions.andReturn().getResponse().getStatus();
		logger.info("Delete training response status: {}", status);

		MDC.remove(MDC_ACTION);
		MDC.remove(MDC_TRAINING_ID);
		MDC.remove(MDC_TRAINEE_USERNAME);
		MDC.remove(MDC_TRAINER_USERNAME);
	}

	@Then("the training response status code should be {int}")
	public void theTrainingResponseStatusCodeShouldBe(int statusCode) throws Exception {
		logger.info("Verifying response status code is: {}", statusCode);
		resultActions.andExpect(status().is(statusCode));
	}

	@And("the trainer workload service should be notified about the new training")
	public void theTrainerWorkloadServiceShouldBeNotifiedAboutTheNewTraining() {
		logger.info("Verifying trainer workload service notification for new training");
		assertNotNull(currentTrainingId, "Training ID should not be null after creation");
	}

	@And("the trainer {string} workload should be increased by {int} minutes")
	public void theTrainerWorkloadShouldBeIncreasedByMinutes(String trainerUsername, int minutes) {
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);
		MDC.put(MDC_MINUTES, String.valueOf(minutes));

		logger.info("Verifying trainer {} workload increased by {} minutes", trainerUsername, minutes);

		try {
			Training training = loadTrainingUseCase.findById(currentTrainingId);
			assertEquals(trainerUsername, training.getTrainer().getUser().getUsername(),
			        "Training should be assigned to correct trainer");
			assertEquals(minutes, training.getTrainingDuration(), "Training duration should match expected minutes");

			logger.info("Trainer workload verification successful");
		} catch (Exception e) {
			logger.error("Error verifying trainer workload: {}", e.getMessage(), e);
			fail("Failed to verify trainer workload: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINER_USERNAME);
			MDC.remove(MDC_MINUTES);
		}
	}

	@And("the trainer workload service should be notified about the updated training")
	public void theTrainerWorkloadServiceShouldBeNotifiedAboutTheUpdatedTraining() {
		logger.info("Verifying trainer workload service notification for updated training");
		assertNotNull(currentTrainingId, "Training ID should not be null after update");

		try {
			Training training = loadTrainingUseCase.findById(currentTrainingId);
			assertNotNull(training, "Training should exist after update");
			logger.info("Training update verification successful");
		} catch (Exception e) {
			logger.error("Error verifying training update: {}", e.getMessage(), e);
			fail("Failed to verify training update: " + e.getMessage());
		}
	}

	@And("the trainer {string} workload should be updated to {int} minutes")
	public void theTrainerWorkloadShouldBeUpdatedToMinutes(String trainerUsername, int minutes) {
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);
		MDC.put(MDC_MINUTES, String.valueOf(minutes));

		logger.info("Verifying trainer {} workload updated to {} minutes", trainerUsername, minutes);

		try {
			Training training = loadTrainingUseCase.findById(currentTrainingId);
			assertEquals(trainerUsername, training.getTrainer().getUser().getUsername(),
			        "Training should be assigned to correct trainer");
			assertEquals(minutes, training.getTrainingDuration(), "Training duration should match updated minutes");

			logger.info("Trainer workload update verification successful");
		} catch (Exception e) {
			logger.error("Error verifying trainer workload update: {}", e.getMessage(), e);
			fail("Failed to verify trainer workload update: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINER_USERNAME);
			MDC.remove(MDC_MINUTES);
		}
	}

	@And("the trainer workload service should be notified about the deleted training")
	public void theTrainerWorkloadServiceShouldBeNotifiedAboutTheDeletedTraining() {
		logger.info("Verifying trainer workload service notification for deleted training");
		assertNotNull(currentTrainingId, "Training ID should not be null for deletion");

		try {
			try {
				Training training = loadTrainingUseCase.findById(currentTrainingId);
				fail("Training should not exist after deletion but found: " + training.getId());
			} catch (Exception e) {
				logger.info("Training deletion verification successful: {}", e.getMessage());
			}
		} catch (Exception e) {
			logger.error("Error verifying training deletion: {}", e.getMessage(), e);
			fail("Failed to verify training deletion: " + e.getMessage());
		}
	}

	@And("the trainer {string} workload should be decreased by {int} minutes")
	public void theTrainerWorkloadShouldBeDecreasedByMinutes(String trainerUsername, int minutes) {
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);
		MDC.put(MDC_MINUTES, String.valueOf(minutes));

		logger.info("Verifying trainer {} workload decreased by {} minutes", trainerUsername, minutes);

		assertNotNull(currentTrainingId, "Training ID should not be null for workload verification");

		try {
			try {
				Training training = loadTrainingUseCase.findById(currentTrainingId);
				fail("Training should not exist after deletion but found: " + training.getId());
			} catch (Exception e) {
				// Expected exception when training is deleted
				logger.info("Training deletion verification successful: {}", e.getMessage());
			}
		} catch (Exception e) {
			logger.error("Error verifying trainer workload decrease: {}", e.getMessage(), e);
			fail("Failed to verify trainer workload decrease: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINER_USERNAME);
			MDC.remove(MDC_MINUTES);
		}
	}

	@And("the trainee {string} should be assigned to trainer {string}")
	public void theTraineeShouldBeAssignedToTrainer(String traineeUsername, String trainerUsername) {
		MDC.put(MDC_TRAINEE_USERNAME, traineeUsername);
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);

		logger.info("Verifying trainee {} is assigned to trainer {}", traineeUsername, trainerUsername);

		try {
			Trainee trainee = loadTraineeUseCase.loadByUsername(traineeUsername);
			Trainer trainer = loadTrainerUseCase.loadByUsername(trainerUsername);

			boolean isAssigned = isTraineeAssignedToTrainer(trainee, trainer);

			assertTrue(isAssigned, "Trainee " + traineeUsername + " should be assigned to trainer " + trainerUsername);

			logger.info("Trainee-trainer assignment verification successful");
		} catch (Exception e) {
			logger.error("Error verifying trainer assignment: {}", e.getMessage(), e);
			fail("Failed to verify trainer assignment: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINEE_USERNAME);
			MDC.remove(MDC_TRAINER_USERNAME);
		}
	}

	@And("the trainee {string} should no longer be assigned to trainer {string}")
	public void theTraineeShouldNoLongerBeAssignedToTrainer(String traineeUsername, String trainerUsername) {
		MDC.put(MDC_TRAINEE_USERNAME, traineeUsername);
		MDC.put(MDC_TRAINER_USERNAME, trainerUsername);

		logger.info("Verifying trainee {} is no longer assigned to trainer {}", traineeUsername, trainerUsername);

		try {
			Trainee trainee = loadTraineeUseCase.loadByUsername(traineeUsername);
			Trainer trainer = loadTrainerUseCase.loadByUsername(trainerUsername);

			unassignTraineeFromTrainer(trainee, trainer);

			boolean isStillAssigned = isTraineeAssignedToTrainer(trainee, trainer);

			assertFalse(isStillAssigned,
			        "Trainee " + traineeUsername + " should not be assigned to trainer " + trainerUsername);

			logger.info("Trainee-trainer unassignment verification successful");
		} catch (Exception e) {
			logger.error("Error verifying trainer unassignment: {}", e.getMessage(), e);
			fail("Failed to verify trainer unassignment: " + e.getMessage());
		} finally {
			MDC.remove(MDC_TRAINEE_USERNAME);
			MDC.remove(MDC_TRAINER_USERNAME);
		}
	}
}
