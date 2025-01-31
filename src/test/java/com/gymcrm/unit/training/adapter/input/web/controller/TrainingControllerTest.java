package com.gymcrm.unit.training.adapter.input.web.controller;

import static org.mockito.Mockito.doNothing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.training.adapter.input.web.controller.TrainingController;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TrainingControllerTest {
	private MockMvc mockMvc;

	private TrainingCreationUseCase trainingCreationUseCase;

	private ObjectMapper objectMapper;

	private String username;
	private String trainingName;
	private LocalDate trainingDate;
	private int trainingDuration;

	@BeforeEach
	public void setup() {
		trainingCreationUseCase = Mockito.mock(TrainingCreationUseCase.class);
		TrainingController trainingController = new TrainingController(trainingCreationUseCase);
		mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();

		objectMapper = new ObjectMapper();

		username = "john.doe";
		trainingName = "Cardio Training";
		trainingDate = LocalDate.now();
		trainingDuration = 60;
	}

	@Test
	public void testCreateTraineeRequest_WithEmptyUsername_ShouldReturnBadRequest() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("trainee_username", "");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateTraineeRequest_WithExcessivelyLongUsername_ShouldReturnBadRequest() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("trainee_username",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint. It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateTraineeRequest_WithEmptyTrainerUsername_ShouldReturnBadRequest() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("trainer_username", "");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateTraineeRequest_WithExcessivelyLongTrainerUsername_ShouldReturnBadRequest() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("trainer_username",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint. It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateTraineeRequest_WithEmptyTrainingName_ShouldReturnBadRequest() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("training_name", "");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateTraineeRequest_WithExcessivelyLongTrainingName_ShouldReturnBadRequest() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("training_name",
		        "This is a sample test description that serves as input data for verifying the functionality of the trainee creation endpoint. It includes details to check proper response handling for valid requests in a realistic scenario.");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateTraineeRequest_WithInvalidTrainingDateType_ShouldReturnBadRequest() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("training_date", true);

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateTraineeRequest_WithInvalidTrainingDuration_ShouldReturnBadRequest() throws Exception {
		Map<String, Object> traineeRequest = createTraineeRequest();
		traineeRequest.put("training_duration", "invalid training duration");

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(traineeRequest)))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateTraineeRequest_WithValidData_ShouldReturnCreated() throws Exception {
		doNothing().when(trainingCreationUseCase).create(getCreateTrainingCommand());

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(objectMapper.findAndRegisterModules().writeValueAsString(createTraineeRequest())))
		        .andExpect(MockMvcResultMatchers.status().isCreated());
	}

	private CreateTrainingCommand getCreateTrainingCommand() {
		return new CreateTrainingCommand(username, username, trainingName, trainingDate, trainingDuration);
	}

	private Map<String, Object> createTraineeRequest() {
		Map<String, Object> request = new HashMap<>();
		request.put("trainee_username", username);
		request.put("trainer_username", username);
		request.put("training_name", trainingName);
		request.put("training_date", trainingDate);
		request.put("training_duration", trainingDuration);
		return request;
	}
}
