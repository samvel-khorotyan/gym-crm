package com.gymcrm.unit.trainer.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gymcrm.trainer.adapter.input.web.response.TrainerDetailsWithWorkloadResponse;
import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.application.TrainerDetailsService;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.domain.User;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerDetailsServiceTest {
	@Mock
	private LoadTrainerWorkloadUseCase loadTrainerWorkloadUseCase;

	@Mock
	private LoadTrainerPort loadTrainerPort;

	@InjectMocks
	private TrainerDetailsService trainerDetailsService;

	private String username;
	private String firstName;
	private String lastName;
	private String specialization;
	private boolean isActive;
	private int summaryDuration;

	@BeforeEach
	void setUp() {
		username = "john.smith";
		firstName = "John";
		lastName = "Smith";
		specialization = "Fitness";
		isActive = true;
		summaryDuration = 25;
	}

	@Test
	void loadTrainerDetailsWithWorkload_ShouldReturnTrainerDetailsWithWorkload_WhenTrainerExists() {
		User user = new User();
		user.setUsername(username);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setIsActive(isActive);

		Trainer trainer = new Trainer();
		trainer.setUser(user);
		trainer.setSpecialization(specialization);

		TrainerMonthlyWorkloadResponse workloadResponse = TrainerMonthlyWorkloadResponse.builder().username(username)
		        .firstName(firstName).lastName(lastName).isActive(isActive).year(LocalDate.now().getYear())
		        .month(LocalDate.now().getMonthValue()).summaryDuration(summaryDuration).build();

		when(loadTrainerPort.findByUsername(username)).thenReturn(trainer);
		when(loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload(username)).thenReturn(workloadResponse);

		TrainerDetailsWithWorkloadResponse response = trainerDetailsService.loadTrainerDetailsWithWorkload(username);

		assertNotNull(response, "Response should not be null");
		assertEquals(username, response.getUsername(), "Username should match");
		assertEquals(firstName, response.getFirstName(), "First name should match");
		assertEquals(lastName, response.getLastName(), "Last name should match");
		assertEquals(specialization, response.getSpecialization(), "Specialization should match");
		assertEquals(isActive, response.isActive(), "Active status should match");
		assertEquals(summaryDuration, response.getCurrentMonthWorkload(), "Current month workload should match");

		verify(loadTrainerPort).findByUsername(username);
		verify(loadTrainerWorkloadUseCase).loadTrainerCurrentMonthWorkload(username);
	}

	@Test
	void loadTrainerDetailsWithWorkload_ShouldThrowTrainerNotFoundException_WhenTrainerDoesNotExist() {
		String nonExistentUsername = "non.existent";
		when(loadTrainerPort.findByUsername(nonExistentUsername))
		        .thenThrow(new TrainerNotFoundException("Trainer not found"));

		TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class,
		        () -> trainerDetailsService.loadTrainerDetailsWithWorkload(nonExistentUsername));
		assertEquals("Trainer not found", exception.getMessage());
	}

	@Test
	void loadTrainerDetailsWithWorkload_ShouldPropagateException_WhenWorkloadServiceFails() {
		User user = new User();
		user.setUsername(username);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setIsActive(isActive);

		Trainer trainer = new Trainer();
		trainer.setUser(user);
		trainer.setSpecialization(specialization);

		when(loadTrainerPort.findByUsername(username)).thenReturn(trainer);
		when(loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload(username))
		        .thenThrow(new RuntimeException("Service unavailable"));

		RuntimeException exception = assertThrows(RuntimeException.class,
		        () -> trainerDetailsService.loadTrainerDetailsWithWorkload(username));
		assertEquals("Service unavailable", exception.getMessage());

		verify(loadTrainerPort).findByUsername(username);
	}
}
