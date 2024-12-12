package com.gymcrm.service;

import com.gymcrm.command.UpdateTraineeCommand;
import com.gymcrm.domain.Trainee;
import com.gymcrm.prot.LoadTraineePort;
import com.gymcrm.prot.UpdateTraineePort;
import com.gymcrm.usecase.LoadTraineeUseCase;
import com.gymcrm.usecase.TraineeCreationUseCase;
import com.gymcrm.usecase.TraineeUpdateUseCase;
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
    logger.debug("Creating new trainee with ID: {}", trainee.getId());
    updateTraineePort.saveOrUpdate(trainee);
  }

  @Override
  public List<Trainee> getAllTrainees() {
    logger.debug("Fetching all trainees.");
    return loadTraineePort.fetchAll();
  }

  @Override
  public Trainee getTraineeById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Fetching trainee with ID: {}", id);
    Trainee trainee = loadTraineePort.fetchById(id);
    if (trainee == null) {
      throw new IllegalArgumentException("Trainee not found with ID: " + id);
    }
    return trainee;
  }

  @Override
  public void updateTrainee(UpdateTraineeCommand command) {
    if (command.getTraineeId() == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Updating trainee with ID: {}", command.getTraineeId());

    Trainee existingTrainee = loadTraineePort.fetchById(command.getTraineeId());
    if (existingTrainee == null) {
      throw new IllegalArgumentException("Trainee not found with ID: " + command.getTraineeId());
    }

    existingTrainee.setAddress(command.getAddress());
    existingTrainee.setDateOfBirth(command.getDateOfBirth());
    updateTraineePort.saveOrUpdate(existingTrainee);
  }

  @Override
  public void removeTrainee(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Deleting trainee with ID: {}", id);

    Trainee trainee = loadTraineePort.fetchById(id);
    if (trainee == null) {
      throw new IllegalArgumentException("Trainee not found with ID: " + id);
    }

    updateTraineePort.removeById(id);
  }
}
