package com.gymcrm.service;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
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

  @Autowired
  public CRMFacadeService(
      TraineeCreationUseCase traineeCreationService,
      TraineeUpdateUseCase traineeUpdateService,
      LoadTraineeUseCase traineeSelectionService,
      TrainingCreationUseCase trainingCreationService,
      LoadTrainingUseCase trainingSelectionService) {
    this.traineeCreationService = traineeCreationService;
    this.traineeUpdateService = traineeUpdateService;
    this.traineeSelectionService = traineeSelectionService;
    this.trainingCreationService = trainingCreationService;
    this.trainingSelectionService = trainingSelectionService;
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

  public void addTraineeWithUser(String firstName, String lastName, Trainee trainee) {
    LOGGER.info("Adding trainee with user: {} {}", firstName, lastName);
    var user = userGenerationService.createUser(firstName, lastName);
    trainee.setUserId(user.getId());
    traineeCreationService.createTrainee(trainee);
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

  public void modifyTrainee(UUID traineeId, Trainee updatedTrainee) {
    LOGGER.info("Updating trainee with ID: {}", traineeId);
    traineeUpdateService.updateTrainee(traineeId, updatedTrainee);
    LOGGER.info("Trainee updated successfully");
  }

  public void removeTrainee(UUID traineeId) {
    LOGGER.info("Removing trainee with ID: {}", traineeId);
    traineeUpdateService.removeTrainee(traineeId);
    LOGGER.info("Trainee removed successfully");
  }

  public void addTrainerWithUser(String firstName, String lastName, Trainer trainer) {
    LOGGER.info("Adding trainer with user: {} {}", firstName, lastName);
    var user = userGenerationService.createUser(firstName, lastName);
    trainer.setUserId(user.getId());
    trainerCreationService.createTrainer(trainer);
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

  public void modifyTrainer(UUID trainerId, Trainer updatedTrainer) {
    LOGGER.info("Updating trainer with ID: {}", trainerId);
    trainerUpdateService.updateTrainer(trainerId, updatedTrainer);
    LOGGER.info("Trainer updated successfully");
  }

  public void addTraining(Training training) {
    LOGGER.info("Adding training: {}", training.getTrainingName());
    trainingCreationService.createTraining(training);
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
