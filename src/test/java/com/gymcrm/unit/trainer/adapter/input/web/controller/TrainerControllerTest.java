package com.gymcrm.unit.trainer.adapter.input.web.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.adapter.input.web.controller.TrainerController;
import com.gymcrm.trainer.application.port.input.*;
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

class TrainerControllerTest {
	private MockMvc mockMvc;

	private TrainerCreationUseCase trainerCreationUseCase;
	private TrainerUpdateUseCase trainerUpdateUseCase;
	private LoadTrainingUseCase loadTrainingUseCase;
	private LoadTrainerUseCase loadTrainerUseCase;

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
		trainerCreationUseCase = Mockito.mock(TrainerCreationUseCase.class);
		trainerUpdateUseCase = Mockito.mock(TrainerUpdateUseCase.class);
		loadTrainingUseCase = Mockito.mock(LoadTrainingUseCase.class);
		loadTrainerUseCase = Mockito.mock(LoadTrainerUseCase.class);

		TrainerController trainerController = new TrainerController(trainerCreationUseCase, trainerUpdateUseCase,
		        loadTrainingUseCase, loadTrainerUseCase);

		mockMvc = MockMvcBuilders.standaloneSetup(trainerController).build();

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
  public void createTrainer_ReturnsOk_WhenValidRequestIsProvided() throws Exception {
    when(trainerCreationUseCase.create(getCreateTrainerCommand())).thenReturn(getTrainer());

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/me/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper
                        .findAndRegisterModules()
                        .writeValueAsString(createTrainerRequest())))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

	@Test
  public void getTrainer_ReturnsOk_WhenTrainerExistsForUsername() throws Exception {
    when(loadTrainerUseCase.loadByUsername(username)).thenReturn(getTrainer());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/users/me/trainers")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

	@Test
  public void updateTrainer_ReturnsOk_WhenValidRequestIsProvided() throws Exception {
    when(trainerUpdateUseCase.update(getUpdateTrainerCommand())).thenReturn(getTrainer());

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/users/me/trainers/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper
                        .findAndRegisterModules()
                        .writeValueAsString(trainerUpdateRequest())))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

	@Test
  public void getUnassignedTrainers_ReturnsOk_WhenValidUsernameIsProvided() throws Exception {
    when(loadTrainerUseCase.loadActiveTrainersNotAssignedToTrainee(username))
        .thenReturn(new ArrayList<>(List.of(getTrainer())));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/users/me/trainers/unassigned")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

	@Test
	public void getTrainerTrainingsByCriteria_ReturnsOk_WhenValidParametersAreProvided() throws Exception {
		LocalDate periodFrom = LocalDate.now();
		LocalDate periodTo = LocalDate.now();
		String traineeName = firstName;

		when(loadTrainerUseCase.loadByUsername(username)).thenReturn(getTrainer());
		when(loadTrainingUseCase.findTrainerTrainingsByCriteria(username, periodFrom, periodTo, traineeName))
		        .thenReturn(new ArrayList<>(List.of(getTraining())));

		mockMvc.perform(MockMvcRequestBuilders.get("/users/me/trainers/trainings").param("username", username)
		        .param("period_from", periodFrom.toString()).param("period_to", periodTo.toString())
		        .param("trainee_name", traineeName).contentType(MediaType.APPLICATION_JSON))
		        .andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void activateDeactivateTrainer_ReturnsOk_WhenValidRequestIsProvided() throws Exception {
		doNothing().when(trainerUpdateUseCase).activateDeactivate(getActivateDeactivateTrainerCommand());

		mockMvc.perform(MockMvcRequestBuilders.patch("/users/me/trainers/state").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(trainerActivateDeactivateRequest())))
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

	public CreateTrainerCommand getCreateTrainerCommand() {
		return new CreateTrainerCommand(firstName, lastName, specialization);
	}

	public UpdateTrainerCommand getUpdateTrainerCommand() {
		return new UpdateTrainerCommand(id, firstName, lastName, specialization, isActive);
	}

	public ActivateDeactivateTrainerCommand getActivateDeactivateTrainerCommand() {
		return new ActivateDeactivateTrainerCommand(firstName, isActive);
	}

	public Map<String, Object> createTrainerRequest() {
		Map<String, Object> trainerRequest = new HashMap<>();
		trainerRequest.put("first_name", firstName);
		trainerRequest.put("last_name", lastName);
		trainerRequest.put("specialization", specialization);
		return trainerRequest;
	}

	private Map<String, Object> trainerUpdateRequest() {
		Map<String, Object> trainerRequest = new HashMap<>();
		trainerRequest.put("first_name", firstName);
		trainerRequest.put("last_name", lastName);
		trainerRequest.put("specialization", specialization);
		trainerRequest.put("is_active", isActive);
		return trainerRequest;
	}

	public Map<String, Object> trainerActivateDeactivateRequest() {
		Map<String, Object> request = new HashMap<>();
		request.put("username", username);
		request.put("is_active", isActive);
		return request;
	}
}
