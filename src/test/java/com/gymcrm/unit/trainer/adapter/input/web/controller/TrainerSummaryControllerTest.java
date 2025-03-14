package com.gymcrm.unit.trainer.adapter.input.web.controller;

import static org.mockito.Mockito.when;

import com.gymcrm.trainer.adapter.input.web.controller.TrainerSummaryController;
import com.gymcrm.trainer.adapter.input.web.response.TrainerWorkloadSummary;
import com.gymcrm.trainer.application.port.input.LoadTrainerSummaryUseCase;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TrainerSummaryControllerTest {
	private MockMvc mockMvc;

	private LoadTrainerSummaryUseCase loadTrainerSummaryUseCase;

	@BeforeEach
	public void setup() {
		loadTrainerSummaryUseCase = Mockito.mock(LoadTrainerSummaryUseCase.class);

		TrainerSummaryController trainerSummaryController = new TrainerSummaryController(loadTrainerSummaryUseCase);

		mockMvc = MockMvcBuilders.standaloneSetup(trainerSummaryController).build();
	}

	@Test
	public void getAllTrainersWithWorkload_ReturnsTrainersList_WhenTrainersExist() throws Exception {
		List<TrainerWorkloadSummary> trainersList = createTrainerWorkloadSummaryList();
		when(loadTrainerSummaryUseCase.loadAllTrainersWithWorkload()).thenReturn(trainersList);

		mockMvc.perform(
		        MockMvcRequestBuilders.get("/users/me/trainers/with-workload").contentType(MediaType.APPLICATION_JSON))
		        .andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
		        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
		        .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("robert.williams"))
		        .andExpect(MockMvcResultMatchers.jsonPath("$[0].fullName").value("Robert Williams"))
		        .andExpect(MockMvcResultMatchers.jsonPath("$[0].specialization").value("Fitness Trainer"))
		        .andExpect(MockMvcResultMatchers.jsonPath("$[0].active").value(true))
		        .andExpect(MockMvcResultMatchers.jsonPath("$[0].currentMonthWorkload").value(19))
		        .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("jane.smith"))
		        .andExpect(MockMvcResultMatchers.jsonPath("$[1].fullName").value("Jane Smith"))
		        .andExpect(MockMvcResultMatchers.jsonPath("$[1].specialization").value("Yoga Instructor"))
		        .andExpect(MockMvcResultMatchers.jsonPath("$[1].active").value(true))
		        .andExpect(MockMvcResultMatchers.jsonPath("$[1].currentMonthWorkload").value(25));
	}

	@Test
	public void getAllTrainersWithWorkload_ReturnsEmptyList_WhenNoTrainersExist() throws Exception {
		List<TrainerWorkloadSummary> emptyList = new ArrayList<>();
		when(loadTrainerSummaryUseCase.loadAllTrainersWithWorkload()).thenReturn(emptyList);

		mockMvc.perform(
		        MockMvcRequestBuilders.get("/users/me/trainers/with-workload").contentType(MediaType.APPLICATION_JSON))
		        .andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
		        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
	}

	private List<TrainerWorkloadSummary> createTrainerWorkloadSummaryList() {
		List<TrainerWorkloadSummary> trainersList = new ArrayList<>();

		TrainerWorkloadSummary trainer1 = TrainerWorkloadSummary.builder().username("robert.williams")
		        .fullName("Robert Williams").specialization("Fitness Trainer").isActive(true).currentMonthWorkload(19)
		        .build();

		TrainerWorkloadSummary trainer2 = TrainerWorkloadSummary.builder().username("jane.smith").fullName("Jane Smith")
		        .specialization("Yoga Instructor").isActive(true).currentMonthWorkload(25).build();

		trainersList.add(trainer1);
		trainersList.add(trainer2);

		return trainersList;
	}
}
