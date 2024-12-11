package com.gymcrm.service;

import com.gymcrm.command.*;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.factory.TraineeFactory;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CRMFacadeService {
  private static final Logger logger = LoggerFactory.getLogger(CRMFacadeService.class);

  private final TraineeCreationUseCase traineeCreationUseCase;
  private final TraineeUpdateUseCase traineeUpdateUseCase;
  private final LoadTraineeUseCase toadTraineeUseCase;
  private TrainerCreationUseCase trainerCreationUseCase;
  private LoadTrainerUseCase loadTrainerUseCase;
  private TrainerUpdateUseCase trainerUpdateUseCase;
  private final TrainingCreationUseCase trainingCreationUseCase;
  private final LoadTrainingUseCase loadTrainingUseCase;
  private UserCreationUseCase userCreationUseCase;
  private final TraineeFactory traineeFactory;

  @Autowired
  public CRMFacadeService(
      TraineeCreationUseCase traineeCreationUseCase,
      TraineeUpdateUseCase traineeUpdateUseCase,
      LoadTraineeUseCase toadTraineeUseCase,
      TrainingCreationUseCase trainingCreationUseCase,
      LoadTrainingUseCase loadTrainingUseCase,
      TraineeFactory traineeFactory) {
    this.traineeCreationUseCase = traineeCreationUseCase;
    this.traineeUpdateUseCase = traineeUpdateUseCase;
    this.toadTraineeUseCase = toadTraineeUseCase;
    this.trainingCreationUseCase = trainingCreationUseCase;
    this.loadTrainingUseCase = loadTrainingUseCase;
    this.traineeFactory = traineeFactory;
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

  public void addTraineeWithUser(CreateTraineeCommand command) {
    logger.debug("Adding trainee: {} {}", command.getUserFirstName(), command.getUserLastName());
    var user =
        userCreationUseCase.createUser(
            new CreateUserCommand(command.getUserFirstName(), command.getUserLastName()));
    command.setUserId(user.getId());
    traineeCreationUseCase.createTrainee(traineeFactory.createFrom(command));
    logger.info("Trainee added successfully");
  }

  public List<Trainee> listAllTrainees() {
    logger.debug("Fetching all trainees");
    return toadTraineeUseCase.getAllTrainees();
  }

  public Trainee getTraineeById(UUID traineeId) {
    logger.debug("Fetching trainee by ID: {}", traineeId);
    return toadTraineeUseCase.getTraineeById(traineeId);
  }

  public void modifyTrainee(UpdateTraineeCommand command) {
    logger.debug("Updating trainee with ID: {}", command.getTraineeId());
    traineeUpdateUseCase.updateTrainee(command);
    logger.info("Trainee updated successfully");
  }

  public void removeTrainee(UUID traineeId) {
    logger.debug("Removing trainee with ID: {}", traineeId);
    traineeUpdateUseCase.removeTrainee(traineeId);
    logger.info("Trainee removed successfully");
  }

  public void addTrainerWithUser(CreateTrainerCommand command) {
    logger.debug("Adding trainer: {} {}", command.getUserFirstName(), command.getUserLastName());
    var user =
        userCreationUseCase.createUser(
            new CreateUserCommand(command.getUserFirstName(), command.getUserLastName()));
    command.setUserId(user.getId());
    trainerCreationUseCase.createTrainer(command);
    logger.info("Trainer added successfully");
  }

  public List<Trainer> listAllTrainers() {
    logger.debug("Fetching all trainers");
    return loadTrainerUseCase.getAllTrainers();
  }

  public Trainer getTrainerById(UUID trainerId) {
    logger.debug("Fetching trainer by ID: {}", trainerId);
    return loadTrainerUseCase.getTrainerById(trainerId);
  }

  public void modifyTrainer(UpdateTrainerCommand command) {
    logger.debug("Updating trainer with ID: {}", command.getTrainerId());
    trainerUpdateUseCase.updateTrainer(command);
    logger.info("Trainer updated successfully");
  }

  public void addTraining(CreateTrainingCommand command) {
    logger.debug("Adding training: {}", command.getTrainingName());
    trainingCreationUseCase.createTraining(command);
    logger.info("Training added successfully");
  }

  public List<Training> listAllTrainings() {
    logger.debug("Fetching all trainings");
    return loadTrainingUseCase.getAllTrainings();
  }

  public Training getTrainingById(UUID trainingId) {
    logger.debug("Fetching training by ID: {}", trainingId);
    return loadTrainingUseCase.getTrainingById(trainingId);
  }
}
