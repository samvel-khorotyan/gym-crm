package com.gymcrm.unit.trainer.adapter.input.web.controller;

import static org.mockito.Mockito.when;

import com.gymcrm.trainer.adapter.input.web.controller.TrainerDetailsController;
import com.gymcrm.trainer.adapter.input.web.response.TrainerDetailsWithWorkloadResponse;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.input.LoadTrainerDetailsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TrainerDetailsControllerTest {
	private MockMvc mockMvc;

	private LoadTrainerDetailsUseCase loadTrainerDetailsUseCase;

	private String username;
	private String firstName;
	private String lastName;
	private String specialization;
	private boolean isActive;
	private Integer currentMonthWorkload;

	@BeforeEach
	public void setup() {
		loadTrainerDetailsUseCase = Mockito.mock(LoadTrainerDetailsUseCase.class);

		TrainerDetailsController trainerDetailsController = new TrainerDetailsController(loadTrainerDetailsUseCase);

		mockMvc = MockMvcBuilders.standaloneSetup(trainerDetailsController).build();

		username = "robert.williams";
		firstName = "Robert";
		lastName = "Williams";
		specialization = "Fitness Trainer";
		isActive = true;
		currentMonthWorkload = 19;
	}

	@Test
  public void getTrainerDetailsWithWorkload_ReturnsTrainerDetails_WhenUsernameIsValid()
      throws Exception {
    when(loadTrainerDetailsUseCase.loadTrainerDetailsWithWorkload(username))
        .thenReturn(getTrainerDetailsWithWorkloadResponse());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    "/users/me/trainers/{username}/details-with-workload", username)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(firstName))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(lastName))
        .andExpect(MockMvcResultMatchers.jsonPath("$.specialization").value(specialization))
        .andExpect(MockMvcResultMatchers.jsonPath("$.active").value(isActive))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.currentMonthWorkload").value(currentMonthWorkload));
  }

	@Test
	public void getTrainerDetailsWithWorkload_ReturnsNotFound_WhenTrainerDoesNotExist() throws Exception {
		String nonExistentUsername = "non.existent";
		when(loadTrainerDetailsUseCase.loadTrainerDetailsWithWorkload(nonExistentUsername))
		        .thenThrow(new TrainerNotFoundException("Trainer not found"));

		mockMvc.perform(
		        MockMvcRequestBuilders.get("/users/me/trainers/{username}/details-with-workload", nonExistentUsername)
		                .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void getTrainerDetailsWithWorkload_ReturnsTrainerDetailsWithZeroWorkload_WhenWorkloadServiceUnavailable()
	        throws Exception {
		TrainerDetailsWithWorkloadResponse response = getTrainerDetailsWithWorkloadResponse();
		response.setCurrentMonthWorkload(0);

		when(loadTrainerDetailsUseCase.loadTrainerDetailsWithWorkload(username)).thenReturn(response);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/me/trainers/{username}/details-with-workload", username)
		        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.jsonPath("$.currentMonthWorkload").value(0));
	}

	private TrainerDetailsWithWorkloadResponse getTrainerDetailsWithWorkloadResponse() {
		return TrainerDetailsWithWorkloadResponse.builder().username(username).firstName(firstName).lastName(lastName)
		        .specialization(specialization).isActive(isActive).currentMonthWorkload(currentMonthWorkload).build();
	}
}
