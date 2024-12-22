package com.gymcrm.trainee.application;

import com.gymcrm.trainee.application.port.input.*;
import com.gymcrm.trainee.application.port.output.LoadTraineePort;
import com.gymcrm.trainee.application.port.output.UpdateTraineePort;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TraineeService
    implements TraineeCreationUseCase, TraineeUpdateUseCase, LoadTraineeUseCase {
  private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

  private final UpdateTraineePort updateTraineePort;
  private LoadTraineePort loadTraineePort;
  private final AuthenticationUseCase authenticationUseCase;
  private final UpdateUserPort updateUserPort;
  private final LoadTrainerPort loadTrainerPort;
  private final UpdateTrainingPort updateTrainingPort;

  @Autowired
  public TraineeService(
      UpdateTraineePort updateTraineePort,
      AuthenticationUseCase authenticationUseCase,
      UpdateUserPort updateUserPort,
      LoadTrainerPort loadTrainerPort,
      UpdateTrainingPort updateTrainingPort) {
    this.updateTraineePort = updateTraineePort;
    this.authenticationUseCase = authenticationUseCase;
    this.updateUserPort = updateUserPort;
    this.loadTrainerPort = loadTrainerPort;
    this.updateTrainingPort = updateTrainingPort;
  }

  @Autowired
  public void setLoadTraineePort(LoadTraineePort loadTraineePort) {
    this.loadTraineePort = loadTraineePort;
  }

  @Override
  public void create(Trainee trainee) {
    logger.debug("Creating new trainee with ID: {}", trainee.getId());
    try {
      updateTraineePort.save(trainee);
    } catch (Exception e) {
      logger.error(
          "Error creating trainee with ID: {}, Reason: {}", trainee.getId(), e.getMessage(), e);
      throw new RuntimeException("Failed to create trainee", e);
    }
  }

  @Override
  public List<Trainee> loadAll() {
    logger.debug("Fetching all trainees.");
    try {
      return loadTraineePort.findAll();
    } catch (Exception e) {
      logger.error("Error fetching all trainees, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch all trainees", e);
    }
  }

  @Override
  public Trainee loadById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Fetching trainee with ID: {}", id);
    try {
      return loadTraineePort.findById(id);
    } catch (Exception e) {
      logger.error("Error fetching trainee with ID: {}, Reason: {}", id, e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainee by ID", e);
    }
  }

  @Override
  public void update(UpdateTraineeCommand command) {
    if (command.getTraineeId() == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Updating trainee with ID: {}", command.getTraineeId());

    try {
      Trainee existingTrainee = loadTraineePort.findById(command.getTraineeId());
      existingTrainee.setAddress(command.getAddress());
      existingTrainee.setDateOfBirth(command.getDateOfBirth());
      updateTraineePort.save(existingTrainee);
    } catch (Exception e) {
      logger.error(
          "Error updating trainee with ID: {}, Reason: {}",
          command.getTraineeId(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update trainee", e);
    }
  }

  @Override
  public void updatePassword(UpdateTraineePasswordCommand command) {
    logger.debug("Updating password for trainee with username: {}", command.getUsername());
    try {
      authenticationUseCase.authenticateTrainee(command.getUsername(), command.getOldPassword());
      User user = loadTraineePort.findById(command.getTraineeId()).getUser();
      user.setPassword(command.getNewPassword());
      updateUserPort.save(user);
    } catch (Exception e) {
      logger.error(
          "Error updating password for trainee with username: {}, Reason: {}",
          command.getUsername(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update trainee password", e);
    }
  }

  @Override
  public void activate(UUID traineeId) {
    logger.debug("Activating trainee with ID: {}", traineeId);
    try {
      User user = loadTraineePort.findById(traineeId).getUser();
      user.setIsActive(true);
      updateUserPort.save(user);
    } catch (Exception e) {
      logger.error(
          "Error activating trainee with ID: {}, Reason: {}", traineeId, e.getMessage(), e);
      throw new RuntimeException("Failed to activate trainee", e);
    }
  }

  @Override
  public void deactivate(UUID traineeId) {
    logger.debug("Deactivating trainee with ID: {}", traineeId);
    try {
      User user = loadTraineePort.findById(traineeId).getUser();
      user.setIsActive(false);
      updateUserPort.save(user);
    } catch (Exception e) {
      logger.error(
          "Error deactivating trainee with ID: {}, Reason: {}", traineeId, e.getMessage(), e);
      throw new RuntimeException("Failed to deactivate trainee", e);
    }
  }

  @Override
  public void deleteById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Deleting trainee with ID: {}", id);
    try {
      updateTraineePort.deleteById(id);
    } catch (Exception e) {
      logger.error("Error deleting trainee with ID: {}, Reason: {}", id, e.getMessage(), e);
      throw new RuntimeException("Failed to delete trainee", e);
    }
  }

  @Override
  public void deleteByUsername(String username) {
    logger.debug("Deleting trainee with username: {}", username);
    try {
      updateTraineePort.deleteByUsername(username);
    } catch (Exception e) {
      logger.error(
          "Error deleting trainee with username: {}, Reason: {}", username, e.getMessage(), e);
      throw new RuntimeException("Failed to delete trainee by username", e);
    }
  }

  @Transactional
  @Override
  public void updateTrainersOfTrainee(UUID traineeId, Map<UUID, UUID> trainerToTrainingMap) {
    logger.debug("Updating trainers for trainee with ID: {}", traineeId);
    try {
      Trainee trainee = loadTraineePort.findById(traineeId);

      List<Training> currentTrainings = trainee.getTrainings();

      if (trainerToTrainingMap.size() != currentTrainings.size()) {
        throw new IllegalArgumentException(
            "The number of trainers does not match the number of trainings for this trainee.");
      }

      for (Training training : currentTrainings) {
        UUID newTrainerId = trainerToTrainingMap.get(training.getId());
        if (newTrainerId == null) {
          throw new IllegalArgumentException(
              "No trainer provided for training with ID: " + training.getId());
        }

        Trainer newTrainer = loadTrainerPort.findById(newTrainerId);

        training.setTrainer(newTrainer);
        updateTrainersOfTrainee(new UpdateTraineeCommand(training));
        updateTrainingPort.save(training);
      }
      logger.info("Trainers for trainee with ID: {} updated successfully", traineeId);
    } catch (Exception e) {
      logger.error(
          "Error updating trainers for trainee with ID: {}, Reason: {}",
          traineeId,
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update trainers for trainee", e);
    }
  }

  @Override
  public void updateTrainersOfTrainee(UpdateTraineeCommand command) {
    logger.debug("Updating trainee for training with ID: {}", command.getTraining().getId());
    try {
      Trainee trainee = command.getTraining().getTrainee();
      trainee.setTrainers(new ArrayList<>(List.of(command.getTraining().getTrainer())));
      updateTraineePort.save(trainee);
      logger.info(
          "Trainee for training with ID: {} updated successfully", command.getTraining().getId());
    } catch (Exception e) {
      logger.error(
          "Error updating trainee for training with ID: {}, Reason: {}",
          command.getTraining().getId(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update trainee for training", e);
    }
  }
}
