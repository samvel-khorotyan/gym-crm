package com.gymcrm.trainer.application;

import com.gymcrm.trainer.application.factory.TrainerFactory;
import com.gymcrm.trainer.application.port.input.*;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService
    implements TrainerCreationUseCase, TrainerUpdateUseCase, LoadTrainerUseCase {
  private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

  private final UpdateTrainerPort updateTrainerPort;
  private LoadTrainerPort loadTrainerPort;
  private final TrainerFactory trainerFactory;
  private final AuthenticationUseCase authenticationUseCase;
  private final UpdateUserPort updateUserPort;

  @Autowired
  public TrainerService(
      UpdateTrainerPort updateTrainerPort,
      TrainerFactory trainerFactory,
      AuthenticationUseCase authenticationUseCase,
      UpdateUserPort updateUserPort) {
    this.updateTrainerPort = updateTrainerPort;
    this.trainerFactory = trainerFactory;
    this.authenticationUseCase = authenticationUseCase;
    this.updateUserPort = updateUserPort;
  }

  @Autowired
  public void setLoadTrainerPort(LoadTrainerPort loadTrainerPort) {
    this.loadTrainerPort = loadTrainerPort;
  }

  @Override
  public void create(CreateTrainerCommand command) {
    if (command.getSpecialization() == null) {
      throw new IllegalArgumentException("Specialization cannot be null.");
    }

    Trainer trainer = trainerFactory.createFrom(command);

    logger.debug("Creating trainer with ID: {}", trainer.getId());
    try {
      updateTrainerPort.save(trainer);
      logger.info("Trainer with ID: {} created successfully", trainer.getId());
    } catch (Exception e) {
      logger.error(
          "Error creating trainer with ID: {}, Reason: {}", trainer.getId(), e.getMessage(), e);
      throw new RuntimeException("Failed to create trainer", e);
    }
  }

  @Override
  public List<Trainer> loadAll() {
    logger.debug("Fetching all trainers.");
    try {
      return loadTrainerPort.findAll();
    } catch (Exception e) {
      logger.error("Error fetching all trainers, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainers", e);
    }
  }

  @Override
  public List<Trainer> loadTrainersNotAssignedToTrainee(String trainerName) {
    logger.debug("Fetching trainers not assigned to trainee with name: {}", trainerName);
    try {
      return loadTrainerPort.findTrainersNotAssignedToTrainee(trainerName);
    } catch (Exception e) {
      logger.error(
          "Error fetching trainers not assigned to trainee, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainers not assigned to trainee", e);
    }
  }

  @Override
  public Trainer loadById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Fetching trainer with ID: {}", id);
    try {
      return loadTrainerPort.findById(id);
    } catch (Exception e) {
      logger.error("Error fetching trainer with ID: {}, Reason: {}", id, e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainer by ID", e);
    }
  }

  @Override
  public void update(UpdateTrainerCommand command) {
    if (command.getTrainerId() == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Updating trainer with ID: {}", command.getTrainerId());

    try {
      Trainer existingTrainer = loadTrainerPort.findById(command.getTrainerId());
      existingTrainer.setSpecialization(command.getSpecialization());
      updateTrainerPort.save(existingTrainer);
      logger.info("Trainer with ID: {} updated successfully", command.getTrainerId());
    } catch (Exception e) {
      logger.error(
          "Error updating trainer with ID: {}, Reason: {}",
          command.getTrainerId(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update trainer", e);
    }
  }

  @Override
  public void updatePassword(UpdateTrainerPasswordCommand command) {
    logger.debug("Updating password for trainer with username: {}", command.getUsername());
    try {
      authenticationUseCase.authenticateTrainer(command.getUsername(), command.getOldPassword());
      User user = loadTrainerPort.findById(command.getTrainerId()).getUser();
      user.setPassword(command.getNewPassword());
      updateUserPort.save(user);
      logger.info(
          "Password updated successfully for trainer with username: {}", command.getUsername());
    } catch (Exception e) {
      logger.error(
          "Error updating password for trainer with username: {}, Reason: {}",
          command.getUsername(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update password for trainer", e);
    }
  }

  @Override
  public boolean activateDeactivate(UUID trainerId) {
    try {
      User user = loadTrainerPort.findById(trainerId).getUser();
      var isActive = user.getIsActive();
      user.setIsActive(!isActive);
      updateUserPort.save(user);
      return user.getIsActive();
    } catch (Exception e) {
      throw new RuntimeException(
          String.format(
              "Failed to activate/deactivate trainer with ID: %s. Cause: %s",
              trainerId, e.getMessage()),
          e);
    }
  }
}
