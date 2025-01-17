package com.gymcrm.trainee.application;

import com.gymcrm.common.exception.BadRequestException;
import com.gymcrm.trainee.adapter.input.web.mapper.TraineeUpdateMapper;
import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainee.application.factory.TraineeFactory;
import com.gymcrm.trainee.application.port.input.*;
import com.gymcrm.trainee.application.port.output.LoadTraineePort;
import com.gymcrm.trainee.application.port.output.UpdateTraineePort;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.factory.TrainingFactory;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.user.adapter.input.web.mapper.UserUpdateMapper;
import com.gymcrm.user.application.port.input.*;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  private final UpdateUserPort updateUserPort;
  private final LoadTrainerPort loadTrainerPort;
  private final UpdateTrainingPort updateTrainingPort;
  private final TraineeFactory traineeFactory;
  private final UserCreationUseCase userCreationUseCase;
  private final TraineeUpdateMapper traineeUpdateMapper;
  private final UserUpdateMapper userUpdateMapper;
  private final LoadTrainingPort loadTrainingPort;
  private final TrainingFactory trainingFactory;

  @Autowired
  public TraineeService(
      UpdateTraineePort updateTraineePort,
      UpdateUserPort updateUserPort,
      LoadTrainerPort loadTrainerPort,
      UpdateTrainingPort updateTrainingPort,
      UserCreationUseCase userCreationUseCase,
      TraineeFactory traineeFactory,
      TraineeUpdateMapper traineeUpdateMapper,
      UserUpdateMapper userUpdateMapper,
      LoadTrainingPort loadTrainingPort,
      TrainingFactory trainingFactory) {
    this.updateTraineePort = updateTraineePort;
    this.updateUserPort = updateUserPort;
    this.loadTrainerPort = loadTrainerPort;
    this.updateTrainingPort = updateTrainingPort;
    this.traineeFactory = traineeFactory;
    this.userCreationUseCase = userCreationUseCase;
    this.traineeUpdateMapper = traineeUpdateMapper;
    this.userUpdateMapper = userUpdateMapper;
    this.loadTrainingPort = loadTrainingPort;
    this.trainingFactory = trainingFactory;
  }

  @Autowired
  public void setLoadTraineePort(LoadTraineePort loadTraineePort) {
    this.loadTraineePort = loadTraineePort;
  }

  @Override
  public Trainee create(CreateTraineeCommand command) {
    try {
      command.setUser(
          userCreationUseCase.create(
              new CreateUserCommand(
                  command.getFirstName(), command.getLastName(), UserType.TRAINEE)));
      return updateTraineePort.save(traineeFactory.createFrom(command));
    } catch (Exception e) {
      logger.error("Failed to create trainee: {}", e.getMessage(), e);
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
  public Trainee loadByUsername(String username) {
    return loadTraineePort.findByUsernameWithTrainers(username);
  }

  @Transactional
  @Override
  public Trainee update(UpdateTraineeCommand command) {
    try {
      Trainee existingTrainee = loadTraineePort.findByIdWithTrainers(command.getTraineeId());
      userUpdateMapper.updateUserFromCommand(
          new UpdateUserCommand(
              command.getFirstName(), command.getLastName(), command.getIsActive()),
          existingTrainee.getUser());
      traineeUpdateMapper.updateTraineeFromCommand(command, existingTrainee);

      return updateTraineePort.save(existingTrainee);
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
  public void activateDeactivate(ActivateDeactivateTraineeCommand command) {
    try {
      User user = loadTraineePort.findByUsername(command.getUsername()).getUser();

      user.setIsActive(command.getIsActive());
      updateUserPort.save(user);
    } catch (TraineeNotFoundException e) {
      logger.warn("Trainee with username: {} not found.", command.getUsername());
      throw e;
    } catch (Exception e) {
      String errorMessage =
          String.format("Failed to activate/deactivate trainee. Cause: %s", e.getMessage());
      logger.error(errorMessage, e);
      throw new RuntimeException(errorMessage, e);
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
  public Trainee updateTraineeTrainers(UpdateTraineeTrainersCommand command) {
    try {
      Trainee trainee = loadTraineePort.findByUsername(command.getTraineeUsername());

      List<Trainer> trainers = loadTrainerPort.findAllByUsernames(command.getTrainerUsernames());
      var x = loadTrainingPort.findAllByTrainerUsernames(command.getTrainerUsernames());
      List<Training> trainings =
          x.stream()
              .collect(
                  Collectors.toMap(
                      e -> e.getTrainer().getId(),
                      Function.identity(),
                      (existing, replacement) -> existing))
              .values()
              .stream()
              .toList();

      validateTrainers(command, trainers, trainings);
      updateTrainingPort.deleteByTraineeId(trainee.getId());

      trainee.setTrainers(trainers);

      updateTrainingPort.saveAll(
          trainings.stream()
              .map(
                  training ->
                      trainingFactory.createFrom(
                          new CreateTrainingCommand(
                              trainee,
                              training.getTrainer(),
                              training.getTrainingName(),
                              training.getTrainingType(),
                              training.getTrainingDate(),
                              training.getTrainingDuration())))
              .toList());
      return updateTraineePort.save(trainee);
    } catch (BadRequestException | TraineeNotFoundException | TrainerNotFoundException e) {
      logger.warn("Validation error: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error while updating trainers for trainee: {}",
          command.getTraineeUsername(),
          e);
      throw new RuntimeException("Failed to update trainee trainers", e);
    }
  }

  private void validateTrainers(
      UpdateTraineeTrainersCommand command, List<Trainer> trainers, List<Training> trainings) {
    if (trainers.isEmpty()) {
      throw new TrainerNotFoundException(
          "No trainers found for usernames: " + command.getTrainerUsernames());
    }

    if (trainers.size() != trainings.size()) {
      List<String> trainerUsernames =
          trainings.stream()
              .map(training -> training.getTrainer().getUser().getUsername())
              .toList();

      List<String> missingUsernames =
          trainers.stream()
              .map(trainer -> trainer.getUser().getUsername())
              .filter(username -> !trainerUsernames.contains(username))
              .toList();

      throw new BadRequestException(
          "The following trainers do not have corresponding trainings: "
              + String.join(", ", missingUsernames));
    }
  }
}
