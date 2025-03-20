package com.gymcrm.unit.training.adapter.input.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymcrm.training.adapter.input.web.controller.TrainingController;
import com.gymcrm.training.adapter.input.web.request.TrainingCreateRequest;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import com.gymcrm.training.application.port.input.UpdateTrainingUseCase;
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
	private UpdateTrainingUseCase updateTrainingUseCase;

	private ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		trainingCreationUseCase = Mockito.mock(TrainingCreationUseCase.class);
		updateTrainingUseCase = Mockito.mock(UpdateTrainingUseCase.class);

		TrainingController trainingController = new TrainingController(trainingCreationUseCase, updateTrainingUseCase);

		mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();

		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Test
	public void create_ReturnsCreated_WhenValidRequestProvided() throws Exception {
		TrainingCreateRequest request = createValidTrainingRequest();
		String requestJson = objectMapper.writeValueAsString(request);

		doNothing().when(trainingCreationUseCase).create(any(CreateTrainingCommand.class));

		mockMvc.perform(MockMvcRequestBuilders.post("/users/me/trainings").contentType(MediaType.APPLICATION_JSON)
		        .content(requestJson)).andExpect(MockMvcResultMatchers.status().isCreated());

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
	public void delete_ReturnsNoContent_WhenValidIdProvided() throws Exception {
		UUID trainingId = UUID.randomUUID();
		doNothing().when(updateTrainingUseCase).deleteTraining(trainingId);

		mockMvc.perform(
		        MockMvcRequestBuilders.delete("/trainings/{id}", trainingId).contentType(MediaType.APPLICATION_JSON))
		        .andExpect(MockMvcResultMatchers.status().isNoContent());

		verify(updateTrainingUseCase).deleteTraining(trainingId);
	}

	private TrainingCreateRequest createValidTrainingRequest() {
		return new TrainingCreateRequest("trainee.username", "trainer.username", "Strength Training",
		        LocalDate.now().plusDays(1), 60);
	}
}
