package com.gymcrm.unit.training.adapter.input.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.adapter.input.web.controller.TrainingController;
import com.gymcrm.training.adapter.input.web.request.TrainingCreateRequest;
import com.gymcrm.training.adapter.input.web.request.TrainingUpdateRequest;
import com.gymcrm.training.application.port.input.*;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.domain.TrainingType;
import com.gymcrm.user.domain.User;
import java.time.LocalDate;
import java.util.UUID;
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
	private LoadTrainingUseCase loadTrainingUseCase;
	private UpdateTrainingUseCase updateTrainingUseCase;

	private ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		trainingCreationUseCase = Mockito.mock(TrainingCreationUseCase.class);
		loadTrainingUseCase = Mockito.mock(LoadTrainingUseCase.class);
		updateTrainingUseCase = Mockito.mock(UpdateTrainingUseCase.class);

		TrainingController trainingController = new TrainingController(trainingCreationUseCase, loadTrainingUseCase,
		        updateTrainingUseCase);

		mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();

		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Test
	public void create_ReturnsCreated_WhenValidRequestProvided() throws Exception {
		TrainingCreateRequest request = createValidTrainingRequest();
		String requestJson = objectMapper.writeValueAsString(request);

		Training mockTraining = createMockTraining();
		when(trainingCreationUseCase.create(any(CreateTrainingCommand.class))).thenReturn(mockTraining);

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(requestJson)).andExpect(MockMvcResultMatchers.status().isCreated())
		        .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

		verify(trainingCreationUseCase).create(any(CreateTrainingCommand.class));
	}

	@Test
	public void create_ReturnsBadRequest_WhenInvalidRequestProvided() throws Exception {
		TrainingCreateRequest invalidRequest = new TrainingCreateRequest();
		invalidRequest.setTrainingName("");
		String requestJson = objectMapper.writeValueAsString(invalidRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(requestJson)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void get_ReturnsTraining_WhenValidIdProvided() throws Exception {
		UUID trainingId = UUID.randomUUID();

		Training mockTraining = createMockTraining();
		when(loadTrainingUseCase.findById(trainingId)).thenReturn(mockTraining);

		mockMvc.perform(
		        MockMvcRequestBuilders.get("/trainings/{id}", trainingId).contentType(MediaType.APPLICATION_JSON))
		        .andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

		verify(loadTrainingUseCase).findById(trainingId);
	}

	@Test
	public void update_ReturnsUpdatedTraining_WhenValidRequestProvided() throws Exception {
		UUID trainingId = UUID.randomUUID();
		TrainingUpdateRequest request = createValidTrainingUpdateRequest();
		String requestJson = objectMapper.writeValueAsString(request);

		Training mockTraining = createMockTraining();
		when(updateTrainingUseCase.update(eq(trainingId), any(UpdateTrainingCommand.class))).thenReturn(mockTraining);

		mockMvc.perform(MockMvcRequestBuilders.put("/trainings/{id}", trainingId)
		        .contentType(MediaType.APPLICATION_JSON).content(requestJson))
		        .andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

		verify(updateTrainingUseCase).update(eq(trainingId), any(UpdateTrainingCommand.class));
	}

	@Test
	public void update_ReturnsBadRequest_WhenInvalidRequestProvided() throws Exception {
		UUID trainingId = UUID.randomUUID();
		TrainingUpdateRequest invalidRequest = new TrainingUpdateRequest();
		invalidRequest.setTrainingName("");
		String requestJson = objectMapper.writeValueAsString(invalidRequest);

		mockMvc.perform(MockMvcRequestBuilders.put("/trainings/{id}", trainingId)
		        .contentType(MediaType.APPLICATION_JSON).content(requestJson))
		        .andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void delete_ReturnsNoContent_WhenValidIdProvided() throws Exception {
		UUID trainingId = UUID.randomUUID();
		doNothing().when(updateTrainingUseCase).deleteById(trainingId);

		mockMvc.perform(
		        MockMvcRequestBuilders.delete("/trainings/{id}", trainingId).contentType(MediaType.APPLICATION_JSON))
		        .andExpect(MockMvcResultMatchers.status().isNoContent());

		verify(updateTrainingUseCase).deleteById(trainingId);
	}

	private Training createMockTraining() {
		Training mockTraining = mock(Training.class);
		Trainee mockTrainee = mock(Trainee.class);
		User mockTraineeUser = mock(User.class);
		Trainer mockTrainer = mock(Trainer.class);
		User mockTrainerUser = mock(User.class);
		TrainingType mockTrainingType = mock(TrainingType.class);

		when(mockTraining.getTrainee()).thenReturn(mockTrainee);
		when(mockTrainee.getUser()).thenReturn(mockTraineeUser);
		when(mockTraineeUser.getUsername()).thenReturn("trainee.username");

		when(mockTraining.getTrainer()).thenReturn(mockTrainer);
		when(mockTrainer.getUser()).thenReturn(mockTrainerUser);
		when(mockTrainerUser.getUsername()).thenReturn("trainer.username");

		when(mockTraining.getTrainingName()).thenReturn("Test Training");
		when(mockTraining.getTrainingDate()).thenReturn(LocalDate.now());
		when(mockTraining.getTrainingDuration()).thenReturn(60);

		when(mockTraining.getTrainingType()).thenReturn(mockTrainingType);
		when(mockTrainingType.getTrainingTypeName()).thenReturn("Strength");

		return mockTraining;
	}

	private TrainingCreateRequest createValidTrainingRequest() {
		TrainingCreateRequest request = new TrainingCreateRequest();
		request.setTraineeUsername("trainee.username");
		request.setTrainerUsername("trainer.username");
		request.setTrainingName("Strength Training");
		request.setTrainingDate(LocalDate.now().plusDays(1));
		request.setTrainingDuration(60);
		return request;
	}

	private TrainingUpdateRequest createValidTrainingUpdateRequest() {
		TrainingUpdateRequest request = new TrainingUpdateRequest();
		request.setTraineeUsername("trainee.username");
		request.setTrainerUsername("trainer.username");
		request.setTrainingName("Updated Strength Training");
		request.setTrainingDate(LocalDate.now().plusDays(2));
		request.setTrainingDuration(90);
		return request;
	}
}
