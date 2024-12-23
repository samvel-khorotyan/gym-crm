package com.gymcrm.unit.training.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainee.application.port.input.TraineeUpdateUseCase;
import com.gymcrm.trainee.application.port.input.UpdateTraineeCommand;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.TrainingService;
import com.gymcrm.training.application.factory.TrainingFactory;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.time.LocalDate;
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

  @Mock private TraineeUpdateUseCase traineeUpdateUseCase;

  @InjectMocks private TrainingService trainingService;

  private CreateTrainingCommand validCommand;

  @BeforeEach
  void setUp() {
    Trainee trainee = new Trainee();
    Trainer trainer = new Trainer();
    TrainingType trainingType = new TrainingType();

    validCommand =
        new CreateTrainingCommand(
            "Wrestling Training", trainee, trainer, trainingType, LocalDate.now(), 60);
  }

  @Test
  void create_ShouldSaveTraining_WhenValidCommandIsProvided() {
    Training training = new Training();
    when(trainingFactory.createFrom(validCommand)).thenReturn(training);

    trainingService.create(validCommand);

    verify(trainingFactory, times(1)).createFrom(validCommand);
    verify(traineeUpdateUseCase, times(1)).updateTrainersOfTrainee(any(UpdateTraineeCommand.class));
    verify(updateTrainingPort, times(1)).save(training);
  }

  @Test
  void loadById_ShouldReturnTraining_WhenIdIsValid() {
    UUID trainingId = UUID.randomUUID();
    Training training = new Training();
    when(loadTrainingPort.findById(trainingId)).thenReturn(training);

    Training result = trainingService.loadById(trainingId);

    assertNotNull(result);
    assertEquals(training, result);
    verify(loadTrainingPort, times(1)).findById(trainingId);
  }

  @Test
  void loadById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> trainingService.loadById(null));

    assertEquals("Training ID cannot be null.", exception.getMessage());
  }

  @Test
  void loadAll_ShouldReturnListOfTrainings() {
    List<Training> trainings = List.of(new Training(), new Training());
    when(loadTrainingPort.findAll()).thenReturn(trainings);

    List<Training> result = trainingService.loadAll();

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(loadTrainingPort, times(1)).findAll();
  }

  @Test
  void findTraineeTrainingsByCriteria_ShouldReturnTrainings_WhenCriteriaProvided() {
    LocalDate startDate = LocalDate.now().minusDays(10);
    LocalDate endDate = LocalDate.now();
    String trainerName = "John Doe";
    String trainingType = "Wrestling";
    List<Training> trainings = List.of(new Training());
    when(loadTrainingPort.findTraineeTrainingsByCriteria(
            startDate, endDate, trainerName, trainingType))
        .thenReturn(trainings);

    List<Training> result =
        trainingService.findTraineeTrainingsByCriteria(
            startDate, endDate, trainerName, trainingType);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(loadTrainingPort, times(1))
        .findTraineeTrainingsByCriteria(startDate, endDate, trainerName, trainingType);
  }

  @Test
  void findTrainerTrainingsByCriteria_ShouldReturnTrainings_WhenCriteriaProvided() {
    LocalDate startDate = LocalDate.now().minusDays(10);
    LocalDate endDate = LocalDate.now();
    String traineeName = "Jane Doe";
    List<Training> trainings = List.of(new Training());
    when(loadTrainingPort.findTrainerTrainingsByCriteria(startDate, endDate, traineeName))
        .thenReturn(trainings);

    List<Training> result =
        trainingService.findTrainerTrainingsByCriteria(startDate, endDate, traineeName);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(loadTrainingPort, times(1))
        .findTrainerTrainingsByCriteria(startDate, endDate, traineeName);
  }
}
