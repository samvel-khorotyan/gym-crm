package com.gymcrm.unit.trainer.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainer.application.TrainerService;
import com.gymcrm.trainer.application.factory.TrainerFactory;
import com.gymcrm.trainer.application.port.input.*;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
  @Mock private UpdateTrainerPort updateTrainerPort;

  @Mock private LoadTrainerPort loadTrainerPort;

  @Mock private TrainerFactory trainerFactory;

  @Mock private AuthenticationUseCase authenticationUseCase;

  @Mock private UpdateUserPort updateUserPort;

  @InjectMocks private TrainerService trainerService;

  private CreateTrainerCommand validCreateCommand;
  private UpdateTrainerCommand validUpdateCommand;
  private UpdateTrainerPasswordCommand validPasswordCommand;
  private UUID trainerId;

  @BeforeEach
  void setUp() {
    User user = new User();
    trainerId = UUID.randomUUID();
    validCreateCommand = new CreateTrainerCommand("Fitness", user);
    validUpdateCommand = new UpdateTrainerCommand();
    validUpdateCommand.setTrainerId(trainerId);
    validUpdateCommand.setSpecialization("Wrestling");
    validPasswordCommand =
        new UpdateTrainerPasswordCommand(trainerId, "trainer1", "oldPass", "newPass");

    trainerService.setLoadTrainerPort(loadTrainerPort);
  }

  @Test
  void create_ShouldSaveTrainer_WhenValidCommandProvided() {
    Trainer trainer = new Trainer();
    when(trainerFactory.createFrom(validCreateCommand)).thenReturn(trainer);

    trainerService.create(validCreateCommand);

    verify(trainerFactory, times(1)).createFrom(validCreateCommand);
    verify(updateTrainerPort, times(1)).save(trainer);
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenSpecializationIsNull() {
    validCreateCommand.setSpecialization(null);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> trainerService.create(validCreateCommand));

    assertEquals("Specialization cannot be null.", exception.getMessage());
  }

  @Test
  void loadAll_ShouldReturnListOfTrainers() {
    List<Trainer> trainers = List.of(new Trainer(), new Trainer());
    when(loadTrainerPort.findAll()).thenReturn(trainers);

    List<Trainer> result = trainerService.loadAll();

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(loadTrainerPort, times(1)).findAll();
  }

  @Test
  void loadById_ShouldReturnTrainer_WhenIdIsValid() {
    Trainer trainer = new Trainer();
    when(loadTrainerPort.findById(trainerId)).thenReturn(trainer);

    Trainer result = trainerService.loadById(trainerId);

    assertNotNull(result);
    assertEquals(trainer, result);
    verify(loadTrainerPort, times(1)).findById(trainerId);
  }

  @Test
  void loadById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> trainerService.loadById(null));

    assertEquals("ID cannot be null.", exception.getMessage());
  }

  @Test
  void update_ShouldUpdateTrainerSpecialization_WhenValidCommandProvided() {
    Trainer trainer = new Trainer();
    when(loadTrainerPort.findById(trainerId)).thenReturn(trainer);

    trainerService.update(validUpdateCommand);

    assertEquals("Wrestling", trainer.getSpecialization());
    verify(updateTrainerPort, times(1)).save(trainer);
  }

  @Test
  void update_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
    validUpdateCommand.setTrainerId(null);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> trainerService.update(validUpdateCommand));

    assertEquals("ID cannot be null.", exception.getMessage());
  }

  @Test
  void updatePassword_ShouldUpdatePassword_WhenValidCommandProvided() {
    Trainer trainer = new Trainer();
    User user = new User();
    trainer.setUser(user);
    when(loadTrainerPort.findById(trainerId)).thenReturn(trainer);
    when(authenticationUseCase.authenticateTrainer("trainer1", "oldPass")).thenReturn(true);

    trainerService.updatePassword(validPasswordCommand);

    assertEquals("newPass", user.getPassword());
    verify(updateUserPort, times(1)).save(user);
  }
}
