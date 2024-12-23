package com.gymcrm.unit.trainee.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainee.application.TraineeService;
import com.gymcrm.trainee.application.factory.TraineeFactory;
import com.gymcrm.trainee.application.port.input.*;
import com.gymcrm.trainee.application.port.output.LoadTraineePort;
import com.gymcrm.trainee.application.port.output.UpdateTraineePort;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.output.UpdateUserPort;
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
class TraineeServiceTest {
  @Mock private UpdateTraineePort updateTraineePort;

  @Mock private LoadTraineePort loadTraineePort;

  @Mock private AuthenticationUseCase authenticationUseCase;

  @Mock private UpdateUserPort updateUserPort;

  @Mock private LoadTrainerPort loadTrainerPort;

  @Mock private UpdateTrainingPort updateTrainingPort;

  @Mock private TraineeFactory traineeFactory;

  @InjectMocks private TraineeService traineeService;

  private UUID traineeId;
  private Trainee trainee;
  private User user;

  @BeforeEach
  void setUp() {
    traineeId = UUID.randomUUID();
    user = new User();
    user.setIsActive(false);
    user.setPassword("oldPassword");

    trainee = new Trainee();
    trainee.setId(traineeId);
    trainee.setUser(user);

    traineeService.setLoadTraineePort(loadTraineePort);
  }

  @Test
  void create_ShouldSaveTrainee_WhenValidCommandProvided() {
    CreateTraineeCommand command = new CreateTraineeCommand(LocalDate.now(), "Test Address", user);
    Trainee createdTrainee = new Trainee();
    when(traineeFactory.createFrom(command)).thenReturn(createdTrainee);

    traineeService.create(command);

    verify(updateTraineePort, times(1)).save(createdTrainee);
  }

  @Test
  void loadAll_ShouldReturnListOfTrainees() {
    List<Trainee> trainees = List.of(trainee, new Trainee());
    when(loadTraineePort.findAll()).thenReturn(trainees);

    List<Trainee> result = traineeService.loadAll();

    assertEquals(2, result.size());
    verify(loadTraineePort, times(1)).findAll();
  }

  @Test
  void loadById_ShouldReturnTrainee_WhenValidIdProvided() {
    when(loadTraineePort.findById(traineeId)).thenReturn(trainee);

    Trainee result = traineeService.loadById(traineeId);

    assertNotNull(result);
    assertEquals(traineeId, result.getId());
    verify(loadTraineePort, times(1)).findById(traineeId);
  }

  @Test
  void loadById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> traineeService.loadById(null));
    assertEquals("ID cannot be null.", exception.getMessage());
  }

  @Test
  void update_ShouldUpdateTrainee_WhenValidCommandProvided() {
    UpdateTraineeCommand command =
        new UpdateTraineeCommand(traineeId, LocalDate.of(1990, 5, 20), "New Address", null);
    when(loadTraineePort.findById(traineeId)).thenReturn(trainee);

    traineeService.update(command);

    assertEquals("New Address", trainee.getAddress());
    assertEquals(LocalDate.of(1990, 5, 20), trainee.getDateOfBirth());
    verify(updateTraineePort, times(1)).save(trainee);
  }

  @Test
  void updatePassword_ShouldUpdatePassword_WhenValidCommandProvided() {
    UpdateTraineePasswordCommand command =
        new UpdateTraineePasswordCommand(traineeId, "username", "oldPassword", "newPassword");
    when(loadTraineePort.findById(traineeId)).thenReturn(trainee);
    when(authenticationUseCase.authenticateTrainee("username", "oldPassword")).thenReturn(true);

    traineeService.updatePassword(command);

    assertEquals("newPassword", user.getPassword());
    verify(updateUserPort, times(1)).save(user);
  }

  @Test
  void activate_ShouldActivateDeactivateTrainee_WhenValidIdProvided() {
    when(loadTraineePort.findById(traineeId)).thenReturn(trainee);

    traineeService.activateDeactivate(traineeId);

    assertTrue(user.getIsActive());
    verify(updateUserPort, times(1)).save(user);
  }

  @Test
  void deleteById_ShouldDeleteTrainee_WhenValidIdProvided() {
    traineeService.deleteById(traineeId);

    verify(updateTraineePort, times(1)).deleteById(traineeId);
  }

  @Test
  void deleteByUsername_ShouldDeleteTrainee_WhenValidUsernameProvided() {
    traineeService.deleteByUsername("username");

    verify(updateTraineePort, times(1)).deleteByUsername("username");
  }

  @Test
  void updateTrainersOfTrainee_ShouldUpdateTrainers_WhenValidMappingProvided() {
    Training training1 = new Training();
    training1.setId(UUID.randomUUID());
    training1.setTrainee(trainee);

    Training training2 = new Training();
    training2.setId(UUID.randomUUID());
    training2.setTrainee(trainee);

    trainee.setTrainings(List.of(training1, training2));

    Trainer trainer1 = new Trainer();
    trainer1.setId(UUID.randomUUID());
    Trainer trainer2 = new Trainer();
    trainer2.setId(UUID.randomUUID());

    Map<UUID, UUID> trainerToTrainingMap =
        Map.of(
            training1.getId(), trainer1.getId(),
            training2.getId(), trainer2.getId());

    when(loadTraineePort.findById(traineeId)).thenReturn(trainee);
    when(loadTrainerPort.findById(trainer1.getId())).thenReturn(trainer1);
    when(loadTrainerPort.findById(trainer2.getId())).thenReturn(trainer2);

    traineeService.updateTrainersOfTrainee(traineeId, trainerToTrainingMap);

    assertEquals(trainer1, training1.getTrainer());
    assertEquals(trainer2, training2.getTrainer());
    verify(updateTrainingPort, times(2)).save(any(Training.class));
  }

  @Test
  void updateTrainersOfTrainee_ShouldUpdateTrainerForTrainee_WhenValidCommandProvided() {
    UUID trainingId = UUID.randomUUID();
    UUID trainerId = UUID.randomUUID();
    Trainer trainer = new Trainer(trainerId, "Specialization", new User());
    Training training =
        new Training(trainingId, "Training Name", trainee, trainer, null, LocalDate.now(), 60);
    trainee.setTrainings(List.of(training));

    UpdateTraineeCommand command = new UpdateTraineeCommand(training);

    lenient().when(loadTrainerPort.findById(trainerId)).thenReturn(trainer);

    traineeService.updateTrainersOfTrainee(command);

    assertEquals(1, trainee.getTrainers().size());
    assertEquals(trainer, trainee.getTrainers().get(0));
    verify(updateTraineePort, times(1)).save(trainee);
  }

  @Test
  void updateTrainersOfTrainee_ShouldHandleRuntimeException_WhenSaveFails() {
    UUID trainingId = UUID.randomUUID();
    UUID trainerId = UUID.randomUUID();
    Trainer trainer = new Trainer(trainerId, "Specialization", new User());
    Training training =
        new Training(trainingId, "Training Name", trainee, trainer, null, LocalDate.now(), 60);
    trainee.setTrainings(List.of(training));

    UpdateTraineeCommand command = new UpdateTraineeCommand(training);

    doThrow(new RuntimeException("Database error")).when(updateTraineePort).save(trainee);

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> traineeService.updateTrainersOfTrainee(command));

    assertEquals("Failed to update trainee for training", exception.getMessage());
    verify(updateTraineePort, times(1)).save(trainee);
  }
}
