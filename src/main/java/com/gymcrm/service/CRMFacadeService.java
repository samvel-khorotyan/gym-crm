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
    logger.debug("Adding trainee: {} {}", command.getUserFirstName(), command.getUserLastName());
    var user =
        userGenerationService.createUser(
            new CreateUserCommand(command.getUserFirstName(), command.getUserLastName()));
    command.setUserId(user.getId());
    traineeCreationService.createTrainee(traineeFactory.createFrom(command));
    logger.info("Trainee added successfully");
  }

  public List<Trainee> listAllTrainees() {
    logger.debug("Fetching all trainees");
    return traineeSelectionService.getAllTrainees();
  }

  public Trainee getTraineeById(UUID traineeId) {
    logger.debug("Fetching trainee by ID: {}", traineeId);
    return traineeSelectionService.getTraineeById(traineeId);
  }

  public void modifyTrainee(UpdateTraineeCommand command) {
    logger.debug("Updating trainee with ID: {}", command.getTraineeId());
    traineeUpdateService.updateTrainee(command);
    logger.info("Trainee updated successfully");
  }

  public void removeTrainee(UUID traineeId) {
    logger.debug("Removing trainee with ID: {}", traineeId);
    traineeUpdateService.removeTrainee(traineeId);
    logger.info("Trainee removed successfully");
  }

  public void addTrainerWithUser(CreateTrainerCommand command) {
    logger.debug("Adding trainer: {} {}", command.getUserFirstName(), command.getUserLastName());
    var user =
        userGenerationService.createUser(
            new CreateUserCommand(command.getUserFirstName(), command.getUserLastName()));
    command.setUserId(user.getId());
    trainerCreationService.createTrainer(command);
    logger.info("Trainer added successfully");
  }

  public List<Trainer> listAllTrainers() {
    logger.debug("Fetching all trainers");
    return trainerSelectionService.getAllTrainers();
  }

  public Trainer getTrainerById(UUID trainerId) {
    logger.debug("Fetching trainer by ID: {}", trainerId);
    return trainerSelectionService.getTrainerById(trainerId);
  }

  public void modifyTrainer(UpdateTrainerCommand command) {
    logger.debug("Updating trainer with ID: {}", command.getTrainerId());
    trainerUpdateService.updateTrainer(command);
    logger.info("Trainer updated successfully");
  }

  public void addTraining(CreateTrainingCommand command) {
    logger.debug("Adding training: {}", command.getTrainingName());
    trainingCreationService.createTraining(command);
    logger.info("Training added successfully");
  }

  public List<Training> listAllTrainings() {
    logger.debug("Fetching all trainings");
    return trainingSelectionService.getAllTrainings();
  }

  public Training getTrainingById(UUID trainingId) {
    logger.debug("Fetching training by ID: {}", trainingId);
    return trainingSelectionService.getTrainingById(trainingId);
  }
}
