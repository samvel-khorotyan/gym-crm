package com.gymcrm.unit.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.facade.CRMFacade;
import com.gymcrm.trainee.application.port.input.CreateTraineeCommand;
import com.gymcrm.trainee.application.port.input.LoadTraineeUseCase;
import com.gymcrm.trainee.application.port.input.TraineeCreationUseCase;
import com.gymcrm.trainee.application.port.input.TraineeUpdateUseCase;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.port.input.LoadTrainerUseCase;
import com.gymcrm.trainer.application.port.input.TrainerCreationUseCase;
import com.gymcrm.trainer.application.port.input.TrainerUpdateUseCase;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import com.gymcrm.trainingtype.application.port.input.TrainingTypeCreationUseCase;
import com.gymcrm.trainingtype.domain.TrainingType;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.input.UserCreationUseCase;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
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
class CRMFacadeTest {
  @Mock private TraineeCreationUseCase traineeCreationUseCase;

  @Mock private UserCreationUseCase userCreationUseCase;

  @Mock private TraineeUpdateUseCase traineeUpdateUseCase;

  @Mock private LoadTraineeUseCase loadTraineeUseCase;

  @Mock private TrainerCreationUseCase trainerCreationUseCase;

  @Mock private LoadTrainerUseCase loadTrainerUseCase;

  @Mock private TrainerUpdateUseCase trainerUpdateUseCase;

  @Mock private TrainingCreationUseCase trainingCreationUseCase;

  @Mock private LoadTrainingUseCase loadTrainingUseCase;

  @Mock private AuthenticationUseCase authenticationUseCase;

  @Mock private TrainingTypeCreationUseCase trainingTypeCreationUseCase;

  @InjectMocks private CRMFacade crmFacade;

  @BeforeEach
  void setUp() {
    crmFacade.setTrainerCreationUseCase(trainerCreationUseCase);
    crmFacade.setLoadTrainerUseCase(loadTrainerUseCase);
    crmFacade.setTrainerUpdateUseCase(trainerUpdateUseCase);
    crmFacade.setUserCreationUseCase(userCreationUseCase);
  }

  private CreateUserCommand createUserCommand() {
    return new CreateUserCommand("John", "Doe", "username", "password", UserType.TRAINEE);
  }

  private CreateTraineeCommand createTraineeCommand() {
    return new CreateTraineeCommand(LocalDate.of(2000, 1, 1), "123 Main St");
  }

  private CreateTrainingCommand createTrainingCommand() {
    return new CreateTrainingCommand(
        "Yoga Training", new Trainee(), new Trainer(), new TrainingType(), LocalDate.now(), 60);
  }

  @Test
  void addTraineeWithUser_ShouldAddTrainee_WhenValidInputProvided() {
    CreateUserCommand userCommand = createUserCommand();
    CreateTraineeCommand traineeCommand = createTraineeCommand();
    User mockUser =
        new User(
            UUID.randomUUID(),
            "John",
            "Doe",
            "some-username",
            "some-password",
            true,
            UserType.TRAINEE);
    when(userCreationUseCase.create(userCommand)).thenReturn(mockUser);

    crmFacade.addTraineeWithUser(traineeCommand, userCommand);

    verify(userCreationUseCase, times(1)).create(userCommand);
    verify(traineeCreationUseCase, times(1)).create(traineeCommand);
  }

  @Test
  void addTraineeWithUser_ShouldThrowException_WhenUserCreationFails() {
    CreateUserCommand userCommand = createUserCommand();
    CreateTraineeCommand traineeCommand = createTraineeCommand();
    when(userCreationUseCase.create(userCommand))
        .thenThrow(new RuntimeException("User creation failed"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> crmFacade.addTraineeWithUser(traineeCommand, userCommand));
    assertEquals("Failed to add trainee", exception.getMessage());
    verify(userCreationUseCase, times(1)).create(userCommand);
    verify(traineeCreationUseCase, never()).create(any());
  }

  @Test
  void loadTrainees_ShouldReturnTrainees_WhenTraineesExist() {
    Trainee trainee = new Trainee();
    trainee.setId(UUID.randomUUID());

    List<Trainee> trainees = List.of();
    when(loadTraineeUseCase.loadAll()).thenReturn(trainees);

    List<Trainee> result = crmFacade.loadTrainees();

    assertEquals(trainees.size(), result.size());
    verify(loadTraineeUseCase, times(1)).loadAll();
  }

  @Test
  void loadTrainees_ShouldReturnEmptyList_WhenNoTraineesExist() {
    when(loadTraineeUseCase.loadAll()).thenReturn(List.of());

    List<Trainee> result = crmFacade.loadTrainees();

    assertTrue(result.isEmpty());
    verify(loadTraineeUseCase, times(1)).loadAll();
  }

  @Test
  void loadTraineeById_ShouldReturnTrainee_WhenTraineeExists() {
    UUID traineeId = UUID.randomUUID();
    Trainee trainee = new Trainee();
    trainee.setId(traineeId);

    Trainee mockTrainee = new Trainee();
    mockTrainee.setId(traineeId);

    when(loadTraineeUseCase.loadById(traineeId)).thenReturn(mockTrainee);

    Trainee result = crmFacade.loadTraineeById(traineeId);

    assertNotNull(result);
    assertEquals(mockTrainee.getId(), result.getId());
    verify(loadTraineeUseCase, times(1)).loadById(traineeId);
  }

  @Test
  void loadTraineeById_ShouldThrowException_WhenTraineeNotFound() {
    UUID traineeId = UUID.randomUUID();
    when(loadTraineeUseCase.loadById(traineeId))
        .thenThrow(new RuntimeException("Trainee not found"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> crmFacade.loadTraineeById(traineeId));
    assertEquals("Failed to fetch trainee by ID", exception.getMessage());
    verify(loadTraineeUseCase, times(1)).loadById(traineeId);
  }

  @Test
  void addTraining_ShouldAddTraining_WhenValidInputProvided() {
    CreateTrainingCommand trainingCommand = createTrainingCommand();

    crmFacade.addTraining(trainingCommand);

    verify(trainingCreationUseCase, times(1)).create(trainingCommand);
  }

  @Test
  void addTraining_ShouldThrowException_WhenTrainingCreationFails() {
    CreateTrainingCommand trainingCommand = createTrainingCommand();
    doThrow(new RuntimeException("Training creation failed"))
        .when(trainingCreationUseCase)
        .create(trainingCommand);

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> crmFacade.addTraining(trainingCommand));
    assertEquals("Failed to add training", exception.getMessage());
    verify(trainingCreationUseCase, times(1)).create(trainingCommand);
  }

  @Test
  void authenticateTrainee_ShouldReturnTrue_WhenValidCredentialsProvided() {
    String username = "john.doe";
    String password = "password";
    when(authenticationUseCase.authenticateTrainee(username, password)).thenReturn(true);

    boolean result = crmFacade.authenticateTrainee(username, password);

    assertTrue(result);
    verify(authenticationUseCase, times(1)).authenticateTrainee(username, password);
  }

  @Test
  void authenticateTrainee_ShouldThrowException_WhenAuthenticationFails() {
    String username = "john.doe";
    String password = "wrong-password";
    when(authenticationUseCase.authenticateTrainee(username, password))
        .thenThrow(new RuntimeException("Invalid credentials"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> crmFacade.authenticateTrainee(username, password));
    assertEquals("Failed to authenticate trainee", exception.getMessage());
    verify(authenticationUseCase, times(1)).authenticateTrainee(username, password);
  }
}
