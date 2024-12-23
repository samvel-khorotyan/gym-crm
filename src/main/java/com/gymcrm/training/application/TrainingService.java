package com.gymcrm.training.application;

import com.gymcrm.trainee.application.port.input.TraineeUpdateUseCase;
import com.gymcrm.trainee.application.port.input.UpdateTraineeCommand;
import com.gymcrm.training.application.factory.TrainingFactory;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingService implements TrainingCreationUseCase, LoadTrainingUseCase {
  private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

  private final UpdateTrainingPort updateTrainingPort;
  private final LoadTrainingPort loadTrainingPort;
  private final TrainingFactory trainingFactory;
  private final TraineeUpdateUseCase traineeUpdateUseCase;

  @Autowired
  public TrainingService(
      UpdateTrainingPort updateTrainingPort,
      LoadTrainingPort loadTrainingPort,
      TrainingFactory trainingFactory,
      TraineeUpdateUseCase traineeUpdateUseCase) {
    this.updateTrainingPort = updateTrainingPort;
    this.loadTrainingPort = loadTrainingPort;
    this.trainingFactory = trainingFactory;
    this.traineeUpdateUseCase = traineeUpdateUseCase;
  }

  @Override
  public void create(CreateTrainingCommand command) {
    logger.debug("Creating training with name: {}", command.getTrainingName());
    try {
      Training training = trainingFactory.createFrom(command);
      traineeUpdateUseCase.updateTrainersOfTrainee(new UpdateTraineeCommand(training));
      updateTrainingPort.save(training);
      logger.info("Training with name: {} created successfully", command.getTrainingName());
    } catch (Exception e) {
      logger.error(
          "Error creating training with name: {}, Reason: {}",
          command.getTrainingName(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to create training", e);
    }
  }

  @Override
  public Training loadById(UUID trainingId) {
    if (trainingId == null) {
      throw new IllegalArgumentException("Training ID cannot be null.");
    }
    logger.debug("Fetching training with ID: {}", trainingId);
    try {
      return loadTrainingPort.findById(trainingId);
    } catch (Exception e) {
      logger.error(
          "Error fetching training with ID: {}, Reason: {}", trainingId, e.getMessage(), e);
      throw new RuntimeException("Failed to fetch training by ID", e);
    }
  }

  @Override
  public List<Training> loadAll() {
    logger.debug("Fetching all trainings.");
    try {
      return loadTrainingPort.findAll();
    } catch (Exception e) {
      logger.error("Error fetching all trainings, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainings", e);
    }
  }

  @Override
  public List<Training> findTraineeTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String trainerName, String trainingType) {
    logger.debug(
        "Fetching trainings with criteria: startDate={}, endDate={}, trainerName={}, trainingType={}",
        startDate,
        endDate,
        trainerName,
        trainingType);
    try {
      return loadTrainingPort.findTraineeTrainingsByCriteria(
          startDate, endDate, trainerName, trainingType);
    } catch (Exception e) {
      logger.error("Error fetching trainings by criteria, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainings by criteria", e);
    }
  }

  @Override
  public List<Training> findTrainerTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String traineeName) {
    logger.debug(
        "Fetching trainings for trainer with criteria: startDate={}, endDate={}, traineeName={}",
        startDate,
        endDate,
        traineeName);
    try {
      return loadTrainingPort.findTrainerTrainingsByCriteria(startDate, endDate, traineeName);
    } catch (Exception e) {
      logger.error("Error fetching trainer trainings by criteria, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainer trainings by criteria", e);
    }
  }
}
