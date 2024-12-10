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
    logger.debug("Creating training with name: {}", command.getTrainingName());
    if (command.getTrainingName() == null || command.getTrainingName().isEmpty()) {
      throw new IllegalArgumentException("Training name cannot be null or empty.");
    }
    Training training = trainingFactory.createFrom(command);
    updateTrainingPort.saveOrUpdate(training);
  }

  @Override
  public List<Training> getAllTrainings() {
    logger.debug("Fetching all trainings.");
    return loadTrainingPort.fetchAll();
  }

  @Override
  public Training getTrainingById(UUID trainingId) {
    if (trainingId == null) {
      throw new IllegalArgumentException("Training ID cannot be null.");
    }
    logger.debug("Fetching training with ID: {}", trainingId);
    Training training = loadTrainingPort.fetchById(trainingId);
    if (training == null) {
      throw new IllegalArgumentException("Training not found with ID: " + trainingId);
    }
    return training;
  }
}
