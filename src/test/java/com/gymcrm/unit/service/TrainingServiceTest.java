package com.gymcrm.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.command.CreateTrainingCommand;
import com.gymcrm.domain.Training;
import com.gymcrm.factory.TrainingFactory;
import com.gymcrm.prot.LoadTrainingPort;
import com.gymcrm.prot.UpdateTrainingPort;
import com.gymcrm.service.TrainingService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
  @Mock private UpdateTrainingPort updateTrainingPort;

  @Mock private LoadTrainingPort loadTrainingPort;

  @Mock private TrainingFactory trainingFactory;

  @InjectMocks private TrainingService trainingService;

  private CreateTrainingCommand createTrainingCommand;
  private Training training;

  @BeforeEach
  void setUp() {
    createTrainingCommand = new CreateTrainingCommand();
    createTrainingCommand.setTrainingName("Strength Training");
    createTrainingCommand.setTrainingType(null);
    createTrainingCommand.setTrainerId(UUID.randomUUID());
    createTrainingCommand.setTraineeId(UUID.randomUUID());
    createTrainingCommand.setTrainingDate(null);
    createTrainingCommand.setTrainingDuration(60);

    training = new Training();
    training.setId(UUID.randomUUID());
    training.setTrainingName("Strength Training");
  }

  @Test
  void createTraining_shouldSaveTraining() {
    doNothing().when(updateTrainingPort).saveOrUpdate(training);
    when(trainingFactory.createFrom(createTrainingCommand)).thenReturn(training);

    trainingService.createTraining(createTrainingCommand);

    verify(trainingFactory).createFrom(createTrainingCommand);
    verify(updateTrainingPort).saveOrUpdate(training);
  }

  @Test
  void createTraining_shouldThrowExceptionWhenTrainingNameIsNull() {
    createTrainingCommand.setTrainingName(null);

    assertThrows(
        IllegalArgumentException.class,
        () -> trainingService.createTraining(createTrainingCommand));
  }

  @Test
  void createTraining_shouldThrowExceptionWhenTrainingNameIsEmpty() {
    createTrainingCommand.setTrainingName("");

    assertThrows(
        IllegalArgumentException.class,
        () -> trainingService.createTraining(createTrainingCommand));
  }

  @Test
  void getAllTrainings_shouldReturnListOfTrainings() {
    List<Training> trainings = List.of(training);
    when(loadTrainingPort.fetchAll()).thenReturn(trainings);

    List<Training> result = trainingService.getAllTrainings();

    assertEquals(trainings, result);
    verify(loadTrainingPort).fetchAll();
  }

  @Test
  void getTrainingById_shouldReturnTraining() {
    UUID trainingId = training.getId();
    when(loadTrainingPort.fetchById(trainingId)).thenReturn(training);

    Training result = trainingService.getTrainingById(trainingId);

    assertEquals(training, result);
    verify(loadTrainingPort).fetchById(trainingId);
  }

  @Test
  void getTrainingById_shouldThrowExceptionWhenIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> trainingService.getTrainingById(null));
  }

  @Test
  void getTrainingById_shouldThrowExceptionWhenTrainingNotFound() {
    UUID trainingId = UUID.randomUUID();
    when(loadTrainingPort.fetchById(trainingId)).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> trainingService.getTrainingById(trainingId));
  }
}
