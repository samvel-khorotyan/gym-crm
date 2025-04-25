package com.gymcrm.cucumber.integration.steps;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.port.output.UpdateTrainerWorkloadPort;
import com.gymcrm.trainer.domain.ActionType;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.adapter.input.web.request.TrainingCreateRequest;
import com.gymcrm.training.adapter.input.web.request.TrainingUpdateRequest;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import com.gymcrm.training.application.port.input.UpdateTrainingUseCase;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.domain.TrainingType;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TrainingIntegrationSteps {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private TrainingCreationUseCase trainingCreationUseCase;

	@MockBean
	private LoadTrainingUseCase loadTrainingUseCase;

	@MockBean
	private UpdateTrainingUseCase updateTrainingUseCase;

	@MockBean
	private LoadTrainingPort loadTrainingPort;

	@MockBean
	private UpdateTrainerWorkloadPort updateTrainerWorkloadPort;

	private ResultActions resultActions;
	private Map<String, User> users = new HashMap<>();
	private Map<String, TrainingType> trainingTypes = new HashMap<>();
	private Map<String, Trainee> trainees = new HashMap<>();
	private Map<String, Trainer> trainers = new HashMap<>();
	private Training currentTraining;
	private UUID currentTrainingId;
	private ResponseEntity<?> responseEntity;

	@Before
	public void setup() {
		users.clear();
		trainingTypes.clear();
		trainees.clear();
		trainers.clear();
		currentTraining = null;
		currentTrainingId = null;
		reset(updateTrainerWorkloadPort);
	}

	@Given("the training service is running")
	public void theTrainingServiceIsRunning() {
		ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
		assertEquals(200, response.getStatusCodeValue());
	}

	@And("the user service is running")
	public void theUserServiceIsRunning() {
		ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
		assertEquals(200, response.getStatusCodeValue());
	}

	@Given("the system has the following users:")
	public void theSystemHasTheFollowingUsers(DataTable dataTable) {
		List<Map<String, String>> rows = dataTable.asMaps();
		for (Map<String, String> row : rows) {
			String username = row.get("username");
			String firstName = row.get("firstName");
			String lastName = row.get("lastName");
			UserType userType = UserType.valueOf(row.get("userType"));

			User user = new User();
			user.setId(UUID.randomUUID());
			user.setUsername(username);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setUserType(userType);
			users.put(username, user);

			if (userType == UserType.TRAINEE) {
				Trainee trainee = new Trainee();
				trainee.setId(UUID.randomUUID());
				trainee.setUser(user);
				trainee.setTrainers(new ArrayList<>());
				trainees.put(username, trainee);
			} else if (userType == UserType.TRAINER) {
				Trainer trainer = new Trainer();
				trainer.setId(UUID.randomUUID());
				trainer.setUser(user);
				trainers.put(username, trainer);
			}
		}
	}

	@Given("the system has the following training types:")
	public void theSystemHasTheFollowingTrainingTypes(DataTable dataTable) {
		List<Map<String, String>> rows = dataTable.asMaps();
		for (Map<String, String> row : rows) {
			String name = row.get("trainingTypeName");
			TrainingType type = new TrainingType();
			type.setId(UUID.randomUUID());
			type.setTrainingTypeName(name);
			trainingTypes.put(name, type);
		}
	}

	@Given("the trainee {string} is assigned to trainer {string}")
	public void theTraineeIsAssignedToTrainer(String traineeUsername, String trainerUsername) {
		Trainee trainee = trainees.get(traineeUsername);
		Trainer trainer = trainers.get(trainerUsername);
		trainee.getTrainers().add(trainer);
	}

	@Given("the user is authenticated with role {string}")
	public void theUserIsAuthenticatedWithRole(String role) {
		// Authentication is handled by @WithMockUser in the request methods
	}

	@When("the user sends a request to create a training with the following details:")
	@WithMockUser(roles = "ADMIN")
	public void theUserSendsARequestToCreateATrainingWithTheFollowingDetails(DataTable dataTable) throws Exception {
		Map<String, String> row = dataTable.asMap();

		TrainingCreateRequest request = new TrainingCreateRequest(row.get("traineeUsername"),
		        row.get("trainerUsername"), row.get("trainingName"), LocalDate.parse(row.get("trainingDate")),
		        Integer.parseInt(row.get("trainingDuration")));

		Training mockTraining = createMockTraining(request);
		when(trainingCreationUseCase.create(any(CreateTrainingCommand.class))).thenReturn(mockTraining);

		resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings")
		        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

		currentTraining = mockTraining;
	}

	@Then("the response status code should be {int}")
	public void theResponseStatusCodeShouldBe(int statusCode) throws Exception {
		resultActions.andExpect(status().is(statusCode));
	}

	@And("the trainer workload service should be notified about the new training")
	public void theTrainerWorkloadServiceShouldBeNotifiedAboutTheNewTraining() {
		verify(updateTrainerWorkloadPort, times(1)).sendTrainerWorkload(any(Training.class), eq(ActionType.ADD));
	}

	@And("the trainer {string} workload should be increased by {int} minutes")
	public void theTrainerWorkloadShouldBeIncreasedByMinutes(String trainerUsername, int minutes) {
		verify(updateTrainerWorkloadPort).sendTrainerWorkload(
		        argThat(training -> training.getTrainer().getUser().getUsername().equals(trainerUsername)
		                && training.getTrainingDuration() == minutes),
		        eq(ActionType.ADD));
	}

	@Given("the system has a training with the following details:")
	public void theSystemHasATrainingWithTheFollowingDetails(DataTable dataTable) {
		Map<String, String> row = dataTable.asMap();

		String traineeUsername = row.get("traineeUsername");
		String trainerUsername = row.get("trainerUsername");
		String trainingName = row.get("trainingName");
		LocalDate trainingDate = LocalDate.parse(row.get("trainingDate"));
		int trainingDuration = Integer.parseInt(row.get("trainingDuration"));

		Trainee trainee = trainees.get(traineeUsername);
		Trainer trainer = trainers.get(trainerUsername);
		TrainingType trainingType = trainingTypes.get(trainingName);

		currentTrainingId = UUID.randomUUID();
		currentTraining = new Training(currentTrainingId, trainingName, trainee, trainer, trainingType, trainingDate,
		        trainingDuration);

		when(loadTrainingUseCase.findById(currentTrainingId)).thenReturn(currentTraining);
	}

	@When("the user sends a request to update the training with the following details:")
	@WithMockUser(roles = "ADMIN")
	public void theUserSendsARequestToUpdateTheTrainingWithTheFollowingDetails(DataTable dataTable) throws Exception {
		Map<String, String> row = dataTable.asMap();

		TrainingUpdateRequest request = new TrainingUpdateRequest();

		if (row.containsKey("traineeUsername")) {
			request.setTraineeUsername(row.get("traineeUsername"));
		}

		if (row.containsKey("trainerUsername")) {
			request.setTrainerUsername(row.get("trainerUsername"));
		}

		if (row.containsKey("trainingName")) {
			request.setTrainingName(row.get("trainingName"));
		}

		if (row.containsKey("trainingDate")) {
			request.setTrainingDate(LocalDate.parse(row.get("trainingDate")));
		}

		if (row.containsKey("trainingDuration")) {
			request.setTrainingDuration(Integer.parseInt(row.get("trainingDuration")));
		}

		Training updatedTraining = new Training(currentTraining.getId(),
		        row.containsKey("trainingName") ? row.get("trainingName") : currentTraining.getTrainingName(),
		        currentTraining.getTrainee(), currentTraining.getTrainer(),
		        row.containsKey("trainingName")
		                ? trainingTypes.get(row.get("trainingName"))
		                : currentTraining.getTrainingType(),
		        row.containsKey("trainingDate")
		                ? LocalDate.parse(row.get("trainingDate"))
		                : currentTraining.getTrainingDate(),
		        row.containsKey("trainingDuration")
		                ? Integer.parseInt(row.get("trainingDuration"))
		                : currentTraining.getTrainingDuration());

		when(updateTrainingUseCase.update(eq(currentTrainingId), any())).thenReturn(updatedTraining);

		resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/trainings/" + currentTrainingId)
		        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

		currentTraining = updatedTraining;
	}

	@And("the trainer workload service should be notified about the updated training")
	public void theTrainerWorkloadServiceShouldBeNotifiedAboutTheUpdatedTraining() {
		verify(updateTrainerWorkloadPort, times(1)).sendTrainerWorkload(any(Training.class), eq(ActionType.DELETE));
		verify(updateTrainerWorkloadPort, times(1)).sendTrainerWorkload(any(Training.class), eq(ActionType.UPDATE));
	}

	@And("the trainer {string} workload should be updated to {int} minutes")
	public void theTrainerWorkloadShouldBeUpdatedToMinutes(String trainerUsername, int minutes) {
		verify(updateTrainerWorkloadPort).sendTrainerWorkload(
		        argThat(training -> training.getTrainer().getUser().getUsername().equals(trainerUsername)
		                && training.getTrainingDuration() == minutes),
		        eq(ActionType.UPDATE));
	}

	@When("the user sends a request to delete the training")
	@WithMockUser(roles = "ADMIN")
	public void theUserSendsARequestToDeleteTheTraining() throws Exception {
		doNothing().when(updateTrainingUseCase).deleteById(currentTrainingId);

		resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/trainings/" + currentTrainingId));
	}

	@And("the trainer workload service should be notified about the deleted training")
	public void theTrainerWorkloadServiceShouldBeNotifiedAboutTheDeletedTraining() {
		verify(updateTrainerWorkloadPort, times(1)).sendTrainerWorkload(any(Training.class), eq(ActionType.DELETE));
	}

	@And("the trainer {string} workload should be decreased by {int} minutes")
	public void theTrainerWorkloadShouldBeDecreasedByMinutes(String trainerUsername, int minutes) {
		verify(updateTrainerWorkloadPort).sendTrainerWorkload(
		        argThat(training -> training.getTrainer().getUser().getUsername().equals(trainerUsername)
		                && training.getTrainingDuration() == minutes),
		        eq(ActionType.DELETE));
	}

	@Given("the system has a new trainee {string} and trainer {string}")
	public void theSystemHasANewTraineeAndTrainer(String traineeUsername, String trainerUsername) {
		User traineeUser = new User();
		traineeUser.setId(UUID.randomUUID());
		traineeUser.setUsername(traineeUsername);
		traineeUser.setFirstName("New");
		traineeUser.setLastName("Trainee");
		traineeUser.setUserType(UserType.TRAINEE);
		users.put(traineeUsername, traineeUser);

		Trainee trainee = new Trainee();
		trainee.setId(UUID.randomUUID());
		trainee.setUser(traineeUser);
		trainee.setTrainers(new ArrayList<>());
		trainees.put(traineeUsername, trainee);

		User trainerUser = new User();
		trainerUser.setId(UUID.randomUUID());
		trainerUser.setUsername(trainerUsername);
		trainerUser.setFirstName("New");
		trainerUser.setLastName("Trainer");
		trainerUser.setUserType(UserType.TRAINER);
		users.put(trainerUsername, trainerUser);

		Trainer trainer = new Trainer();
		trainer.setId(UUID.randomUUID());
		trainer.setUser(trainerUser);
		trainers.put(trainerUsername, trainer);
	}

	@And("the trainee {string} is not assigned to trainer {string}")
	public void theTraineeIsNotAssignedToTrainer(String traineeUsername, String trainerUsername) {
		Trainee trainee = trainees.get(traineeUsername);
		Trainer trainer = trainers.get(trainerUsername);

		if (trainee.getTrainers() == null) {
			trainee.setTrainers(new ArrayList<>());
		} else {
			trainee.getTrainers().removeIf(t -> t.getId().equals(trainer.getId()));
		}
	}

	@And("the trainee {string} should be assigned to trainer {string}")
	public void theTraineeShouldBeAssignedToTrainer(String traineeUsername, String trainerUsername) {
		Trainee trainee = trainees.get(traineeUsername);
		Trainer trainer = trainers.get(trainerUsername);

		if (!trainee.getTrainers().contains(trainer)) {
			trainee.getTrainers().add(trainer);
		}

		assertTrue(trainee.getTrainers().contains(trainer));
	}

	@Given("the trainee {string} is assigned to trainer {string} with only one training")
	public void theTraineeIsAssignedToTrainerWithOnlyOneTraining(String traineeUsername, String trainerUsername) {
		Trainee trainee = trainees.get(traineeUsername);
		Trainer trainer = trainers.get(trainerUsername);

		if (trainee.getTrainers() == null) {
			trainee.setTrainers(new ArrayList<>());
		}
		trainee.getTrainers().add(trainer);

		TrainingType trainingType = trainingTypes.values().iterator().next();
		currentTrainingId = UUID.randomUUID();
		currentTraining = new Training(currentTrainingId, trainingType.getTrainingTypeName(), trainee, trainer,
		        trainingType, LocalDate.now(), 60);

		when(loadTrainingUseCase.findById(currentTrainingId)).thenReturn(currentTraining);

		when(loadTrainingPort.existsByTraineeAndTrainer(trainee.getId(), trainer.getId())).thenReturn(true);
	}

	@When("the user sends a request to delete the training between {string} and {string}")
	@WithMockUser(roles = "ADMIN")
	public void theUserSendsARequestToDeleteTheTrainingBetween(String traineeUsername, String trainerUsername)
	        throws Exception {
		doNothing().when(updateTrainingUseCase).deleteById(currentTrainingId);

		when(loadTrainingPort.existsByTraineeAndTrainer(trainees.get(traineeUsername).getId(),
		        trainers.get(trainerUsername).getId())).thenReturn(false);

		resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/trainings/" + currentTrainingId));
	}

	@And("the trainee {string} should no longer be assigned to trainer {string}")
	public void theTraineeShouldNoLongerBeAssignedToTrainer(String traineeUsername, String trainerUsername) {
		Trainee trainee = trainees.get(traineeUsername);
		Trainer trainer = trainers.get(trainerUsername);

		trainee.getTrainers().remove(trainer);

		assertFalse(trainee.getTrainers().contains(trainer));
	}

	private Training createMockTraining(TrainingCreateRequest request) {
		Trainee trainee = trainees.get(request.getTraineeUsername());
		Trainer trainer = trainers.get(request.getTrainerUsername());
		TrainingType trainingType = trainingTypes.get(request.getTrainingName());

		return new Training(UUID.randomUUID(), request.getTrainingName(), trainee, trainer, trainingType,
		        request.getTrainingDate(), request.getTrainingDuration());
	}
}
