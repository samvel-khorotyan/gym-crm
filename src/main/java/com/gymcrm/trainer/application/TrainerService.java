package com.gymcrm.trainer.application;

import com.gymcrm.trainer.adapter.input.web.mapper.TrainerUpdateMapper;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.factory.TrainerFactory;
import com.gymcrm.trainer.application.port.input.*;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.adapter.input.web.mapper.UserUpdateMapper;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.input.UpdateUserCommand;
import com.gymcrm.user.application.port.input.UserCreationUseCase;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainerService
    implements TrainerCreationUseCase, TrainerUpdateUseCase, LoadTrainerUseCase {
  private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

  private final UpdateTrainerPort updateTrainerPort;
  private LoadTrainerPort loadTrainerPort;
  private final TrainerFactory trainerFactory;
  private final UserCreationUseCase userCreationUseCase;
  private final UpdateUserPort updateUserPort;
  private final UserUpdateMapper userUpdateMapper;
  private final TrainerUpdateMapper trainerUpdateMapper;

  @Autowired
  public TrainerService(
      UpdateTrainerPort updateTrainerPort,
      TrainerFactory trainerFactory,
      UserCreationUseCase userCreationUseCase,
      UpdateUserPort updateUserPort,
      UserUpdateMapper userUpdateMapper,
      TrainerUpdateMapper trainerUpdateMapper) {
    this.updateTrainerPort = updateTrainerPort;
    this.trainerFactory = trainerFactory;
    this.userCreationUseCase = userCreationUseCase;
    this.updateUserPort = updateUserPort;
    this.userUpdateMapper = userUpdateMapper;
    this.trainerUpdateMapper = trainerUpdateMapper;
  }

  @Autowired
  public void setLoadTrainerPort(LoadTrainerPort loadTrainerPort) {
    this.loadTrainerPort = loadTrainerPort;
  }

  @Override
  public Trainer create(CreateTrainerCommand command) {
    try {
      command.setUser(
          userCreationUseCase.create(
              new CreateUserCommand(
                  command.getFirstName(), command.getLastName(), UserType.TRAINER)));
      return updateTrainerPort.save(trainerFactory.createFrom(command));
    } catch (Exception e) {
      logger.error("Failed to create trainer: {}", e.getMessage(), e);
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
  public List<Trainer> loadActiveTrainersNotAssignedToTrainee(String traineeUsername) {
    try {
      return loadTrainerPort.findActiveTrainersNotAssignedToTrainee(traineeUsername);
    } catch (Exception e) {
      logger.error(
          "Error fetching trainers not assigned to trainee, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainers not assigned to trainee", e);
    }
  }

  @Override
  public Trainer loadByUsername(String username) {
    return loadTrainerPort.findByUsernameWithTrainees(username);
  }

  @Transactional
  @Override
  public Trainer update(UpdateTrainerCommand command) {
    try {
      Trainer existingTrainer = loadTrainerPort.findByIdWithTrainees(command.getTrainerId());
      userUpdateMapper.updateUserFromCommand(
          new UpdateUserCommand(
              command.getFirstName(), command.getLastName(), command.getIsActive()),
          existingTrainer.getUser());
      trainerUpdateMapper.updateTrainerFromCommand(command, existingTrainer);

      return updateTrainerPort.save(existingTrainer);
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
  public void activateDeactivate(ActivateDeactivateTrainerCommand command) {
    try {
      User user = loadTrainerPort.findByUsername(command.getUsername()).getUser();

      user.setIsActive(command.getIsActive());
      updateUserPort.save(user);
    } catch (TrainerNotFoundException e) {
      logger.warn("Trainer with username: {} not found.", command.getUsername());
      throw e;
    } catch (Exception e) {
      String errorMessage =
          String.format("Failed to activate/deactivate trainer. Cause: %s", e.getMessage());
      logger.error(errorMessage, e);
      throw new RuntimeException(errorMessage, e);
    }
  }
}
