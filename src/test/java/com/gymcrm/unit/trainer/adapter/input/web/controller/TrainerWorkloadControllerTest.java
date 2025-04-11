package com.gymcrm.unit.trainer.adapter.input.web.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.gymcrm.trainer.adapter.input.web.controller.TrainerWorkloadController;
import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

class TrainerWorkloadControllerTest {
	private MockMvc mockMvc;

	private LoadTrainerWorkloadUseCase loadTrainerWorkloadUseCase;

	private String username;
	private int year;
	private int month;
	private int summaryDuration;
	private boolean isActive;
	private String firstName;
	private String lastName;

	@BeforeEach
	public void setup() {
		loadTrainerWorkloadUseCase = Mockito.mock(LoadTrainerWorkloadUseCase.class);

		TrainerWorkloadController trainerWorkloadController = new TrainerWorkloadController(loadTrainerWorkloadUseCase);

		mockMvc = MockMvcBuilders.standaloneSetup(trainerWorkloadController).build();

		username = "robert.williams";
		firstName = "Robert";
		lastName = "Williams";
		isActive = true;
		summaryDuration = 19;

		LocalDate now = LocalDate.now();
		year = now.getYear();
		month = now.getMonthValue();
	}

	@Test
	public void getTrainerMonthlyWorkload_ReturnsWorkloadData_WhenValidParametersProvided() throws Exception {
		TrainerMonthlyWorkloadResponse response = createTrainerMonthlyWorkloadResponse(year, month);
		when(loadTrainerWorkloadUseCase.loadTrainerMonthlyWorkload(username, year, month)).thenReturn(response);

		mockMvc.perform(MockMvcRequestBuilders
		        .get("/users/me/trainers/{username}/workload/{year}/{month}", username, year, month)
		        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(firstName))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(lastName))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(isActive))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.year").value(year))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.month").value(month))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.summaryDuration").value(summaryDuration));
	}

	@Test
	public void getTrainerMonthlyWorkload_ReturnsError_WhenTrainerNotFound() {
		String nonExistentUsername = "non.existent";
		when(loadTrainerWorkloadUseCase.loadTrainerMonthlyWorkload(nonExistentUsername, year, month))
		        .thenThrow(new RuntimeException("Trainer not found"));

		Exception exception = assertThrows(NestedServletException.class,
		        () -> mockMvc.perform(MockMvcRequestBuilders
		                .get("/users/me/trainers/{username}/workload/{year}/{month}", nonExistentUsername, year, month)
		                .contentType(MediaType.APPLICATION_JSON)));

		assertTrue(exception.getCause() instanceof RuntimeException);
		assertTrue(exception.getCause().getMessage().contains("Trainer not found"));
	}

	@Test
	public void getTrainerCurrentMonthWorkload_ReturnsWorkloadData_WhenValidUsernameProvided() throws Exception {
		TrainerMonthlyWorkloadResponse response = createTrainerMonthlyWorkloadResponse(year, month);
		when(loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload(username)).thenReturn(response);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/me/trainers/{username}/workload/current", username)
		        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(firstName))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(lastName))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(isActive))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.year").value(year))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.month").value(month))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.summaryDuration").value(summaryDuration));
	}

	@Test
	public void getTrainerCurrentMonthWorkload_ReturnsError_WhenTrainerNotFound() {
		String nonExistentUsername = "non.existent";
		when(loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload(nonExistentUsername))
		        .thenThrow(new RuntimeException("Trainer not found"));

		Exception exception = assertThrows(NestedServletException.class,
		        () -> mockMvc.perform(MockMvcRequestBuilders
		                .get("/users/me/trainers/{username}/workload/current", nonExistentUsername)
		                .contentType(MediaType.APPLICATION_JSON)));

		assertTrue(exception.getCause() instanceof RuntimeException);
		assertTrue(exception.getCause().getMessage().contains("Trainer not found"));
	}

	@Test
	public void getTrainerMonthlyWorkload_ReturnsZeroWorkload_WhenCircuitBreakerTriggered() throws Exception {
		TrainerMonthlyWorkloadResponse fallbackResponse = createTrainerMonthlyWorkloadResponseWithZeroWorkload(year,
		        month);
		when(loadTrainerWorkloadUseCase.loadTrainerMonthlyWorkload(username, year, month)).thenReturn(fallbackResponse);

		mockMvc.perform(MockMvcRequestBuilders
		        .get("/users/me/trainers/{username}/workload/{year}/{month}", username, year, month)
		        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.jsonPath("$.summaryDuration").value(0));
	}

	private TrainerMonthlyWorkloadResponse createTrainerMonthlyWorkloadResponse(int year, int month) {
		return TrainerMonthlyWorkloadResponse.builder().username(username).firstName(firstName).lastName(lastName)
		        .isActive(isActive).year(year).month(month).summaryDuration(summaryDuration).build();
	}

	private TrainerMonthlyWorkloadResponse createTrainerMonthlyWorkloadResponseWithZeroWorkload(int year, int month) {
		return TrainerMonthlyWorkloadResponse.builder().username(username).firstName(firstName).lastName(lastName)
		        .isActive(isActive).year(year).month(month).summaryDuration(0).build();
	}
}
