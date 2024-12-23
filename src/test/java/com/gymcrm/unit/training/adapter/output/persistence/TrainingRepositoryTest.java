package com.gymcrm.unit.training.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
  @Mock private TrainingPersistenceRepository repository;

  @InjectMocks private TrainingRepository trainingRepository;

  private Training training;
  private UUID trainingId;

  @BeforeEach
  void setUp() {
    trainingId = UUID.randomUUID();
    training = new Training(trainingId, "Wrestling Session", null, null, null, LocalDate.now(), 60);
  }

  @Test
  void save_ShouldSaveTraining() {
    when(repository.save(training)).thenReturn(training);

    trainingRepository.save(training);

    verify(repository, times(1)).save(training);
  }

  @Test
  void findById_ShouldReturnTraining_WhenTrainingExists() {
    when(repository.findById(trainingId)).thenReturn(Optional.of(training));

    Training result = trainingRepository.findById(trainingId);

    assertNotNull(result);
    assertEquals(training, result);
    verify(repository, times(1)).findById(trainingId);
  }

  @Test
  void findById_ShouldThrowTrainingNotFoundException_WhenTrainingDoesNotExist() {
    when(repository.findById(trainingId)).thenReturn(Optional.empty());

    assertThrows(TrainingNotFoundException.class, () -> trainingRepository.findById(trainingId));
    verify(repository, times(1)).findById(trainingId);
  }

  @Test
  void findAll_ShouldReturnAllTrainings() {
    Training anotherTraining =
        new Training(UUID.randomUUID(), "Pilates Session", null, null, null, LocalDate.now(), 45);
    when(repository.findAll()).thenReturn(List.of(training, anotherTraining));

    List<Training> result = trainingRepository.findAll();

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(repository, times(1)).findAll();
  }

  @Test
  void findTraineeTrainingsByCriteria_ShouldReturnFilteredTrainings() {
    Specification spec = mock(Specification.class);
    LocalDate startDate = LocalDate.now().minusDays(5);
    LocalDate endDate = LocalDate.now().plusDays(5);
    when(repository.findAll(any(Specification.class))).thenReturn(List.of(training));

    List<Training> result =
        trainingRepository.findTraineeTrainingsByCriteria(
            startDate, endDate, "John Doe", "Wrestling");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(training, result.get(0));
    verify(repository, times(1)).findAll(any(Specification.class));
  }

  @Test
  void findTrainerTrainingsByCriteria_ShouldReturnFilteredTrainings() {
    Specification spec = mock(Specification.class);
    LocalDate startDate = LocalDate.now().minusDays(5);
    LocalDate endDate = LocalDate.now().plusDays(5);
    when(repository.findAll(any(Specification.class))).thenReturn(List.of(training));

    List<Training> result =
        trainingRepository.findTrainerTrainingsByCriteria(startDate, endDate, "Jane Doe");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(training, result.get(0));
    verify(repository, times(1)).findAll(any(Specification.class));
  }
}
