package com.gymcrm.unit.trainer.adapter.input.web.controller;

import static org.mockito.Mockito.when;

import com.gymcrm.trainer.adapter.input.web.controller.TrainerWorkloadDashboardController;
import com.gymcrm.trainer.adapter.input.web.response.TrainerWorkloadStatistics;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadDashboardUseCase;
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

class TrainerWorkloadDashboardControllerTest {
	private MockMvc mockMvc;

	private LoadTrainerWorkloadDashboardUseCase loadTrainerWorkloadDashboardUseCase;

	@BeforeEach
	public void setup() {
		loadTrainerWorkloadDashboardUseCase = Mockito.mock(LoadTrainerWorkloadDashboardUseCase.class);

		TrainerWorkloadDashboardController trainerWorkloadDashboardController = new TrainerWorkloadDashboardController(
		        loadTrainerWorkloadDashboardUseCase);

		mockMvc = MockMvcBuilders.standaloneSetup(trainerWorkloadDashboardController).build();
	}

	@Test
	public void getTrainerWorkloadStatistics_ReturnsStatistics_WhenDataExists() throws Exception {
		TrainerWorkloadStatistics statistics = createTrainerWorkloadStatistics();
		when(loadTrainerWorkloadDashboardUseCase.loadTrainerWorkloadStatistics()).thenReturn(statistics);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/me/trainers/dashboard/trainer-workload")
		        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.jsonPath("$.totalTrainers").value(10))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.activeTrainers").value(8))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.totalWorkloadHours").value(250))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.averageWorkloadHours").value(25.5))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.maxWorkloadHours").value(40))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.mostBusyTrainer").value("john.smith"))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.workloadBySpecialization.Fitness").value(120))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.workloadBySpecialization.Yoga").value(80))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.workloadBySpecialization.Pilates").value(50));
	}

	@Test
	public void getTrainerWorkloadStatistics_ReturnsEmptyStatistics_WhenNoDataExists() throws Exception {
		TrainerWorkloadStatistics emptyStatistics = createEmptyTrainerWorkloadStatistics();
		when(loadTrainerWorkloadDashboardUseCase.loadTrainerWorkloadStatistics()).thenReturn(emptyStatistics);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/me/trainers/dashboard/trainer-workload")
		        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.jsonPath("$.totalTrainers").value(0))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.activeTrainers").value(0))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.totalWorkloadHours").value(0))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.averageWorkloadHours").value(0.0))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.maxWorkloadHours").value(0))
		        .andExpect(MockMvcResultMatchers.jsonPath("$.mostBusyTrainer").isEmpty())
		        .andExpect(MockMvcResultMatchers.jsonPath("$.workloadBySpecialization").isEmpty());
	}

	private TrainerWorkloadStatistics createTrainerWorkloadStatistics() {
		Map<String, Integer> workloadBySpecialization = new HashMap<>();
		workloadBySpecialization.put("Fitness", 120);
		workloadBySpecialization.put("Yoga", 80);
		workloadBySpecialization.put("Pilates", 50);

		return TrainerWorkloadStatistics.builder().totalTrainers(10).activeTrainers(8).totalWorkloadHours(250)
		        .averageWorkloadHours(25.5).maxWorkloadHours(40).mostBusyTrainer("john.smith")
		        .workloadBySpecialization(workloadBySpecialization).build();
	}

	private TrainerWorkloadStatistics createEmptyTrainerWorkloadStatistics() {
		return TrainerWorkloadStatistics.builder().totalTrainers(0).activeTrainers(0).totalWorkloadHours(0)
		        .averageWorkloadHours(0.0).maxWorkloadHours(0).mostBusyTrainer("")
		        .workloadBySpecialization(new HashMap<>()).build();
	}
}
