package com.gymcrm.unit.trainer.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.adapter.input.web.response.TrainerWorkloadSummary;
import com.gymcrm.trainer.application.TrainerSummaryService;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.domain.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerSummaryServiceTest {
	@Mock
	private LoadTrainerPort loadTrainerPort;

	@Mock
	private LoadTrainerWorkloadUseCase loadTrainerWorkloadUseCase;

	@InjectMocks
	private TrainerSummaryService trainerSummaryService;

	private Trainer trainer1;
	private Trainer trainer2;
	private TrainerMonthlyWorkloadResponse workload1;
	private TrainerMonthlyWorkloadResponse workload2;

	@BeforeEach
	void setUp() {
		User user1 = new User();
		user1.setUsername("john.doe");
		user1.setFirstName("John");
		user1.setLastName("Doe");
		user1.setIsActive(true);

		trainer1 = new Trainer();
		trainer1.setUser(user1);
		trainer1.setSpecialization("Fitness");

		User user2 = new User();
		user2.setUsername("jane.smith");
		user2.setFirstName("Jane");
		user2.setLastName("Smith");
		user2.setIsActive(false);

		trainer2 = new Trainer();
		trainer2.setUser(user2);
		trainer2.setSpecialization("Yoga");

		workload1 = TrainerMonthlyWorkloadResponse.builder().username("john.doe").firstName("John").lastName("Doe")
		        .isActive(true).year(LocalDate.now().getYear()).month(LocalDate.now().getMonthValue())
		        .summaryDuration(30).build();

		workload2 = TrainerMonthlyWorkloadResponse.builder().username("jane.smith").firstName("Jane").lastName("Smith")
		        .isActive(false).year(LocalDate.now().getYear()).month(LocalDate.now().getMonthValue())
		        .summaryDuration(15).build();
	}

	@Test
	void loadAllTrainersWithWorkload_ShouldReturnTrainersList_WhenTrainersExist() {
		List<Trainer> trainers = Arrays.asList(trainer1, trainer2);
		when(loadTrainerPort.findAll()).thenReturn(trainers);
		when(loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload("john.doe")).thenReturn(workload1);
		when(loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload("jane.smith")).thenReturn(workload2);

		List<TrainerWorkloadSummary> result = trainerSummaryService.loadAllTrainersWithWorkload();

		assertNotNull(result, "Result should not be null");
		assertEquals(2, result.size(), "Should return 2 trainers");

		TrainerWorkloadSummary summary1 = result.get(0);
		assertEquals("john.doe", summary1.getUsername(), "Username should match");
		assertEquals("John Doe", summary1.getFullName(), "Full name should match");
		assertEquals("Fitness", summary1.getSpecialization(), "Specialization should match");
		assertTrue(summary1.isActive(), "Active status should match");
		assertEquals(30, summary1.getCurrentMonthWorkload(), "Current month workload should match");

		TrainerWorkloadSummary summary2 = result.get(1);
		assertEquals("jane.smith", summary2.getUsername(), "Username should match");
		assertEquals("Jane Smith", summary2.getFullName(), "Full name should match");
		assertEquals("Yoga", summary2.getSpecialization(), "Specialization should match");
		assertFalse(summary2.isActive(), "Active status should match");
		assertEquals(15, summary2.getCurrentMonthWorkload(), "Current month workload should match");

		verify(loadTrainerPort).findAll();
		verify(loadTrainerWorkloadUseCase).loadTrainerCurrentMonthWorkload("john.doe");
		verify(loadTrainerWorkloadUseCase).loadTrainerCurrentMonthWorkload("jane.smith");
	}

	@Test
  void loadAllTrainersWithWorkload_ShouldReturnEmptyList_WhenNoTrainersExist() {
    when(loadTrainerPort.findAll()).thenReturn(new ArrayList<>());

    List<TrainerWorkloadSummary> result = trainerSummaryService.loadAllTrainersWithWorkload();

    assertNotNull(result, "Result should not be null");
    assertTrue(result.isEmpty(), "Result should be empty");
    verify(loadTrainerPort).findAll();
    verify(loadTrainerWorkloadUseCase, times(0)).loadTrainerCurrentMonthWorkload(anyString());
  }

	@Test
	void loadAllTrainersWithWorkload_ShouldHandleWorkloadServiceFailure() {
		List<Trainer> trainers = Arrays.asList(trainer1, trainer2);
		when(loadTrainerPort.findAll()).thenReturn(trainers);
		when(loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload("john.doe"))
		        .thenThrow(new RuntimeException("Service unavailable"));
		when(loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload("jane.smith")).thenReturn(workload2);

		List<TrainerWorkloadSummary> result = trainerSummaryService.loadAllTrainersWithWorkload();

		assertNotNull(result, "Result should not be null");
		assertEquals(2, result.size(), "Should return 2 trainers");

		TrainerWorkloadSummary summary1 = result.get(0);
		assertEquals("john.doe", summary1.getUsername(), "Username should match");
		assertEquals(0, summary1.getCurrentMonthWorkload(), "Current month workload should be 0 for failed service");

		TrainerWorkloadSummary summary2 = result.get(1);
		assertEquals("jane.smith", summary2.getUsername(), "Username should match");
		assertEquals(15, summary2.getCurrentMonthWorkload(), "Current month workload should match");

		verify(loadTrainerPort).findAll();
		verify(loadTrainerWorkloadUseCase).loadTrainerCurrentMonthWorkload("john.doe");
		verify(loadTrainerWorkloadUseCase).loadTrainerCurrentMonthWorkload("jane.smith");
	}
}
