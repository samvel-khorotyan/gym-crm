package com.gymcrm.cucumber.component.steps;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CucumberSpringConfiguration.class)
public class TrainingComponentTest {
	private static final Logger logger = LoggerFactory.getLogger(TrainingComponentTest.class);
	private static final String TRAINING_ENDPOINT = "/trainings/";
	private static final String USER_TRAININGS_ENDPOINT = "/users/me/trainings";

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
		currentTrainingId = null;
		currentUserRole = null;
		logger.info("Setting up test environment");
	}

	@After
	public void cleanupDatabase() {
		try {
			entityManager.createQuery("DELETE FROM Training").executeUpdate();
			entityManager.createQuery("DELETE FROM Trainee").executeUpdate();
			entityManager.createQuery("DELETE FROM Trainer").executeUpdate();
			entityManager.createQuery("DELETE FROM User").executeUpdate();
			entityManager.flush();
			entityManager.clear();
			logger.info("Database cleaned up successfully");
		} catch (Exception e) {
			logger.error("Error cleaning up database", e);
		}
	}

	@Given("the system has the following users:")
	public void theSystemHasTheFollowingUsers(DataTable dataTable) {
		List<Map<String, String>> rows = dataTable.asMaps();
		for (Map<String, String> row : rows) {
			createUser(row);
		}
	}

	@Given("the system has the following training types:")
	public void theSystemHasTheFollowingTrainingTypes(DataTable dataTable) {
		List<Map<String, String>> rows = dataTable.asMaps();
		for (Map<String, String> row : rows) {
			createTrainingType(row.get("trainingTypeName"));
		}
	}

	@Given("the trainee {string} is assigned to trainer {string}")
	public void theTraineeIsAssignedToTrainer(String traineeUsername, String trainerUsername) {
		assignTrainerToTrainee(traineeUsername, trainerUsername);
	}

	@Given("the user is authenticated with role {string}")
	public void theUserIsAuthenticatedWithRole(String role) {
		currentUserRole = role;
		logger.info("User role set to: {}", role);
	}

	@When("the user sends a request to create a training with the following details:")
	public void theUserSendsARequestToCreateATrainingWithTheFollowingDetails(DataTable dataTable) throws Exception {
		Map<String, String> row = dataTable.asMap();
		TrainingCreateRequest request = createTrainingRequest(row);

		logger.info("Sending request to create training: {}", objectMapper.writeValueAsString(request));

		resultActions = mockMvc
		        .perform(MockMvcRequestBuilders.post(USER_TRAININGS_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
		                .content(objectMapper.writeValueAsString(request)).with(getSecurityContext()));

		extractTrainingIdFromResponse();
	}

	@Then("the response status code should be {int}")
	public void theResponseStatusCodeShouldBe(int statusCode) throws Exception {
		resultActions.andExpect(status().is(statusCode));
		logger.info("Verified response status code: {}", statusCode);
	}

	@And("the response should contain a training with name {string}")
	public void theResponseShouldContainATrainingWithName(String trainingName) throws Exception {
		resultActions.andExpect(jsonPath("$.training_name").value(trainingName));
		logger.info("Verified training name: {}", trainingName);
	}

	@And("the response should contain a training with trainee {string}")
	public void theResponseShouldContainATrainingWithTrainee(String traineeUsername) throws Exception {
		resultActions.andExpect(jsonPath("$.trainee_username").value(traineeUsername));
		logger.info("Verified trainee username: {}", traineeUsername);
	}

	@And("the response should contain a training with trainer {string}")
	public void theResponseShouldContainATrainingWithTrainer(String trainerUsername) throws Exception {
		resultActions.andExpect(jsonPath("$.trainer_username").value(trainerUsername));
		logger.info("Verified trainer username: {}", trainerUsername);
	}

	@Given("the system has a training with the following details:")
	public void theSystemHasATrainingWithTheFollowingDetails(DataTable dataTable) {
		Map<String, String> row = dataTable.asMap();
		createTrainingInSystem(row);
	}

	@When("the user sends a request to get the training by ID")
	public void theUserSendsARequestToGetTheTrainingByID() throws Exception {
		logger.info("Sending request to get training with ID: {}, with role: {}", currentTrainingId, currentUserRole);

		resultActions = mockMvc.perform(MockMvcRequestBuilders.get(TRAINING_ENDPOINT + currentTrainingId)
		        .contentType(MediaType.APPLICATION_JSON).with(getSecurityContext()));
	}

	@When("the user sends a request to update the training with the following details:")
	public void theUserSendsARequestToUpdateTheTrainingWithTheFollowingDetails(DataTable dataTable) throws Exception {
		Map<String, String> row = dataTable.asMap();
		TrainingUpdateRequest request = createTrainingUpdateRequest(row);

		logger.info("Sending request to update training with ID: {}", currentTrainingId);

		resultActions = mockMvc.perform(MockMvcRequestBuilders.put(TRAINING_ENDPOINT + currentTrainingId)
		        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
		        .with(getSecurityContext()));
	}

	@And("the response should contain a training with date {string}")
	public void theResponseShouldContainATrainingWithDate(String date) throws Exception {
		resultActions.andExpect(jsonPath("$.training_date").value(date));
		logger.info("Verified training date: {}", date);
	}

	@And("the response should contain a training with duration {int}")
	public void theResponseShouldContainATrainingWithDuration(int duration) throws Exception {
		resultActions.andExpect(jsonPath("$.training_duration").value(duration));
		logger.info("Verified training duration: {}", duration);
	}

	@When("the user sends a request to delete the training")
	public void theUserSendsARequestToDeleteTheTraining() throws Exception {
		logger.info("Sending request to delete training with ID: {}", currentTrainingId);

		resultActions = mockMvc.perform(
		        MockMvcRequestBuilders.delete(TRAINING_ENDPOINT + currentTrainingId).with(getSecurityContext()));
	}

	@And("the training should no longer exist in the system")
	public void theTrainingShouldNoLongerExistInTheSystem() {
		verifyTrainingDoesNotExist();
	}

	private void createUser(Map<String, String> userData) {
		String username = userData.get("username");
		String firstName = userData.get("firstName");
		String lastName = userData.get("lastName");
		UserType userType = UserType.valueOf(userData.get("userType"));

		try {
			try {
				loadUserPort.findByUsername(username);
				logger.info("User already exists: {}", username);
				return;
			} catch (Exception e) {
				logger.info("User not found, creating new: {}", username);
			}

			User user = new User();
			user.setId(UUID.randomUUID());
			user.setUsername(username);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setUserType(userType);
			user.setPassword("password");
			user.setIsActive(true);

			User savedUser = updateUserPort.save(user);
			logger.info("Created user: {} with ID: {}", username, savedUser.getId());

			if (userType == UserType.TRAINEE) {
				createTrainee(savedUser);
			} else if (userType == UserType.TRAINER) {
				createTrainer(savedUser);
			}
		} catch (Exception e) {
			logger.error("Failed to create user: {}", username, e);
			throw new RuntimeException("Failed to create user: " + username, e);
		}
	}

	private void createTrainee(User user) {
		Trainee trainee = new Trainee();
		trainee.setId(UUID.randomUUID());
		trainee.setUser(user);
		trainee.setTrainers(new ArrayList<>());
		updateTraineePort.save(trainee);
		logger.info("Created trainee for user: {}", user.getUsername());
	}

	private void createTrainer(User user) {
		Trainer trainer = new Trainer();
		trainer.setId(UUID.randomUUID());
		trainer.setUser(user);
		trainer.setSpecialization("General Fitness");
		updateTrainerPort.save(trainer);
		logger.info("Created trainer for user: {}", user.getUsername());
	}

	private void createTrainingType(String name) {
		try {
			List<TrainingType> allTypes = loadTrainingTypeUseCase.loadAll();
			if (allTypes.stream().anyMatch(type -> type.getTrainingTypeName().equals(name))) {
				logger.info("Training type already exists: {}", name);
				return;
			}

			TrainingType type = new TrainingType();
			type.setId(UUID.randomUUID());
			type.setTrainingTypeName(name);
			updateTrainingTypePort.save(type);
			logger.info("Created training type: {}", name);
		} catch (Exception e) {
			logger.error("Failed to create training type: {}", name, e);
			throw new RuntimeException("Failed to create training type: " + name, e);
		}
	}

	private void assignTrainerToTrainee(String traineeUsername, String trainerUsername) {
		try {
			Trainee trainee = loadTraineeUseCase.loadByUsername(traineeUsername);
			Trainer trainer = loadTrainerUseCase.loadByUsername(trainerUsername);

			if (trainee.getTrainers() == null) {
				trainee.setTrainers(new ArrayList<>());
			}

			if (!trainee.getTrainers().contains(trainer)) {
				trainee.getTrainers().add(trainer);
				updateTraineePort.save(trainee);
				logger.info("Assigned trainer {} to trainee {}", trainerUsername, traineeUsername);
			} else {
				logger.info("Trainer {} is already assigned to trainee {}", trainerUsername, traineeUsername);
			}
		} catch (Exception e) {
			logger.error("Failed to assign trainer to trainee", e);
			throw new RuntimeException("Failed to assign trainer to trainee", e);
		}
	}

	private TrainingCreateRequest createTrainingRequest(Map<String, String> data) {
		return new TrainingCreateRequest(data.get("traineeUsername"), data.get("trainerUsername"),
		        data.get("trainingName"), LocalDate.parse(data.get("trainingDate")),
		        Integer.parseInt(data.get("trainingDuration")));
	}

	private TrainingUpdateRequest createTrainingUpdateRequest(Map<String, String> data) {
		TrainingUpdateRequest request = new TrainingUpdateRequest();

		Optional.ofNullable(data.get("traineeUsername")).ifPresent(request::setTraineeUsername);
		Optional.ofNullable(data.get("trainerUsername")).ifPresent(request::setTrainerUsername);
		Optional.ofNullable(data.get("trainingName")).ifPresent(request::setTrainingName);
		Optional.ofNullable(data.get("trainingDate")).ifPresent(date -> request.setTrainingDate(LocalDate.parse(date)));
		Optional.ofNullable(data.get("trainingDuration"))
		        .ifPresent(duration -> request.setTrainingDuration(Integer.parseInt(duration)));

		return request;
	}

	private void createTrainingInSystem(Map<String, String> data) {
		String traineeUsername = data.get("traineeUsername");
		String trainerUsername = data.get("trainerUsername");
		String trainingName = data.get("trainingName");
		LocalDate trainingDate = LocalDate.parse(data.get("trainingDate"));
		int trainingDuration = Integer.parseInt(data.get("trainingDuration"));

		try {
			Trainee trainee = loadTraineeUseCase.loadByUsername(traineeUsername);
			Trainer trainer = loadTrainerUseCase.loadByUsername(trainerUsername);

			TrainingType trainingType = findOrCreateTrainingType(trainingName);

			CreateTrainingCommand command = new CreateTrainingCommand(traineeUsername, trainerUsername, trainee,
			        trainer, trainingName, trainingType, trainingDate, trainingDuration);

			Training createdTraining = trainingCreationUseCase.create(command);
			currentTrainingId = createdTraining.getId();
			logger.info("Created training with ID: {}", currentTrainingId);
		} catch (Exception e) {
			logger.error("Failed to create training for scenario", e);
			throw new RuntimeException("Failed to create training for scenario", e);
		}
	}

	private TrainingType findOrCreateTrainingType(String trainingName) {
		return loadTrainingTypeUseCase.loadAll().stream()
		        .filter(type -> type.getTrainingTypeName().equals(trainingName)).findFirst().orElseGet(() -> {
			        TrainingType newType = new TrainingType();
			        newType.setId(UUID.randomUUID());
			        newType.setTrainingTypeName(trainingName);
			        return updateTrainingTypePort.save(newType);
		        });
	}

	private void extractTrainingIdFromResponse() {
		try {
			if (resultActions.andReturn().getResponse().getStatus() == 201) {
				String responseBody = resultActions.andReturn().getResponse().getContentAsString();
				Pattern pattern = Pattern.compile("\"id\":\"([^\"]+)\"");
				Matcher matcher = pattern.matcher(responseBody);
				if (matcher.find()) {
					currentTrainingId = UUID.fromString(matcher.group(1));
					logger.info("Extracted training ID: {}", currentTrainingId);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to extract training ID from response", e);
		}
	}

	private void verifyTrainingDoesNotExist() {
		try {
			loadTrainingUseCase.findById(currentTrainingId);
			throw new AssertionError("Training still exists in the system");
		} catch (Exception e) {
			logger.info("Verified training no longer exists: {}", currentTrainingId);
		}
	}

	private RequestPostProcessor getSecurityContext() {
		if (currentUserRole == null) {
			return SecurityMockMvcRequestPostProcessors.user("admin.user").roles("ADMIN");
		}

		return switch (currentUserRole) {
			case "TRAINER" -> SecurityMockMvcRequestPostProcessors.user("trainer.user").roles("TRAINER");
			case "TRAINEE" -> SecurityMockMvcRequestPostProcessors.user("trainee.user").roles("TRAINEE");
			default -> SecurityMockMvcRequestPostProcessors.user("admin.user").roles("ADMIN");
		};
	}
}
