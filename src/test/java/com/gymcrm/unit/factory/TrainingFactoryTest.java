package com.gymcrm.unit.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.factory.TrainingFactory;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingFactoryTest {
  @Mock private UUIDGeneratorInterface uuidGeneratorInterface;

  @InjectMocks private TrainingFactory trainingFactory;

  @Test
  void testCreateFrom_createsTrainingWithCorrectFields() {
    UUID generatedUUID = UUID.randomUUID();
    UUID traineeId = UUID.randomUUID();
    UUID trainerId = UUID.randomUUID();
    String trainingName = "Advanced Gym Techniques";
    TrainingType trainingType = new TrainingType(UUID.randomUUID(), "Weightlifting");
    LocalDate trainingDate = LocalDate.of(2023, 12, 1);
    Integer trainingDuration = 90;

    Trainee trainee = new Trainee(traineeId, LocalDate.of(2000, 1, 1), "123 Main St", null);
    Trainer trainer = new Trainer(trainerId, "Fitness Specialist", null);

    CreateTrainingCommand command =
        new CreateTrainingCommand(
            trainingName, trainee, trainer, trainingType, trainingDate, trainingDuration);

    when(uuidGeneratorInterface.newUUID()).thenReturn(generatedUUID);

    Training training = trainingFactory.createFrom(command);

    assertNotNull(training, "Training object should not be null");
    assertEquals(generatedUUID, training.getId(), "Training ID should match generated UUID");
    assertEquals(trainee, training.getTrainee(), "Trainee should match command");
    assertEquals(trainer, training.getTrainer(), "Trainer should match command");
    assertEquals(trainingName, training.getTrainingName(), "Training name should match command");
    assertEquals(trainingType, training.getTrainingType(), "Training type should match command");
    assertEquals(trainingDate, training.getTrainingDate(), "Training date should match command");
    assertEquals(
        trainingDuration, training.getTrainingDuration(), "Training duration should match command");

    verify(uuidGeneratorInterface, times(1)).newUUID();
  }

  @Test
  void testCreateFrom_handlesNullCommand() {
    assertThrows(
        NullPointerException.class,
        () -> trainingFactory.createFrom(null),
        "Should throw NullPointerException when command is null");
  }
}
