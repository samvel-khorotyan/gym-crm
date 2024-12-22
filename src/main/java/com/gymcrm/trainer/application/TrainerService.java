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
    updateTrainerPort.save(trainer);
  }

  @Override
  public List<Trainer> loadAll() {
    logger.debug("Fetching all trainers.");
    return loadTrainerPort.findAll();
  }

  @Override
  public List<Trainer> loadTrainersNotAssignedToTrainee(String trainerName) {
    return loadTrainerPort.findTrainersNotAssignedToTrainee(trainerName);
  }

  @Override
  public Trainer loadById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Fetching trainer with ID: {}", id);
    Trainer trainer = loadTrainerPort.findById(id);
    if (trainer == null) {
      throw new IllegalArgumentException("Trainer not found with ID: " + id);
    }
    return trainer;
  }

  @Override
  public void update(UpdateTrainerCommand command) {
    if (command.getTrainerId() == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    logger.debug("Updating trainer with ID: {}", command.getTrainerId());

    Trainer existingTrainer = loadTrainerPort.findById(command.getTrainerId());
    if (existingTrainer == null) {
      throw new IllegalArgumentException("Trainer not found with ID: " + command.getTrainerId());
    }

    existingTrainer.setSpecialization(command.getSpecialization());
    updateTrainerPort.save(existingTrainer);
  }

  @Override
  public void updatePassword(UpdateTrainerPasswordCommand command) {
    authenticationUseCase.authenticateTrainer(command.getUsername(), command.getOldPassword());
    User user = loadTrainerPort.findById(command.getTrainerId()).getUser();
    user.setPassword(command.getNewPassword());
    updateUserPort.save(user);
  }

  @Override
  public void activate(UUID trainerId) {
    User user = loadTrainerPort.findById(trainerId).getUser();
    user.setIsActive(true);
    updateUserPort.save(user);
  }

  @Override
  public void deactivate(UUID trainerId) {
    User user = loadTrainerPort.findById(trainerId).getUser();
    user.setIsActive(false);
    updateUserPort.save(user);
  }
}
