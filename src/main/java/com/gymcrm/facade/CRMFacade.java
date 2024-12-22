package com.gymcrm.facade;

import com.gymcrm.trainee.application.factory.TraineeFactory;
import com.gymcrm.trainee.application.port.input.*;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.port.input.*;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.application.port.input.CreateTrainingTypeCommand;
import com.gymcrm.trainingtype.application.port.input.TrainingTypeCreationUseCase;
import com.gymcrm.trainingtype.domain.TrainingType;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.input.LoadUserUseCase;
import com.gymcrm.user.application.port.input.UserCreationUseCase;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CRMFacade {
  private static final Logger logger = LoggerFactory.getLogger(CRMFacade.class);

  private final TrainingTypeCreationUseCase trainingTypeCreationUseCase;
  private final TraineeCreationUseCase traineeCreationUseCase;
  private final TraineeUpdateUseCase traineeUpdateUseCase;
  private final LoadTraineeUseCase loadTraineeUseCase;
  private TrainerCreationUseCase trainerCreationUseCase;
  private LoadTrainerUseCase loadTrainerUseCase;
  private TrainerUpdateUseCase trainerUpdateUseCase;
  private final TrainingCreationUseCase trainingCreationUseCase;
  private final AuthenticationUseCase authenticationUseCase;
  private final LoadTrainingUseCase loadTrainingUseCase;
  private UserCreationUseCase userCreationUseCase;
  private final TraineeFactory traineeFactory;
  private final LoadUserUseCase loadUserUseCase;

  @Autowired
  public CRMFacade(
      TrainingTypeCreationUseCase trainingTypeCreationUseCase,
      TraineeCreationUseCase traineeCreationUseCase,
      AuthenticationUseCase authenticationUseCase,
      TraineeUpdateUseCase traineeUpdateUseCase,
      LoadTraineeUseCase loadTraineeUseCase,
      TrainingCreationUseCase trainingCreationUseCase,
      LoadTrainingUseCase loadTrainingUseCase,
      TraineeFactory traineeFactory,
      LoadUserUseCase loadUserUseCase) {
    this.trainingTypeCreationUseCase = trainingTypeCreationUseCase;
    this.authenticationUseCase = authenticationUseCase;
    this.traineeCreationUseCase = traineeCreationUseCase;
    this.traineeUpdateUseCase = traineeUpdateUseCase;
    this.loadTraineeUseCase = loadTraineeUseCase;
    this.trainingCreationUseCase = trainingCreationUseCase;
    this.loadTrainingUseCase = loadTrainingUseCase;
    this.traineeFactory = traineeFactory;
    this.loadUserUseCase = loadUserUseCase;
  }

  @Autowired
  public void setTrainerCreationUseCase(TrainerCreationUseCase trainerCreationUseCase) {
    this.trainerCreationUseCase = trainerCreationUseCase;
  }

  @Autowired
  public void setLoadTrainerUseCase(LoadTrainerUseCase loadTrainerUseCase) {
    this.loadTrainerUseCase = loadTrainerUseCase;
  }

  @Autowired
  public void setTrainerUpdateUseCase(TrainerUpdateUseCase trainerUpdateUseCase) {
    this.trainerUpdateUseCase = trainerUpdateUseCase;
  }

  @Autowired
  public void setUserCreationUseCase(UserCreationUseCase userCreationUseCase) {
    this.userCreationUseCase = userCreationUseCase;
  }

  public void addTraineeWithUser(
      CreateTraineeCommand createTraineeCommand, CreateUserCommand createUserCommand) {
    logger.debug(
        "Adding trainee: {} {}", createUserCommand.getFirstName(), createUserCommand.getLastName());
    createUserCommand.setUserType(UserType.TRAINEE);
    var user = userCreationUseCase.create(createUserCommand);
    createTraineeCommand.setUser(user);
    traineeCreationUseCase.create(traineeFactory.createFrom(createTraineeCommand));
    logger.info("Trainee added successfully");
  }

  public List<Trainee> loadTrainees() {
    logger.debug("Fetching all trainees");
    return loadTraineeUseCase.loadAll();
  }

  public List<Training> findTraineeTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String trainerName, String trainingType) {
    return loadTrainingUseCase.findTraineeTrainingsByCriteria(
        startDate, endDate, trainerName, trainingType);
  }

  public Trainee loadTraineeById(UUID traineeId) {
    logger.debug("Fetching trainee by ID: {}", traineeId);
    return loadTraineeUseCase.loadById(traineeId);
  }

  public void modifyTrainee(UpdateTraineeCommand command) {
    logger.debug("Updating trainee with ID: {}", command.getTraineeId());
    traineeUpdateUseCase.update(command);
    logger.info("Trainee updated successfully");
  }

  public void modifyTraineePassword(UpdateTraineePasswordCommand command) {
    logger.debug("Updating trainee password with ID: {}", command.getTraineeId());
    traineeUpdateUseCase.updatePassword(command);
  }

  public void modifyTraineeTrainers(UUID traineeId, Map<UUID, UUID> map) {
    traineeUpdateUseCase.updateTrainersOfTrainee(traineeId, map);
  }

  public void activateTrainee(UUID traineeId) {
    logger.debug("Activating trainee: {}", traineeId);
    traineeUpdateUseCase.activate(traineeId);
  }

  public void deactivateTrainee(UUID traineeId) {
    logger.debug("Deactivate trainee: {}", traineeId);
    traineeUpdateUseCase.deactivate(traineeId);
  }

  public void deleteTraineeById(UUID traineeId) {
    logger.debug("Removing trainee with ID: {}", traineeId);
    traineeUpdateUseCase.deleteById(traineeId);
    logger.info("Trainee removed successfully");
  }

  public void deleteTraineeByUsername(String username) {
    logger.debug("Removing trainee with username: {}", username);
    traineeUpdateUseCase.deleteByUsername(username);
  }

  public void addTrainerWithUser(
      CreateTrainerCommand createTrainerCommand, CreateUserCommand createUserCommand) {
    logger.debug(
        "Adding trainer: {} {}", createUserCommand.getFirstName(), createUserCommand.getLastName());
    createUserCommand.setUserType(UserType.TRAINER);
    User user = userCreationUseCase.create(createUserCommand);
    createTrainerCommand.setUser(user);
    trainerCreationUseCase.create(createTrainerCommand);
    logger.info("Trainer added successfully");
  }

  public List<Trainer> loadTrainers() {
    logger.debug("Fetching all trainers");
    return loadTrainerUseCase.loadAll();
  }

  public List<Trainer> loadTrainersNotAssignedToTrainee(String trainerName) {
    logger.debug("Fetching all trainers not assigned to trainee");
    return loadTrainerUseCase.loadTrainersNotAssignedToTrainee(trainerName);
  }

  public List<Training> findTrainerTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String traineeName) {
    return loadTrainingUseCase.findTrainerTrainingsByCriteria(startDate, endDate, traineeName);
  }

  public Trainer loadTrainerById(UUID trainerId) {
    logger.debug("Fetching trainer by ID: {}", trainerId);
    return loadTrainerUseCase.loadById(trainerId);
  }

  public void modifyTrainer(UpdateTrainerCommand command) {
    logger.debug("Updating trainer with ID: {}", command.getTrainerId());
    trainerUpdateUseCase.update(command);
    logger.info("Trainer updated successfully");
  }

  public void modifyTrainerPassword(UpdateTrainerPasswordCommand command) {
    logger.debug("Updating trainer password with ID: {}", command.getTrainerId());
    trainerUpdateUseCase.updatePassword(command);
  }

  public void activateTrainer(UUID trainerId) {
    logger.debug("Activating trainer: {}", trainerId);
    trainerUpdateUseCase.activate(trainerId);
  }

  public void deactivateTrainer(UUID trainerId) {
    logger.debug("Deactivate trainer: {}", trainerId);
    trainerUpdateUseCase.deactivate(trainerId);
  }

  public void addTraining(CreateTrainingCommand command) {
    logger.debug("Adding training: {}", command.getTrainingName());
    trainingCreationUseCase.create(command);
    logger.info("Training added successfully");
  }

  public List<Training> loadTrainings() {
    logger.debug("Fetching all trainings");
    return loadTrainingUseCase.loadAll();
  }

  public Training loadTrainingById(UUID trainingId) {
    logger.debug("Fetching training by ID: {}", trainingId);
    return loadTrainingUseCase.loadById(trainingId);
  }

  public TrainingType addTrainingType(CreateTrainingTypeCommand command) {
    logger.debug("Adding training type: {}", command.getTrainingTypeName());
    return trainingTypeCreationUseCase.create(command);
  }

  public boolean authenticateTrainee(String username, String password) {
    return authenticationUseCase.authenticateTrainee(username, password);
  }

  public boolean authenticateTrainer(String username, String password) {
    return authenticationUseCase.authenticateTrainer(username, password);
  }

  public boolean authenticateAdmin(String username, String password) {
    return authenticationUseCase.authenticateAdmin(username, password);
  }

  public List<User> loadUsers() {
    return loadUserUseCase.loadAll();
  }

  public Trainee loadTraineeByUsername(String username) {
    return loadUserUseCase.loadUserByUsername(username).getTrainee();
  }

  public Trainer loadTrainerByUsername(String username) {
    return loadUserUseCase.loadUserByUsername(username).getTrainer();
  }
}
