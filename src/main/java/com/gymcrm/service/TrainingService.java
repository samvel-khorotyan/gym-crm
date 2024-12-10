package com.gymcrm.service;

import com.gymcrm.command.CreateTrainingCommand;
import com.gymcrm.dao.LoadTrainingPort;
import com.gymcrm.dao.UpdateTrainingPort;
import com.gymcrm.domain.Training;
import com.gymcrm.factory.TrainingFactory;
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

  @Autowired
  public TrainingService(
      UpdateTrainingPort updateTrainingPort,
      LoadTrainingPort loadTrainingPort,
      TrainingFactory trainingFactory) {
    this.updateTrainingPort = updateTrainingPort;
    this.loadTrainingPort = loadTrainingPort;
    this.trainingFactory = trainingFactory;
  }

  @Override
  public void createTraining(CreateTrainingCommand command) {
    logger.info("Attempting to create training with name: {}", command.getTrainingName());
    if (command.getTrainingName() == null || command.getTrainingName().isEmpty()) {
      throw new IllegalArgumentException("Training name cannot be null or empty.");
    }
    Training training = trainingFactory.createFrom(command);
    updateTrainingPort.saveOrUpdate(training);
    logger.info("Training created successfully with ID: {}", training.getId());
  }

  @Override
  public List<Training> getAllTrainings() {
    logger.info("Fetching all trainings.");
    List<Training> trainings = loadTrainingPort.fetchAll();
    if (trainings.isEmpty()) {
      logger.warn("No trainings found.");
    } else {
      logger.info("Found {} trainings.", trainings.size());
    }
    return trainings;
  }

  @Override
  public Training getTrainingById(UUID trainingId) {
    logger.info("Fetching training with ID: {}", trainingId);
    if (trainingId == null) {
      throw new IllegalArgumentException("Training ID cannot be null.");
    }
    Training training = loadTrainingPort.fetchById(trainingId);
    if (training == null) {
      logger.error("Training not found with ID: {}", trainingId);
      throw new IllegalArgumentException("Training not found with ID: " + trainingId);
    }
    logger.info("Training fetched successfully: {}", training);
    return training;
  }
}
