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
    updateTraineePort.save(trainee);
  }

  @Override
  public List<Trainee> loadAll() {
    logger.debug("Fetching all trainees.");
    return loadTraineePort.findAll();
  }

  @Override
  public Trainee loadById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Fetching trainee with ID: {}", id);
    return loadTraineePort.findById(id);
  }

  @Override
  public void update(UpdateTraineeCommand command) {
    if (command.getTraineeId() == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Updating trainee with ID: {}", command.getTraineeId());

    Trainee existingTrainee = loadTraineePort.findById(command.getTraineeId());
    if (existingTrainee == null) {
      throw new IllegalArgumentException("Trainee not found with ID: " + command.getTraineeId());
    }

    existingTrainee.setAddress(command.getAddress());
    existingTrainee.setDateOfBirth(command.getDateOfBirth());
    updateTraineePort.save(existingTrainee);
  }

  @Override
  public void updatePassword(UpdateTraineePasswordCommand command) {
    authenticationUseCase.authenticateTrainee(command.getUsername(), command.getOldPassword());
    User user = loadTraineePort.findById(command.getTraineeId()).getUser();
    user.setPassword(command.getNewPassword());
    updateUserPort.save(user);
  }

  @Override
  public void activate(UUID traineeId) {
    User user = loadTraineePort.findById(traineeId).getUser();
    user.setIsActive(true);
    updateUserPort.save(user);
  }

  @Override
  public void deactivate(UUID traineeId) {
    User user = loadTraineePort.findById(traineeId).getUser();
    user.setIsActive(false);
    updateUserPort.save(user);
  }

  @Override
  public void deleteById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Deleting trainee with ID: {}", id);

    Trainee trainee = loadTraineePort.findById(id);
    if (trainee == null) {
      throw new IllegalArgumentException("Trainee not found with ID: " + id);
    }

    updateTraineePort.deleteById(id);
  }

  @Override
  public void deleteByUsername(String username) {
    updateTraineePort.deleteByUsername(username);
  }

  @Transactional
  @Override
  public void updateTrainersOfTrainee(UUID traineeId, Map<UUID, UUID> trainerToTrainingMap) {
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
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void updateTrainersOfTrainee(UpdateTraineeCommand command) {
    try {
      Trainee trainee = command.getTraining().getTrainee();
      trainee.setTrainers(new ArrayList<>(List.of(command.getTraining().getTrainer())));
      updateTraineePort.save(trainee);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
}
