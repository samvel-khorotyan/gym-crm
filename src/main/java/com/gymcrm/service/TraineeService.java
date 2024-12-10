package com.gymcrm.service;

import com.gymcrm.dao.LoadTraineePort;
import com.gymcrm.dao.UpdateTraineePort;
import com.gymcrm.domain.Trainee;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeService
    implements TraineeCreationUseCase, TraineeUpdateUseCase, LoadTraineeUseCase {
  private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

  private final UpdateTraineePort updateTraineePort;
  private LoadTraineePort loadTraineePort;

  @Autowired
  public TraineeService(UpdateTraineePort updateTraineePort) {
    this.updateTraineePort = updateTraineePort;
  }

  @Autowired
  public void setLoadTraineePort(LoadTraineePort loadTraineePort) {
    this.loadTraineePort = loadTraineePort;
  }

  @Override
  public void createTrainee(Trainee trainee) {
    logger.info("Creating new trainee with ID: {}", trainee.getId());
    updateTraineePort.saveOrUpdate(trainee);
    logger.info("Trainee created successfully: {}", trainee);
  }

  @Override
  public List<Trainee> getAllTrainees() {
    logger.info("Fetching all trainees.");
    List<Trainee> trainees = loadTraineePort.fetchAll();
    logger.info("Found {} trainees.", trainees.size());
    return trainees;
  }

  @Override
  public Trainee getTraineeById(UUID id) {
    logger.info("Fetching trainee with ID: {}", id);
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    Trainee trainee = loadTraineePort.fetchById(id);
    if (trainee == null) {
      throw new IllegalArgumentException("Trainee not found with ID: " + id);
    }
    logger.info("Trainee fetched successfully: {}", trainee);
    return trainee;
  }

  @Override
  public void updateTrainee(UUID id, Trainee trainee) {
    logger.info("Updating trainee with ID: {}", id);
    if (id == null || trainee == null) {
      throw new IllegalArgumentException("ID and Trainee cannot be null.");
    }

    Trainee existingTrainee = loadTraineePort.fetchById(id);
    if (existingTrainee == null) {
      throw new IllegalArgumentException("Trainee not found with ID: " + id);
    }

    existingTrainee.setAddress(trainee.getAddress());
    existingTrainee.setDateOfBirth(trainee.getDateOfBirth());
    updateTraineePort.saveOrUpdate(existingTrainee);
    logger.info("Trainee updated successfully: {}", existingTrainee);
  }

  @Override
  public void removeTrainee(UUID id) {
    logger.info("Deleting trainee with ID: {}", id);
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }

    Trainee trainee = loadTraineePort.fetchById(id);
    if (trainee == null) {
      throw new IllegalArgumentException("Trainee not found with ID: " + id);
    }

    updateTraineePort.removeById(id);
    logger.info("Trainee deleted successfully: {}", id);
  }
}
