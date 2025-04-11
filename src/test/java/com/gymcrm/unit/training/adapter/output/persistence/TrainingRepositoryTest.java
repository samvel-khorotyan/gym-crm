package com.gymcrm.unit.training.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.training.adapter.output.persistence.TrainingPersistenceRepository;
import com.gymcrm.training.adapter.output.persistence.TrainingRepository;
import com.gymcrm.training.application.exception.TrainingNotFoundException;
import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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

	private Training sampleTraining;
	private UUID trainingId;
	private UUID traineeId;
	private UUID trainerId;

	@BeforeEach
	void setUp() {
		trainingId = UUID.randomUUID();
		traineeId = UUID.randomUUID();
		trainerId = UUID.randomUUID();
		sampleTraining = new Training(trainingId, "Strength Training", null, null, null, LocalDate.now(), 60);
	}

	@Test
	void findAll_ShouldReturnAllTrainings() {
		List<Training> trainings = List.of(sampleTraining);
		when(repository.findAll()).thenReturn(trainings);

		List<Training> result = trainingRepository.findAll();

		assertEquals(trainings, result);
		verify(repository).findAll();
	}

	@Test
	void findAllByTrainerUsernames_ShouldReturnTrainings() {
		List<String> usernames = List.of("trainer1");
		List<Training> trainings = List.of(sampleTraining);
		when(repository.findAllByTrainerUsernames(usernames)).thenReturn(trainings);

		List<Training> result = trainingRepository.findAllByTrainerUsernames(usernames);

		assertEquals(trainings, result);
		verify(repository).findAllByTrainerUsernames(usernames);
	}

	@Test
	void save_ShouldCallRepositorySave() {
		trainingRepository.save(sampleTraining);

		verify(repository).save(sampleTraining);
	}

	@Test
	void saveAll_ShouldCallRepositorySaveAll() {
		List<Training> trainings = List.of(sampleTraining);
		trainingRepository.saveAll(trainings);

		verify(repository).saveAll(trainings);
	}

	@Test
	void deleteByTraineeId_ShouldCallRepositoryDelete() {
		trainingRepository.deleteByTraineeId(traineeId);

		verify(repository).deleteAllByTraineeId(traineeId);
	}

	@Test
	void deleteById_ShouldCallRepositoryDelete() {
		trainingRepository.deleteById(trainingId);

		verify(repository).deleteTrainingById(trainingId);
	}

	@Test
  void findById_WhenTrainingExists_ShouldReturnTraining() {
    when(repository.findById(trainingId)).thenReturn(Optional.of(sampleTraining));

    Training result = trainingRepository.findById(trainingId);

    assertEquals(sampleTraining, result);
    verify(repository).findById(trainingId);
  }

	@Test
  void findById_WhenTrainingNotFound_ShouldThrowException() {
    when(repository.findById(trainingId)).thenReturn(Optional.empty());

    assertThrows(TrainingNotFoundException.class, () -> trainingRepository.findById(trainingId));
    verify(repository).findById(trainingId);
  }

	@Test
  void existsByTraineeAndTrainer_ShouldReturnTrue() {
    when(repository.existsByTraineeIdAndTrainerId(traineeId, trainerId)).thenReturn(true);

    boolean result = trainingRepository.existsByTraineeAndTrainer(traineeId, trainerId);

    assertTrue(result);
    verify(repository).existsByTraineeIdAndTrainerId(traineeId, trainerId);
  }

	@Test
	void findTraineeTrainingsByCriteria_WhenTraineeExists_ShouldReturnTrainings() {
		String username = "trainee1";
		LocalDate startDate = LocalDate.now().minusDays(1);
		LocalDate endDate = LocalDate.now();
		String trainerName = "John Doe";
		String trainingType = "Cardio";
		List<Training> trainings = List.of(sampleTraining);

		when(repository.existsByTraineeUserUsername(username)).thenReturn(true);
		when(repository.findAll(any(Specification.class))).thenReturn(trainings);

		List<Training> result = trainingRepository.findTraineeTrainingsByCriteria(username, startDate, endDate,
		        trainerName, trainingType);

		assertEquals(trainings, result);
		verify(repository).existsByTraineeUserUsername(username);
		verify(repository).findAll(any(Specification.class));
	}

	@Test
	void findTraineeTrainingsByCriteria_WhenTraineeNotFound_ShouldThrowException() {
		String username = "trainee1";
		when(repository.existsByTraineeUserUsername(username)).thenReturn(false);

		assertThrows(TraineeNotFoundException.class,
		        () -> trainingRepository.findTraineeTrainingsByCriteria(username, null, null, null, null));
		verify(repository).existsByTraineeUserUsername(username);
		verify(repository, never()).findAll(any(Specification.class));
	}

	@Test
	void findTrainerTrainingsByCriteria_WhenTrainerExists_ShouldReturnTrainings() {
		String username = "trainer1";
		LocalDate startDate = LocalDate.now().minusDays(1);
		LocalDate endDate = LocalDate.now();
		String traineeName = "Jane Doe";
		List<Training> trainings = List.of(sampleTraining);

		when(repository.existsByTrainerUserUsername(username)).thenReturn(true);
		when(repository.findAll(any(Specification.class))).thenReturn(trainings);

		List<Training> result = trainingRepository.findTrainerTrainingsByCriteria(username, startDate, endDate,
		        traineeName);

		assertEquals(trainings, result);
		verify(repository).existsByTrainerUserUsername(username);
		verify(repository).findAll(any(Specification.class));
	}

	@Test
	void findTrainerTrainingsByCriteria_WhenTrainerNotFound_ShouldThrowException() {
		String username = "trainer1";
		when(repository.existsByTrainerUserUsername(username)).thenReturn(false);

		assertThrows(TrainerNotFoundException.class,
		        () -> trainingRepository.findTrainerTrainingsByCriteria(username, null, null, null));
		verify(repository).existsByTrainerUserUsername(username);
		verify(repository, never()).findAll(any(Specification.class));
	}
}
