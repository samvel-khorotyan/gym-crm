package com.gymcrm.unit.trainer.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.adapter.input.web.response.TrainerWorkloadStatistics;
import com.gymcrm.trainer.application.TrainerWorkloadDashboardService;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.domain.User;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadDashboardServiceTest {
	@Mock
	private LoadTrainerPort loadTrainerPort;

	@Mock
	private LoadTrainerWorkloadUseCase loadTrainerWorkloadUseCase;

	@InjectMocks
	private TrainerWorkloadDashboardService trainerWorkloadDashboardService;

	private Trainer activeTrainer1;
	private Trainer activeTrainer2;
	private Trainer inactiveTrainer;
	private TrainerMonthlyWorkloadResponse workload1;
	private TrainerMonthlyWorkloadResponse workload2;
	private int currentYear;
	private int currentMonth;

	@BeforeEach
	void setUp() {
		currentYear = LocalDate.now().getYear();
		currentMonth = LocalDate.now().getMonthValue();

		User user1 = new User();
		user1.setUsername("john.doe");
		user1.setFirstName("John");
		user1.setLastName("Doe");
		user1.setIsActive(true);

		activeTrainer1 = new Trainer();
		activeTrainer1.setUser(user1);
		activeTrainer1.setSpecialization("Fitness");

		User user2 = new User();
		user2.setUsername("jane.smith");
		user2.setFirstName("Jane");
		user2.setLastName("Smith");
		user2.setIsActive(true);

		activeTrainer2 = new Trainer();
		activeTrainer2.setUser(user2);
		activeTrainer2.setSpecialization("Yoga");

		User user3 = new User();
		user3.setUsername("inactive.trainer");
		user3.setFirstName("Inactive");
		user3.setLastName("Trainer");
		user3.setIsActive(false);

		inactiveTrainer = new Trainer();
		inactiveTrainer.setUser(user3);
		inactiveTrainer.setSpecialization("Pilates");

		workload1 = TrainerMonthlyWorkloadResponse.builder().username("john.doe").firstName("John").lastName("Doe")
		        .isActive(true).year(currentYear).month(currentMonth).summaryDuration(120) // 2 hours in minutes
		        .build();

		workload2 = TrainerMonthlyWorkloadResponse.builder().username("jane.smith").firstName("Jane").lastName("Smith")
		        .isActive(true).year(currentYear).month(currentMonth).summaryDuration(300) // 5 hours in minutes
		        .build();
	}

	@Test
	void loadTrainerWorkloadStatistics_ShouldReturnCorrectStatistics_WhenTrainersExist() {
		List<Trainer> trainers = Arrays.asList(activeTrainer1, activeTrainer2, inactiveTrainer);
		when(loadTrainerPort.findAll()).thenReturn(trainers);
		when(loadTrainerWorkloadUseCase.loadTrainerMonthlyWorkload("john.doe", currentYear, currentMonth))
		        .thenReturn(workload1);
		when(loadTrainerWorkloadUseCase.loadTrainerMonthlyWorkload("jane.smith", currentYear, currentMonth))
		        .thenReturn(workload2);

		TrainerWorkloadStatistics result = trainerWorkloadDashboardService.loadTrainerWorkloadStatistics();

		assertNotNull(result, "Result should not be null");
		assertEquals(3, result.getTotalTrainers(), "Total trainers should be 3");
		assertEquals(2, result.getActiveTrainers(), "Active trainers should be 2");
		assertEquals(7, result.getTotalWorkloadHours(), "Total workload hours should be 7 (2+5)");
		assertEquals(3.5, result.getAverageWorkloadHours(), "Average workload hours should be 3.5 (7/2)");
		assertEquals(5, result.getMaxWorkloadHours(), "Max workload hours should be 5");
		assertEquals("Jane Smith", result.getMostBusyTrainer(), "Most busy trainer should be Jane Smith");

		Map<String, Integer> workloadBySpecialization = result.getWorkloadBySpecialization();
		assertNotNull(workloadBySpecialization, "Workload by specialization should not be null");
		assertEquals(2, workloadBySpecialization.get("Fitness"), "Fitness workload should be 2 hours");
		assertEquals(5, workloadBySpecialization.get("Yoga"), "Yoga workload should be 5 hours");
		assertEquals(2, workloadBySpecialization.size(), "Should have 2 specializations");

		verify(loadTrainerPort).findAll();
		verify(loadTrainerWorkloadUseCase).loadTrainerMonthlyWorkload("john.doe", currentYear, currentMonth);
		verify(loadTrainerWorkloadUseCase).loadTrainerMonthlyWorkload("jane.smith", currentYear, currentMonth);
		verify(loadTrainerWorkloadUseCase, times(0)).loadTrainerMonthlyWorkload("inactive.trainer", currentYear,
		        currentMonth);
	}

	@Test
  void loadTrainerWorkloadStatistics_ShouldReturnZeroValues_WhenNoTrainersExist() {
    when(loadTrainerPort.findAll()).thenReturn(new ArrayList<>());

    TrainerWorkloadStatistics result =
        trainerWorkloadDashboardService.loadTrainerWorkloadStatistics();

    assertNotNull(result, "Result should not be null");
    assertEquals(0, result.getTotalTrainers(), "Total trainers should be 0");
    assertEquals(0, result.getActiveTrainers(), "Active trainers should be 0");
    assertEquals(0, result.getTotalWorkloadHours(), "Total workload hours should be 0");
    assertEquals(0, result.getAverageWorkloadHours(), "Average workload hours should be 0");
    assertEquals(0, result.getMaxWorkloadHours(), "Max workload hours should be 0");
    assertEquals("", result.getMostBusyTrainer(), "Most busy trainer should be empty");
    assertTrue(
        result.getWorkloadBySpecialization().isEmpty(),
        "Workload by specialization should be empty");

    verify(loadTrainerPort).findAll();
    verify(loadTrainerWorkloadUseCase, times(0))
        .loadTrainerMonthlyWorkload(anyString(), anyInt(), anyInt());
  }

	@Test
	void loadTrainerWorkloadStatistics_ShouldHandleWorkloadServiceFailure() {
		List<Trainer> trainers = Arrays.asList(activeTrainer1, activeTrainer2);
		when(loadTrainerPort.findAll()).thenReturn(trainers);
		when(loadTrainerWorkloadUseCase.loadTrainerMonthlyWorkload("john.doe", currentYear, currentMonth))
		        .thenThrow(new RuntimeException("Service unavailable"));
		when(loadTrainerWorkloadUseCase.loadTrainerMonthlyWorkload("jane.smith", currentYear, currentMonth))
		        .thenReturn(workload2);

		TrainerWorkloadStatistics result = trainerWorkloadDashboardService.loadTrainerWorkloadStatistics();

		assertNotNull(result, "Result should not be null");
		assertEquals(2, result.getTotalTrainers(), "Total trainers should be 2");
		assertEquals(2, result.getActiveTrainers(), "Active trainers should be 2");
		assertEquals(5, result.getTotalWorkloadHours(), "Total workload hours should be 5");
		assertEquals(2.5, result.getAverageWorkloadHours(), "Average workload hours should be 2.5 (5/2)");
		assertEquals(5, result.getMaxWorkloadHours(), "Max workload hours should be 5");
		assertEquals("Jane Smith", result.getMostBusyTrainer(), "Most busy trainer should be Jane Smith");

		Map<String, Integer> workloadBySpecialization = result.getWorkloadBySpecialization();
		assertNotNull(workloadBySpecialization, "Workload by specialization should not be null");
		assertEquals(5, workloadBySpecialization.get("Yoga"), "Yoga workload should be 5 hours");
		assertEquals(1, workloadBySpecialization.size(), "Should have 1 specialization");

		verify(loadTrainerPort).findAll();
		verify(loadTrainerWorkloadUseCase).loadTrainerMonthlyWorkload("john.doe", currentYear, currentMonth);
		verify(loadTrainerWorkloadUseCase).loadTrainerMonthlyWorkload("jane.smith", currentYear, currentMonth);
	}

	@Test
	void loadTrainerWorkloadStatistics_ShouldReturnZeroValues_WhenOnlyInactiveTrainersExist() {
		List<Trainer> trainers = Collections.singletonList(inactiveTrainer);
		when(loadTrainerPort.findAll()).thenReturn(trainers);

		TrainerWorkloadStatistics result = trainerWorkloadDashboardService.loadTrainerWorkloadStatistics();

		assertNotNull(result, "Result should not be null");
		assertEquals(1, result.getTotalTrainers(), "Total trainers should be 1");
		assertEquals(0, result.getActiveTrainers(), "Active trainers should be 0");
		assertEquals(0, result.getTotalWorkloadHours(), "Total workload hours should be 0");
		assertEquals(0, result.getAverageWorkloadHours(), "Average workload hours should be 0");
		assertEquals(0, result.getMaxWorkloadHours(), "Max workload hours should be 0");
		assertEquals("", result.getMostBusyTrainer(), "Most busy trainer should be empty");
		assertTrue(result.getWorkloadBySpecialization().isEmpty(), "Workload by specialization should be empty");

		verify(loadTrainerPort).findAll();
		verify(loadTrainerWorkloadUseCase, times(0)).loadTrainerMonthlyWorkload(anyString(), anyInt(), anyInt());
	}
}
