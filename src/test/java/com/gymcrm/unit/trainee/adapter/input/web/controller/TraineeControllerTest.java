package com.gymcrm.unit.trainee.adapter.input.web.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.trainee.adapter.input.web.controller.TraineeController;
import com.gymcrm.trainee.application.port.input.*;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.domain.TrainingType;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TraineeControllerTest {
	private MockMvc mockMvc;

	private TraineeCreationUseCase traineeCreationUseCase;
	private TraineeUpdateUseCase traineeUpdateUseCase;
	private LoadTrainingUseCase loadTrainingUseCase;
	private LoadTraineeUseCase loadTraineeUseCase;

	private ObjectMapper objectMapper;

	private UUID id;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private boolean isActive;
	private UserType userType;
	private String address;
	private LocalDate dateOfBirth;
	private String specialization;
	private String trainingName;
	private String trainingTypeName;
	private LocalDate trainingDate;
	private int trainingDuration;

	@BeforeEach
	public void setup() {
		traineeCreationUseCase = Mockito.mock(TraineeCreationUseCase.class);
		traineeUpdateUseCase = Mockito.mock(TraineeUpdateUseCase.class);
		loadTrainingUseCase = Mockito.mock(LoadTrainingUseCase.class);
		loadTraineeUseCase = Mockito.mock(LoadTraineeUseCase.class);

		TraineeController traineeController = new TraineeController(traineeCreationUseCase, traineeUpdateUseCase,
		        loadTrainingUseCase, loadTraineeUseCase);

		mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();

		objectMapper = new ObjectMapper();

		id = UUID.randomUUID();
		firstName = "John";
		lastName = "Doe";
		username = "john.doe";
		password = "password123";
		isActive = true;
		userType = UserType.TRAINEE;
		dateOfBirth = LocalDate.now();
		address = "123 Main St, City ville";
		specialization = "Fitness Trainer";
		trainingName = "Cardio Training";
		trainingTypeName = "Cardio Training";
		trainingDate = LocalDate.now();
		trainingDuration = 60;
	}

	@Test
	public void shouldReturnBadRequestWhenFirstNameIsBlank() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("first_name", "");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainees").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenFirstNameExceedsMaxAllowedLength() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("first_name",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint. It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainees").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenLastNameIsBlank() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("last_name", "");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainees").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenLastNameExceedsMaxAllowedLength() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("last_name",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint. It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainees").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenDateOfBirthIsInvalid() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("date_of_birth", "wrong-date-of-birth");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainees").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void shouldReturnBadRequestWhenAddressIsInvalid() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("address", LocalDate.now());

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainees").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
  public void ensureSuccessfulTraineeCreation() throws Exception {
    when(traineeCreationUseCase.create(getCreateTraineeCommand())).thenReturn(getTrainee());

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/me/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper
                        .findAndRegisterModules()
                        .writeValueAsString(createTraineeRequest())))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

	@Test
  public void getByUsername_ReturnsTraineeDetails_WhenUsernameIsValid() throws Exception {
    when(loadTraineeUseCase.loadByUsername(username)).thenReturn(getTrainee());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/users/me/trainees")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

	@Test
	public void updateTrainee_ReturnsBadRequest_WhenFirstNameIsEmpty() throws Exception {
		Map<String, Object> request = traineeUpdateRequest();
		request.put("first_name", "");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/trainees/" + id).contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateTrainee_ReturnsBadRequest_WhenFirstNameExceedsMaxLength() throws Exception {
		Map<String, Object> request = traineeUpdateRequest();
		request.put("first_name",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint. It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/trainees/" + id).contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateTrainee_ReturnsBadRequest_WhenLastNameIsEmpty() throws Exception {
		Map<String, Object> request = traineeUpdateRequest();
		request.put("last_name", "");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/trainees/" + id).contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateTrainee_ReturnsBadRequest_WhenLastNameExceedsMaxLength() throws Exception {
		Map<String, Object> request = traineeUpdateRequest();
		request.put("last_name",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint. It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/trainees/" + id).contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateTrainee_ReturnsBadRequest_WhenDateOfBirthIsInvalid() throws Exception {
		Map<String, Object> request = traineeUpdateRequest();
		request.put("date_of_birth", "wrong-date-of-birth");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/trainees/" + id).contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateTrainee_ReturnsBadRequest_WhenAddressHasInvalidFormat() throws Exception {
		Map<String, Object> request = traineeUpdateRequest();
		request.put("address", LocalDate.now());

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/trainees/" + id).contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateTrainee_ReturnsBadRequest_WhenIsActiveHasInvalidType() throws Exception {
		Map<String, Object> request = traineeUpdateRequest();
		request.put("is_active", "incorrect-field");

		mockMvc.perform(MockMvcRequestBuilders.put("/users/me/trainees/" + id).contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
  public void updateTrainee_ReturnsOk_WhenValidRequestIsProvided() throws Exception {
    when(traineeUpdateUseCase.update(getUpdateTraineeCommand())).thenReturn(getTrainee());

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/users/me/trainees/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper
                        .findAndRegisterModules()
                        .writeValueAsString(traineeUpdateRequest())))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

	@Test
	public void deleteTrainee_ReturnsNoContent_WhenValidUsernameIsProvided() throws Exception {
		doNothing().when(traineeUpdateUseCase).deleteByUsername(username);

		mockMvc.perform(MockMvcRequestBuilders.delete("/users/me/trainees").param("username", username)
		        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	public void updateTraineeTrainers_ReturnsBadRequest_WhenTraineeUsernameIsEmpty() throws Exception {
		Map<String, Object> traineeRequest = traineeTrainersUpdateRequest();
		traineeRequest.put("trainee_username", "");

		mockMvc.perform(
		        MockMvcRequestBuilders.put("/users/me/trainees/trainers").contentType(MediaType.APPLICATION_JSON)
		                .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateTraineeTrainers_ReturnsBadRequest_WhenTraineeUsernameExceedsMaxLength() throws Exception {
		Map<String, Object> request = traineeTrainersUpdateRequest();
		request.put("trainee_username",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint. It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(
		        MockMvcRequestBuilders.put("/users/me/trainees/trainers").contentType(MediaType.APPLICATION_JSON)
		                .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void updateTraineeTrainers_ReturnsBadRequest_WhenTrainerUsernamesIsInvalidType() throws Exception {
		Map<String, Object> request = traineeTrainersUpdateRequest();
		request.put("trainer_usernames", "incorrect-trainer-usernames");

		mockMvc.perform(
		        MockMvcRequestBuilders.put("/users/me/trainees/trainers").contentType(MediaType.APPLICATION_JSON)
		                .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
  public void updateTraineeTrainers_ReturnsOk_WhenValidRequestIsProvided() throws Exception {
    when(traineeUpdateUseCase.updateTraineeTrainers(getUpdateTraineeTrainersCommand()))
        .thenReturn(getTrainee());

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/users/me/trainees/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper
                        .findAndRegisterModules()
                        .writeValueAsString(traineeTrainersUpdateRequest())))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

	@Test
	public void findTraineeTrainingsByCriteria_ReturnsOk_WhenValidRequestIsProvided() throws Exception {
		var periodFrom = LocalDate.now();
		var periodTo = LocalDate.now();

		when(loadTrainingUseCase.findTraineeTrainingsByCriteria(username, periodFrom, periodTo, trainingName,
		        trainingTypeName)).thenReturn(new ArrayList<>(List.of(getTraining())));

		mockMvc.perform(MockMvcRequestBuilders.get("/users/me/trainees/trainings").param("username", username)
		        .param("period_from", periodFrom.toString()).param("period_to", periodTo.toString())
		        .param("trainer_name", trainingName).param("training_type", trainingTypeName)
		        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void activateDeactivateTrainee_ReturnsBadRequest_WhenUsernameIsEmpty() throws Exception {
		Map<String, Object> request = traineeActivateDeactivateRequest();
		request.put("username", "");

		mockMvc.perform(MockMvcRequestBuilders.patch("/users/me/trainees/state").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void activateDeactivateTrainee_ReturnsBadRequest_WhenUsernameExceedsMaxLength() throws Exception {
		Map<String, Object> request = traineeActivateDeactivateRequest();
		request.put("username",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint. It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.patch("/users/me/trainees/state").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void activateDeactivateTrainee_ReturnsBadRequest_WhenIsActiveHasInvalidType() throws Exception {
		Map<String, Object> request = traineeActivateDeactivateRequest();
		request.put("is_active", "incorrect-field");

		mockMvc.perform(MockMvcRequestBuilders.patch("/users/me/trainees/state").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(request)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void activateDeactivateTrainee_ReturnsNoContent_WhenValidRequestIsProvided() throws Exception {
		doNothing().when(traineeUpdateUseCase).activateDeactivate(getActivateDeactivateTraineeCommand());

		mockMvc.perform(MockMvcRequestBuilders.patch("/users/me/trainees/state").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeActivateDeactivateRequest())))
		        .andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	private User getUser() {
		return new User(id, firstName, lastName, username, password, isActive, userType);
	}

	private Trainer getTrainer() {
		return new Trainer(id, specialization, getUser());
	}

	private Trainee getTrainee() {
		return new Trainee(id, dateOfBirth, address, getUser());
	}

	private TrainingType getTrainingType() {
		return new TrainingType(id, trainingTypeName);
	}

	private Training getTraining() {
		return new Training(id, trainingName, getTrainee(), getTrainer(), getTrainingType(), trainingDate,
		        trainingDuration);
	}

	private CreateTraineeCommand getCreateTraineeCommand() {
		return new CreateTraineeCommand(firstName, lastName, dateOfBirth, address);
	}

	private UpdateTraineeCommand getUpdateTraineeCommand() {
		return new UpdateTraineeCommand(id, firstName, lastName, dateOfBirth, address, isActive);
	}

	private UpdateTraineeTrainersCommand getUpdateTraineeTrainersCommand() {
		return new UpdateTraineeTrainersCommand(username, new ArrayList<>(List.of(username)));
	}

	private ActivateDeactivateTraineeCommand getActivateDeactivateTraineeCommand() {
		return new ActivateDeactivateTraineeCommand(username, isActive);
	}

	private Map<String, Object> createTraineeRequest() {
		Map<String, Object> request = new HashMap<>();
		request.put("first_name", firstName);
		request.put("last_name", lastName);
		request.put("date_of_birth", dateOfBirth);
		request.put("address", address);
		return request;
	}

	public Map<String, Object> traineeUpdateRequest() {
		Map<String, Object> request = new HashMap<>();
		request.put("first_name", firstName);
		request.put("last_name", lastName);
		request.put("date_of_birth", dateOfBirth);
		request.put("address", address);
		request.put("is_active", isActive);
		return request;
	}

	public Map<String, Object> traineeTrainersUpdateRequest() {
		Map<String, Object> map = new HashMap<>();
		map.put("trainee_username", username);
		map.put("trainer_usernames", new ArrayList<>(List.of(username)));
		return map;
	}

	public Map<String, Object> traineeActivateDeactivateRequest() {
		Map<String, Object> map = new HashMap<>();
		map.put("username", username);
		map.put("is_active", isActive);
		return map;
	}
}
