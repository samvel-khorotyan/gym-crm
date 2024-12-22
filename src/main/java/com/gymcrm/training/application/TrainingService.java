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
    if (command.getTrainingName() == null || command.getTrainingName().isEmpty()) {
      throw new IllegalArgumentException("Training name cannot be null or empty.");
    }
    Training training = trainingFactory.createFrom(command);
    traineeUpdateUseCase.updateTrainersOfTrainee(new UpdateTraineeCommand(training));
    updateTrainingPort.save(training);
  }

  @Override
  public Training loadById(UUID trainingId) {
    if (trainingId == null) {
      throw new IllegalArgumentException("Training ID cannot be null.");
    }
    logger.debug("Fetching training with ID: {}", trainingId);
    Training training = loadTrainingPort.findById(trainingId);
    if (training == null) {
      throw new IllegalArgumentException("Training not found with ID: " + trainingId);
    }
    return training;
  }

  @Override
  public List<Training> loadAll() {
    logger.debug("Fetching all trainings.");
    return loadTrainingPort.findAll();
  }

  @Override
  public List<Training> findTraineeTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String trainerName, String trainingType) {
    return loadTrainingPort.findTraineeTrainingsByCriteria(
        startDate, endDate, trainerName, trainingType);
  }

  @Override
  public List<Training> findTrainerTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String traineeName) {
    return loadTrainingPort.findTrainerTrainingsByCriteria(startDate, endDate, traineeName);
  }
}
