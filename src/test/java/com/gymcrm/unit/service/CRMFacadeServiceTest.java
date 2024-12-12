package com.gymcrm.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.command.*;
import com.gymcrm.domain.*;
import com.gymcrm.factory.TraineeFactory;
import com.gymcrm.service.*;
import com.gymcrm.usecase.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CRMFacadeServiceTest {
  @Mock private TraineeCreationUseCase traineeCreationUseCase;

  @Mock private TraineeUpdateUseCase traineeUpdateUseCase;

  @Mock private LoadTraineeUseCase loadTraineeUseCase;

  @Mock private TrainerCreationUseCase trainerCreationUseCase;

  @Mock private LoadTrainerUseCase loadTrainerUseCase;

  @Mock private TrainerUpdateUseCase trainerUpdateUseCase;

  @Mock private TrainingCreationUseCase trainingCreationUseCase;

  @Mock private LoadTrainingUseCase loadTrainingUseCase;

  @Mock private UserCreationUseCase userCreationUseCase;

  @Mock private TraineeFactory traineeFactory;

  @InjectMocks private CRMFacadeService crmFacadeService;

  private CreateTraineeCommand createTraineeCommand;
  private UpdateTraineeCommand updateTraineeCommand;
  private UpdateTrainerCommand updateTrainerCommand;
  private Trainee trainee;

  @BeforeEach
  void setUp() {
    createTraineeCommand = new CreateTraineeCommand();
    createTraineeCommand.setAddress("some-street");
    createTraineeCommand.setDateOfBirth(LocalDate.of(2000, 1, 1));
    createTraineeCommand.setUserFirstName("some-name");
    createTraineeCommand.setUserLastName("some-last-name");

    updateTraineeCommand = new UpdateTraineeCommand();
    updateTraineeCommand.setTraineeId(UUID.randomUUID());
    updateTraineeCommand.setDateOfBirth(LocalDate.of(1999, 1, 1));
    updateTraineeCommand.setAddress("some-address");

    updateTrainerCommand = new UpdateTrainerCommand();
    updateTrainerCommand.setTrainerId(UUID.randomUUID());
    updateTrainerCommand.setSpecialization("some-specialization");

    trainee = new Trainee();
    trainee.setId(UUID.randomUUID());
    trainee.setAddress("some-address");
    trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));

    crmFacadeService.setLoadTrainerUseCase(loadTrainerUseCase);
    crmFacadeService.setUserCreationUseCase(userCreationUseCase);
    crmFacadeService.setTrainerCreationUseCase(trainerCreationUseCase);
    crmFacadeService.setTrainerUpdateUseCase(trainerUpdateUseCase);
  }

  @Test
  void addTraineeWithUser_shouldAddTraineeSuccessfully() {
    UUID userId = UUID.randomUUID();
    var user =
        new User(
            userId, "some-first-name", "some-last-name", "some-username", "some-password", true);
    when(userCreationUseCase.createUser(any(CreateUserCommand.class))).thenReturn(user);
    when(traineeFactory.createFrom(createTraineeCommand)).thenReturn(trainee);

    crmFacadeService.addTraineeWithUser(createTraineeCommand);

    verify(userCreationUseCase).createUser(any(CreateUserCommand.class));
    verify(traineeCreationUseCase).createTrainee(trainee);
  }

  @Test
  void listAllTrainees_shouldReturnListOfTrainees() {
    List<Trainee> trainees = List.of(trainee);
    when(loadTraineeUseCase.getAllTrainees()).thenReturn(trainees);

    List<Trainee> result = crmFacadeService.listAllTrainees();

    assertEquals(trainees, result);
    verify(loadTraineeUseCase).getAllTrainees();
  }

  @Test
  void getTraineeById_shouldReturnTrainee() {
    UUID traineeId = trainee.getId();
    when(loadTraineeUseCase.getTraineeById(traineeId)).thenReturn(trainee);

    Trainee result = crmFacadeService.getTraineeById(traineeId);

    assertEquals(trainee, result);
    verify(loadTraineeUseCase).getTraineeById(traineeId);
  }

  @Test
  void modifyTrainee_shouldUpdateTrainee() {
    doNothing().when(traineeUpdateUseCase).updateTrainee(updateTraineeCommand);

    crmFacadeService.modifyTrainee(updateTraineeCommand);

    verify(traineeUpdateUseCase).updateTrainee(updateTraineeCommand);
  }

  @Test
  void removeTrainee_shouldRemoveTrainee() {
    UUID traineeId = UUID.randomUUID();
    doNothing().when(traineeUpdateUseCase).removeTrainee(traineeId);

    crmFacadeService.removeTrainee(traineeId);

    verify(traineeUpdateUseCase).removeTrainee(traineeId);
  }

  @Test
  void addTrainerWithUser_shouldAddTrainerSuccessfully() {
    CreateTrainerCommand createTrainerCommand = new CreateTrainerCommand();
    createTrainerCommand.setUserFirstName("some-first-name");
    createTrainerCommand.setUserLastName("some-last-name");

    UUID userId = UUID.randomUUID();
    var user =
        new User(
            userId, "some-first-name", "some-last-name", "some-username", "some-password", true);
    when(userCreationUseCase.createUser(any(CreateUserCommand.class))).thenReturn(user);
    doNothing().when(trainerCreationUseCase).createTrainer(createTrainerCommand);

    crmFacadeService.addTrainerWithUser(createTrainerCommand);

    verify(userCreationUseCase).createUser(any(CreateUserCommand.class));
    verify(trainerCreationUseCase).createTrainer(createTrainerCommand);
  }

  @Test
  void modifyTrainer_shouldUpdateTrainer() {
    doNothing().when(trainerUpdateUseCase).updateTrainer(updateTrainerCommand);

    crmFacadeService.modifyTrainer(updateTrainerCommand);

    verify(trainerUpdateUseCase).updateTrainer(updateTrainerCommand);
  }

  @Test
  void listAllTrainers_shouldReturnListOfTrainers() {
    Trainer trainer = new Trainer();
    List<Trainer> trainers = List.of(trainer);
    when(loadTrainerUseCase.getAllTrainers()).thenReturn(trainers);

    List<Trainer> result = crmFacadeService.listAllTrainers();

    assertEquals(trainers, result);
    verify(loadTrainerUseCase).getAllTrainers();
  }

  @Test
  void getTrainerById_shouldReturnTrainer() {
    UUID trainerId = UUID.randomUUID();
    Trainer trainer = new Trainer();
    when(loadTrainerUseCase.getTrainerById(trainerId)).thenReturn(trainer);

    Trainer result = crmFacadeService.getTrainerById(trainerId);

    assertEquals(trainer, result);
    verify(loadTrainerUseCase).getTrainerById(trainerId);
  }

  @Test
  void addTraining_shouldAddTrainingSuccessfully() {
    CreateTrainingCommand command = new CreateTrainingCommand();
    command.setTrainingName("some-training");

    doNothing().when(trainingCreationUseCase).createTraining(command);

    crmFacadeService.addTraining(command);

    verify(trainingCreationUseCase).createTraining(command);
  }

  @Test
  void listAllTrainings_shouldReturnListOfTrainings() {
    Training training = new Training();
    List<Training> trainings = List.of(training);
    when(loadTrainingUseCase.getAllTrainings()).thenReturn(trainings);

    List<Training> result = crmFacadeService.listAllTrainings();

    assertEquals(trainings, result);
    verify(loadTrainingUseCase).getAllTrainings();
  }

  @Test
  void getTrainingById_shouldReturnTraining() {
    UUID trainingId = UUID.randomUUID();
    Training training = new Training();
    when(loadTrainingUseCase.getTrainingById(trainingId)).thenReturn(training);

    Training result = crmFacadeService.getTrainingById(trainingId);

    assertEquals(training, result);
    verify(loadTrainingUseCase).getTrainingById(trainingId);
  }
}
