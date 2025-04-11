package com.gymcrm.unit.training.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainee.application.port.output.LoadTraineePort;
import com.gymcrm.trainee.application.port.output.UpdateTraineePort;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerWorkloadPort;
import com.gymcrm.trainer.domain.ActionType;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.TrainingService;
import com.gymcrm.training.application.factory.TrainingFactory;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.application.port.output.LoadTrainingTypePort;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
	@Mock
	private UpdateTrainingPort updateTrainingPort;

	@Mock
	private LoadTrainingTypePort loadTrainingTypePort;

	@Mock
	private LoadTraineePort loadTraineePort;

	@Mock
	private LoadTrainerPort loadTrainerPort;

	@Mock
	private UpdateTraineePort updateTraineePort;

	@Mock
	private TrainingFactory trainingFactory;

	@Mock
	private LoadTrainingPort loadTrainingPort;

	@Mock
	private UpdateTrainerWorkloadPort updateTrainerWorkloadPort;

	@InjectMocks
	private TrainingService trainingService;

	@Test
	void create_ShouldCreateTrainingSuccessfully() {
		String traineeUsername = "trainee1";
		String trainerUsername = "trainer1";
		String trainingName = "Strength Training";
		LocalDate trainingDate = LocalDate.of(2025, 1, 20);
		Integer trainingDuration = 60;

		Trainee trainee = new Trainee();
		Trainer trainer = new Trainer();
		TrainingType trainingType = new TrainingType(UUID.randomUUID(), "Strength Training");
		Training training = new Training();

		CreateTrainingCommand command = new CreateTrainingCommand(traineeUsername, trainerUsername, trainingName,
		        trainingDate, trainingDuration);

		when(loadTraineePort.findByUsername(traineeUsername)).thenReturn(trainee);
		when(loadTrainerPort.findByUsername(trainerUsername)).thenReturn(trainer);
		when(loadTrainingTypePort.findByTrainingTypeName(trainingName)).thenReturn(trainingType);
		when(trainingFactory.createFrom(command)).thenReturn(training);
		doNothing().when(updateTrainerWorkloadPort).sendTrainerWorkload(training, ActionType.ADD);

		trainingService.create(command);

		verify(updateTraineePort).save(trainee);
		verify(updateTrainingPort).save(training);
		verify(updateTrainerWorkloadPort).sendTrainerWorkload(training, ActionType.ADD);
	}

	@Test
	void create_ShouldThrowTraineeNotFoundException() {
		String traineeUsername = "invalidTrainee";
		CreateTrainingCommand command = new CreateTrainingCommand(traineeUsername, "trainer1", "Yoga Training",
		        LocalDate.now(), 45);

		when(loadTraineePort.findByUsername(traineeUsername))
		        .thenThrow(new TraineeNotFoundException("Trainee not found"));

		assertThrows(TraineeNotFoundException.class, () -> trainingService.create(command));

		verifyNoInteractions(loadTrainerPort);
		verifyNoInteractions(updateTrainingPort);
		verifyNoInteractions(updateTrainerWorkloadPort);
	}

	@Test
	void create_ShouldThrowTrainerNotFoundException() {
		String traineeUsername = "trainee1";
		String trainerUsername = "invalidTrainer";
		CreateTrainingCommand command = new CreateTrainingCommand(traineeUsername, trainerUsername, "Yoga Training",
		        LocalDate.now(), 45);

		Trainee trainee = new Trainee();
		when(loadTraineePort.findByUsername(traineeUsername)).thenReturn(trainee);
		when(loadTrainerPort.findByUsername(trainerUsername))
		        .thenThrow(new TrainerNotFoundException("Trainer not found"));

		assertThrows(TrainerNotFoundException.class, () -> trainingService.create(command));

		verify(loadTraineePort).findByUsername(traineeUsername);
		verifyNoInteractions(updateTrainingPort);
		verifyNoInteractions(updateTrainerWorkloadPort);
	}

	@Test
	void create_ShouldThrowExceptionWhenTrainingTypeNotFound() {
		String traineeUsername = "trainee1";
		String trainerUsername = "trainer1";
		String trainingName = "Invalid Training";
		CreateTrainingCommand command = new CreateTrainingCommand(traineeUsername, trainerUsername, trainingName,
		        LocalDate.now(), 45);

		Trainee trainee = new Trainee();
		Trainer trainer = new Trainer();
		when(loadTraineePort.findByUsername(traineeUsername)).thenReturn(trainee);
		when(loadTrainerPort.findByUsername(trainerUsername)).thenReturn(trainer);
		when(loadTrainingTypePort.findByTrainingTypeName(trainingName))
		        .thenThrow(new RuntimeException("Training type not found"));

		assertThrows(RuntimeException.class, () -> trainingService.create(command));

		verify(loadTraineePort).findByUsername(traineeUsername);
		verify(loadTrainerPort).findByUsername(trainerUsername);
		verifyNoInteractions(updateTrainingPort);
		verifyNoInteractions(updateTrainerWorkloadPort);
	}

	@Test
	void create_ShouldAddTrainerToTraineeWhenTrainerListIsNull() {
		String traineeUsername = "trainee1";
		String trainerUsername = "trainer1";
		String trainingName = "Strength Training";
		LocalDate trainingDate = LocalDate.of(2025, 1, 20);
		Integer trainingDuration = 60;

		Trainee trainee = new Trainee();
		trainee.setTrainers(null);
		Trainer trainer = new Trainer();
		TrainingType trainingType = new TrainingType(UUID.randomUUID(), "Strength Training");
		Training training = new Training();

		CreateTrainingCommand command = new CreateTrainingCommand(traineeUsername, trainerUsername, trainingName,
		        trainingDate, trainingDuration);

		when(loadTraineePort.findByUsername(traineeUsername)).thenReturn(trainee);
		when(loadTrainerPort.findByUsername(trainerUsername)).thenReturn(trainer);
		when(loadTrainingTypePort.findByTrainingTypeName(trainingName)).thenReturn(trainingType);
		when(trainingFactory.createFrom(command)).thenReturn(training);
		doNothing().when(updateTrainerWorkloadPort).sendTrainerWorkload(training, ActionType.ADD);

		trainingService.create(command);

		verify(updateTraineePort).save(trainee);
		verify(updateTrainingPort).save(training);
		verify(updateTrainerWorkloadPort).sendTrainerWorkload(training, ActionType.ADD);
		assertNotNull(trainee.getTrainers(), "Trainee's trainers list should not be null");
		assertTrue(trainee.getTrainers().contains(trainer), "Trainer should be added to trainee's trainers list");
	}

	@Test
	void loadAll_ShouldReturnListOfTrainings_WhenTrainingsExist() {
		List<Training> mockTrainings = new ArrayList<>();
		mockTrainings.add(new Training());
		mockTrainings.add(new Training());

		when(loadTrainingPort.findAll()).thenReturn(mockTrainings);

		List<Training> result = trainingService.loadAll();

		assertNotNull(result, "Result should not be null");
		assertEquals(2, result.size(), "Result size should match the number of trainings");
		verify(loadTrainingPort, times(1)).findAll();
	}

	@Test
  void loadAll_ShouldReturnEmptyList_WhenNoTrainingsExist() {
    when(loadTrainingPort.findAll()).thenReturn(new ArrayList<>());

    List<Training> result = trainingService.loadAll();

    assertNotNull(result, "Result should not be null");
    assertTrue(result.isEmpty(), "Result should be an empty list");
    verify(loadTrainingPort, times(1)).findAll();
  }

	@Test
  void loadAll_ShouldThrowRuntimeException_WhenLoadTrainingPortFails() {
    when(loadTrainingPort.findAll()).thenThrow(new RuntimeException("Database error"));

    RuntimeException exception = assertThrows(RuntimeException.class, trainingService::loadAll);
    assertEquals(
        "Failed to fetch trainings", exception.getMessage(), "Exception message should match");
    verify(loadTrainingPort, times(1)).findAll();
  }

	@Test
	void findTraineeTrainingsByCriteria_ShouldReturnTrainings_WhenCriteriaMatch() {
		String username = "trainee1";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String trainerName = "Trainer A";
		String trainingType = "Strength";

		List<Training> mockTrainings = List.of(new Training(), new Training());

		when(loadTrainingPort.findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName, trainingType))
		        .thenReturn(mockTrainings);

		List<Training> result = trainingService.findTraineeTrainingsByCriteria(username, startDate, endDate,
		        trainerName, trainingType);

		assertNotNull(result, "Result should not be null");
		assertEquals(2, result.size(), "Result size should match the number of trainings");
		verify(loadTrainingPort, times(1)).findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName,
		        trainingType);
	}

	@Test
	void findTraineeTrainingsByCriteria_ShouldReturnEmptyList_WhenNoMatchFound() {
		String username = "trainee1";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String trainerName = "Trainer A";
		String trainingType = "Strength";

		when(loadTrainingPort.findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName, trainingType))
		        .thenReturn(new ArrayList<>());

		List<Training> result = trainingService.findTraineeTrainingsByCriteria(username, startDate, endDate,
		        trainerName, trainingType);

		assertNotNull(result, "Result should not be null");
		assertTrue(result.isEmpty(), "Result should be an empty list");
		verify(loadTrainingPort, times(1)).findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName,
		        trainingType);
	}

	@Test
	void findTraineeTrainingsByCriteria_ShouldThrowTraineeNotFoundException_WhenTraineeDoesNotExist() {
		String username = "nonexistent_trainee";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String trainerName = "Trainer A";
		String trainingType = "Strength";

		when(loadTrainingPort.findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName, trainingType))
		        .thenThrow(new TraineeNotFoundException("Trainee not found"));

		TraineeNotFoundException exception = assertThrows(TraineeNotFoundException.class, () -> trainingService
		        .findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName, trainingType));

		assertEquals("Trainee not found", exception.getMessage(), "Exception message should match");
		verify(loadTrainingPort, times(1)).findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName,
		        trainingType);
	}

	@Test
	void findTraineeTrainingsByCriteria_ShouldThrowRuntimeException_WhenUnexpectedErrorOccurs() {
		String username = "trainee1";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String trainerName = "Trainer A";
		String trainingType = "Strength";

		when(loadTrainingPort.findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName, trainingType))
		        .thenThrow(new RuntimeException("Unexpected error"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> trainingService
		        .findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName, trainingType));

		assertEquals("Failed to fetch trainings by criteria", exception.getMessage(), "Exception message should match");
		verify(loadTrainingPort, times(1)).findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName,
		        trainingType);
	}

	@Test
	void findTrainerTrainingsByCriteria_ShouldReturnTrainings_WhenCriteriaMatch() {
		String username = "trainer1";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String traineeName = "Trainee A";

		List<Training> mockTrainings = List.of(new Training(), new Training());

		when(loadTrainingPort.findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName))
		        .thenReturn(mockTrainings);

		List<Training> result = trainingService.findTrainerTrainingsByCriteria(username, startDate, endDate,
		        traineeName);

		assertEquals(2, result.size(), "Result size should match the number of trainings");
		verify(loadTrainingPort, times(1)).findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName);
	}

	@Test
	void findTrainerTrainingsByCriteria_ShouldReturnEmptyList_WhenNoMatchFound() {
		String username = "trainer1";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String traineeName = "Trainee A";

		when(loadTrainingPort.findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName))
		        .thenReturn(new ArrayList<>());

		List<Training> result = trainingService.findTrainerTrainingsByCriteria(username, startDate, endDate,
		        traineeName);

		assertNotNull(result, "Result should not be null");
		assertTrue(result.isEmpty(), "Result should be an empty list");
		verify(loadTrainingPort, times(1)).findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName);
	}

	@Test
	void findTrainerTrainingsByCriteria_ShouldThrowTrainerNotFoundException_WhenTrainerDoesNotExist() {
		String username = "nonexistent_trainer";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String traineeName = "Trainee A";

		when(loadTrainingPort.findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName))
		        .thenThrow(new TrainerNotFoundException("Trainer not found"));

		TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class,
		        () -> trainingService.findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName));

		assertEquals("Trainer not found", exception.getMessage(), "Exception message should match");
		verify(loadTrainingPort, times(1)).findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName);
	}

	@Test
	void findTrainerTrainingsByCriteria_ShouldThrowRuntimeException_WhenUnexpectedErrorOccurs() {
		String username = "trainer1";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String traineeName = "Trainee A";

		when(loadTrainingPort.findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName))
		        .thenThrow(new RuntimeException("Unexpected error"));

		RuntimeException exception = assertThrows(RuntimeException.class,
		        () -> trainingService.findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName));

		assertEquals("Failed to fetch trainings by criteria.", exception.getMessage(),
		        "Exception message should match");
		verify(loadTrainingPort, times(1)).findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName);
	}

	@Test
	void deleteByIdTraining_ShouldThrowException_WhenNotFound() {
		// Arrange
		UUID trainingId = UUID.randomUUID();
		when(loadTrainingPort.findById(trainingId)).thenThrow(new RuntimeException("Training not found"));

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> trainingService.deleteById(trainingId));
		assertEquals("Failed to delete training: Training not found", exception.getMessage());
		verify(updateTrainingPort, never()).deleteById(any());
		verify(updateTrainerWorkloadPort, never()).sendTrainerWorkload(any(), any());
	}
}
