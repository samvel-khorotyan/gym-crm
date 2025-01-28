package com.gymcrm.unit.training.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.training.adapter.output.persistence.TrainingPersistenceRepository;
import com.gymcrm.training.adapter.output.persistence.TrainingRepository;
import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class TrainingRepositoryTest {
	@Mock
	private TrainingPersistenceRepository repository;

	@InjectMocks
	private TrainingRepository trainingRepository;

	@Test
	void findAllByTrainerUsernames_ShouldReturnTrainings_WhenUsernamesExist() {
		List<String> trainerUsernames = List.of("trainer1", "trainer2");
		List<Training> trainings = List.of(new Training(), new Training());

		when(repository.findAllByTrainerUsernames(trainerUsernames)).thenReturn(trainings);

		List<Training> result = trainingRepository.findAllByTrainerUsernames(trainerUsernames);

		assertEquals(trainings, result);
		verify(repository, times(1)).findAllByTrainerUsernames(trainerUsernames);
	}

	@Test
	void findAllByTrainerUsernames_ShouldReturnEmptyList_WhenNoUsernamesProvided() {
		List<String> trainerUsernames = new ArrayList<>();

		when(repository.findAllByTrainerUsernames(trainerUsernames)).thenReturn(List.of());

		List<Training> result = trainingRepository.findAllByTrainerUsernames(trainerUsernames);

		assertTrue(result.isEmpty());
		verify(repository, times(1)).findAllByTrainerUsernames(trainerUsernames);
	}

	@Test
	void findAllByTrainerUsernames_ShouldReturnEmptyList_WhenNoTrainingsFound() {
		List<String> trainerUsernames = List.of("trainer1", "trainer2");

		when(repository.findAllByTrainerUsernames(trainerUsernames)).thenReturn(List.of());

		List<Training> result = trainingRepository.findAllByTrainerUsernames(trainerUsernames);

		assertTrue(result.isEmpty());
		verify(repository, times(1)).findAllByTrainerUsernames(trainerUsernames);
	}

	@Test
	void findAllByTrainerUsernames_ShouldThrowException_WhenRepositoryFails() {
		List<String> trainerUsernames = List.of("trainer1", "trainer2");

		when(repository.findAllByTrainerUsernames(trainerUsernames)).thenThrow(new RuntimeException("Database error"));

		RuntimeException exception = assertThrows(RuntimeException.class,
		        () -> trainingRepository.findAllByTrainerUsernames(trainerUsernames));

		assertEquals("Database error", exception.getMessage());
		verify(repository, times(1)).findAllByTrainerUsernames(trainerUsernames);
	}

	@Test
	void save_ShouldSaveTraining_WhenValidTrainingProvided() {
		Training training = new Training();

		trainingRepository.save(training);

		verify(repository, times(1)).save(training);
	}

	@Test
	void save_ShouldThrowException_WhenRepositoryFails() {
		Training training = new Training();
		doThrow(new RuntimeException("Database error")).when(repository).save(training);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> trainingRepository.save(training));

		assertEquals("Database error", exception.getMessage());
		verify(repository, times(1)).save(training);
	}

	@Test
	void save_ShouldThrowException_WhenTrainingHasMissingFields() {
		Training training = new Training();

		doThrow(new IllegalArgumentException("Missing required fields")).when(repository).save(training);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
		        () -> trainingRepository.save(training));

		assertEquals("Missing required fields", exception.getMessage());
		verify(repository, times(1)).save(training);
	}

	@Test
	void saveAll_ShouldSaveAllTrainings_WhenValidTrainingsProvided() {
		List<Training> trainings = List.of(new Training(), new Training());

		trainingRepository.saveAll(trainings);

		verify(repository, times(1)).saveAll(trainings);
	}

	@Test
	void saveAll_ShouldThrowException_WhenRepositoryFails() {
		List<Training> trainings = List.of(new Training(), new Training());
		doThrow(new RuntimeException("Database error")).when(repository).saveAll(trainings);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> trainingRepository.saveAll(trainings));

		assertEquals("Database error", exception.getMessage());
		verify(repository, times(1)).saveAll(trainings);
	}

	@Test
	void saveAll_ShouldThrowException_WhenTrainingsHaveMissingFields() {
		Training invalidTraining = new Training();
		List<Training> trainings = List.of(invalidTraining);

		doThrow(new IllegalArgumentException("Invalid training data")).when(repository).saveAll(trainings);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
		        () -> trainingRepository.saveAll(trainings));

		assertEquals("Invalid training data", exception.getMessage());
		verify(repository, times(1)).saveAll(trainings);
	}

	@Test
	void deleteByTraineeId_ShouldDeleteTrainings_WhenValidTraineeIdProvided() {
		UUID traineeId = UUID.randomUUID();
		doNothing().when(repository).deleteAllByTraineeId(traineeId);

		trainingRepository.deleteByTraineeId(traineeId);

		verify(repository, times(1)).deleteAllByTraineeId(traineeId);
	}

	@Test
	void deleteByTraineeId_ShouldNotThrowException_WhenTraineeIdDoesNotExist() {
		UUID traineeId = UUID.randomUUID();
		doNothing().when(repository).deleteAllByTraineeId(traineeId);

		trainingRepository.deleteByTraineeId(traineeId);

		verify(repository, times(1)).deleteAllByTraineeId(traineeId);
	}

	@Test
	void deleteByTraineeId_ShouldThrowException_WhenRepositoryFails() {
		UUID traineeId = UUID.randomUUID();
		doThrow(new RuntimeException("Database error")).when(repository).deleteAllByTraineeId(traineeId);

		RuntimeException exception = assertThrows(RuntimeException.class,
		        () -> trainingRepository.deleteByTraineeId(traineeId));

		assertEquals("Database error", exception.getMessage());
		verify(repository, times(1)).deleteAllByTraineeId(traineeId);
	}

	@Test
	void findTraineeTrainingsByCriteria_ShouldReturnTrainings_WhenCriteriaIsMet() {
		String username = "trainee123";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String trainerName = "Trainer A";
		String trainingType = "Yoga";

		List<Training> expectedTrainings = List.of(new Training(), new Training());
		when(repository.existsByTraineeUserUsername(username)).thenReturn(true);
		when(repository.findAll(any(Specification.class))).thenReturn(expectedTrainings);

		List<Training> result = trainingRepository.findTraineeTrainingsByCriteria(username, startDate, endDate,
		        trainerName, trainingType);

		assertEquals(expectedTrainings, result);
		verify(repository, times(1)).existsByTraineeUserUsername(username);
		verify(repository, times(1)).findAll(any(Specification.class));
	}

	@Test
	void findTraineeTrainingsByCriteria_ShouldThrowException_WhenTraineeNotFound() {
		String username = "nonexistent_trainee";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String trainerName = "Trainer A";
		String trainingType = "Yoga";

		when(repository.existsByTraineeUserUsername(username)).thenReturn(false);

		TraineeNotFoundException exception = assertThrows(TraineeNotFoundException.class, () -> trainingRepository
		        .findTraineeTrainingsByCriteria(username, startDate, endDate, trainerName, trainingType));

		assertEquals("Trainee not found by username: " + username, exception.getMessage());
		verify(repository, times(1)).existsByTraineeUserUsername(username);
		verify(repository, never()).findAll(any(Specification.class));
	}

	@Test
	void findTraineeTrainingsByCriteria_ShouldReturnAllTrainings_WhenCriteriaIsEmpty() {
		String username = "trainee123";
		LocalDate startDate = null;
		LocalDate endDate = null;
		String trainerName = null;
		String trainingType = null;

		List<Training> expectedTrainings = List.of(new Training());
		when(repository.existsByTraineeUserUsername(username)).thenReturn(true);
		when(repository.findAll(any(Specification.class))).thenReturn(expectedTrainings);

		List<Training> result = trainingRepository.findTraineeTrainingsByCriteria(username, startDate, endDate,
		        trainerName, trainingType);

		assertEquals(expectedTrainings, result);
		verify(repository, times(1)).existsByTraineeUserUsername(username);
		verify(repository, times(1)).findAll(any(Specification.class));
	}

	@Test
	void findTraineeTrainingsByCriteria_ShouldReturnEmptyList_WhenNoTrainingsMatch() {
		String username = "trainee123";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String trainerName = "Trainer A";
		String trainingType = "Yoga";

		when(repository.existsByTraineeUserUsername(username)).thenReturn(true);
		when(repository.findAll(any(Specification.class))).thenReturn(List.of());

		List<Training> result = trainingRepository.findTraineeTrainingsByCriteria(username, startDate, endDate,
		        trainerName, trainingType);

		assertTrue(result.isEmpty());
		verify(repository, times(1)).existsByTraineeUserUsername(username);
		verify(repository, times(1)).findAll(any(Specification.class));
	}

	@Test
	void findTrainerTrainingsByCriteria_ShouldReturnTrainings_WhenCriteriaIsMet() {
		String username = "trainer123";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String traineeName = "Trainee A";

		List<Training> expectedTrainings = List.of(new Training(), new Training());
		when(repository.existsByTrainerUserUsername(username)).thenReturn(true);
		when(repository.findAll(any(Specification.class))).thenReturn(expectedTrainings);

		List<Training> result = trainingRepository.findTrainerTrainingsByCriteria(username, startDate, endDate,
		        traineeName);

		assertEquals(expectedTrainings, result);
		verify(repository, times(1)).existsByTrainerUserUsername(username);
		verify(repository, times(1)).findAll(any(Specification.class));
	}

	@Test
	void findTrainerTrainingsByCriteria_ShouldThrowException_WhenTrainerNotFound() {
		String username = "nonexistent_trainer";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String traineeName = "Trainee A";

		when(repository.existsByTrainerUserUsername(username)).thenReturn(false);

		TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class,
		        () -> trainingRepository.findTrainerTrainingsByCriteria(username, startDate, endDate, traineeName));

		assertEquals("Trainer not found by username: " + username, exception.getMessage());
		verify(repository, times(1)).existsByTrainerUserUsername(username);
		verify(repository, never()).findAll(any(Specification.class));
	}

	@Test
	void findTrainerTrainingsByCriteria_ShouldReturnAllTrainings_WhenCriteriaIsEmpty() {
		String username = "trainer123";
		LocalDate startDate = null;
		LocalDate endDate = null;
		String traineeName = null;

		List<Training> expectedTrainings = List.of(new Training());
		when(repository.existsByTrainerUserUsername(username)).thenReturn(true);
		when(repository.findAll(any(Specification.class))).thenReturn(expectedTrainings);

		List<Training> result = trainingRepository.findTrainerTrainingsByCriteria(username, startDate, endDate,
		        traineeName);

		assertEquals(expectedTrainings, result);
		verify(repository, times(1)).existsByTrainerUserUsername(username);
		verify(repository, times(1)).findAll(any(Specification.class));
	}

	@Test
	void findTrainerTrainingsByCriteria_ShouldReturnEmptyList_WhenNoTrainingsMatch() {
		String username = "trainer123";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = LocalDate.of(2023, 12, 31);
		String traineeName = "Nonexistent Trainee";

		when(repository.existsByTrainerUserUsername(username)).thenReturn(true);
		when(repository.findAll(any(Specification.class))).thenReturn(List.of());

		List<Training> result = trainingRepository.findTrainerTrainingsByCriteria(username, startDate, endDate,
		        traineeName);

		assertTrue(result.isEmpty());
		verify(repository, times(1)).existsByTrainerUserUsername(username);
		verify(repository, times(1)).findAll(any(Specification.class));
	}

	@Test
	void findTrainerTrainingsByCriteria_ShouldFilterByPartialCriteria() {
		String username = "trainer123";
		LocalDate startDate = LocalDate.of(2023, 1, 1);
		LocalDate endDate = null; // Only filtering by start date
		String traineeName = "Trainee A";

		List<Training> expectedTrainings = List.of(new Training());
		when(repository.existsByTrainerUserUsername(username)).thenReturn(true);
		when(repository.findAll(any(Specification.class))).thenReturn(expectedTrainings);

		List<Training> result = trainingRepository.findTrainerTrainingsByCriteria(username, startDate, endDate,
		        traineeName);

		assertEquals(expectedTrainings, result);
		verify(repository, times(1)).existsByTrainerUserUsername(username);
		verify(repository, times(1)).findAll(any(Specification.class));
	}
}
