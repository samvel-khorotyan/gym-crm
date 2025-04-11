package com.gymcrm.unit.trainingtype.adapter.input.web.controller;

import static org.mockito.Mockito.when;

import com.gymcrm.trainingtype.adapter.input.web.controller.TrainingTypeController;
import com.gymcrm.trainingtype.application.port.input.LoadTrainingTypeUseCase;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TrainingTypeControllerTest {
	private MockMvc mockMvc;

	private LoadTrainingTypeUseCase loadTrainingTypeUseCase;

	private UUID id;
	private String trainingTypeName;

	@BeforeEach
	public void setup() {
		loadTrainingTypeUseCase = Mockito.mock(LoadTrainingTypeUseCase.class);

		TrainingTypeController trainingTypeController = new TrainingTypeController(loadTrainingTypeUseCase);

		mockMvc = MockMvcBuilders.standaloneSetup(trainingTypeController).build();

		id = UUID.randomUUID();
		trainingTypeName = "Cardio Training";
	}

	@Test
  public void testLoadAllTrainingTypes_ShouldReturnOk() throws Exception {
    when(loadTrainingTypeUseCase.loadAll()).thenReturn(new ArrayList<>(List.of(getTrainingType())));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/users/me/training-types")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

	private TrainingType getTrainingType() {
		return new TrainingType(id, trainingTypeName);
	}
}
