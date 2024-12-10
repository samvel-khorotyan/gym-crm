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
  private static final Logger LOGGER = LoggerFactory.getLogger(CRMFacadeService.class);

  private final TraineeCreationUseCase traineeCreationService;
  private final TraineeUpdateUseCase traineeUpdateService;
  private final LoadTraineeUseCase traineeSelectionService;
  private TrainerCreationUseCase trainerCreationService;
  private LoadTrainerUseCase trainerSelectionService;
  private TrainerUpdateUseCase trainerUpdateService;
  private final TrainingCreationUseCase trainingCreationService;
  private final LoadTrainingUseCase trainingSelectionService;
  private UserCreationUseCase userGenerationService;
  private final TraineeFactory traineeFactory;

  @Autowired
  public CRMFacadeService(
      TraineeCreationUseCase traineeCreationService,
      TraineeUpdateUseCase traineeUpdateService,
      LoadTraineeUseCase traineeSelectionService,
      TrainingCreationUseCase trainingCreationService,
      LoadTrainingUseCase trainingSelectionService,
      TraineeFactory traineeFactory) {
    this.traineeCreationService = traineeCreationService;
    this.traineeUpdateService = traineeUpdateService;
    this.traineeSelectionService = traineeSelectionService;
    this.trainingCreationService = trainingCreationService;
    this.trainingSelectionService = trainingSelectionService;
    this.traineeFactory = traineeFactory;
  }

  @Autowired
  public void setTrainerCreationService(TrainerCreationUseCase trainerCreationService) {
    this.trainerCreationService = trainerCreationService;
  }

  @Autowired
  public void setTrainerSelectionService(LoadTrainerUseCase trainerSelectionService) {
    this.trainerSelectionService = trainerSelectionService;
  }

  @Autowired
  public void setTrainerUpdateService(TrainerUpdateUseCase trainerUpdateService) {
    this.trainerUpdateService = trainerUpdateService;
  }

  @Autowired
  public void setUserGenerationService(UserCreationUseCase userGenerationService) {
    this.userGenerationService = userGenerationService;
  }

  public void addTraineeWithUser(CreateTraineeCommand command) {
    LOGGER.info(
        "Adding trainee with user: {} {}", command.getUserFirstName(), command.getUserLastName());
    var user =
        userGenerationService.createUser(
            new CreateUserCommand(command.getUserFirstName(), command.getUserLastName()));
    command.setUserId(user.getId());
    traineeCreationService.createTrainee(traineeFactory.createFrom(command));
    LOGGER.info("Trainee added successfully with username: {}", user.getUsername());
  }

  public List<Trainee> listAllTrainees() {
    LOGGER.info("Fetching all trainees");
    return traineeSelectionService.getAllTrainees();
  }

  public Trainee getTraineeById(UUID traineeId) {
    LOGGER.info("Fetching trainee by ID: {}", traineeId);
    return traineeSelectionService.getTraineeById(traineeId);
  }

  public void modifyTrainee(UpdateTraineeCommand command) {
    LOGGER.info("Updating trainee with ID: {}", command.getTraineeId());
    traineeUpdateService.updateTrainee(command);
    LOGGER.info("Trainee updated successfully");
  }

  public void removeTrainee(UUID traineeId) {
    LOGGER.info("Removing trainee with ID: {}", traineeId);
    traineeUpdateService.removeTrainee(traineeId);
    LOGGER.info("Trainee removed successfully");
  }

  public void addTrainerWithUser(CreateTrainerCommand command) {
    LOGGER.info(
        "Adding trainer with user: {} {}", command.getUserFirstName(), command.getUserLastName());
    var user =
        userGenerationService.createUser(
            new CreateUserCommand(command.getUserFirstName(), command.getUserLastName()));
    command.setUserId(user.getId());
    trainerCreationService.createTrainer(command);
    LOGGER.info("Trainer added successfully with username: {}", user.getUsername());
  }

  public List<Trainer> listAllTrainers() {
    LOGGER.info("Fetching all trainers");
    return trainerSelectionService.getAllTrainers();
  }

  public Trainer getTrainerById(UUID trainerId) {
    LOGGER.info("Fetching trainer by ID: {}", trainerId);
    return trainerSelectionService.getTrainerById(trainerId);
  }

  public void modifyTrainer(UpdateTrainerCommand command) {
    LOGGER.info("Updating trainer with ID: {}", command.getTrainerId());
    trainerUpdateService.updateTrainer(command);
    LOGGER.info("Trainer updated successfully");
  }

  public void addTraining(CreateTrainingCommand command) {
    LOGGER.info("Adding training: {}", command.getTrainingName());
    trainingCreationService.createTraining(command);
    LOGGER.info("Training added successfully");
  }

  public List<Training> listAllTrainings() {
    LOGGER.info("Fetching all trainings");
    return trainingSelectionService.getAllTrainings();
  }

  public Training getTrainingById(UUID trainingId) {
    LOGGER.info("Fetching training by ID: {}", trainingId);
    return trainingSelectionService.getTrainingById(trainingId);
  }
}
