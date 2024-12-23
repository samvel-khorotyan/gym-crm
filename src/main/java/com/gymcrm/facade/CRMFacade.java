package com.gymcrm.facade;

import com.gymcrm.trainee.application.port.input.*;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.port.input.*;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.application.port.input.CreateTrainingTypeCommand;
import com.gymcrm.trainingtype.application.port.input.TrainingTypeCreationUseCase;
import com.gymcrm.trainingtype.domain.TrainingType;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.input.LoadUserUseCase;
import com.gymcrm.user.application.port.input.UserCreationUseCase;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CRMFacade {
  private static final Logger logger = LoggerFactory.getLogger(CRMFacade.class);

  private final TrainingTypeCreationUseCase trainingTypeCreationUseCase;
  private final TraineeCreationUseCase traineeCreationUseCase;
  private final TraineeUpdateUseCase traineeUpdateUseCase;
  private final LoadTraineeUseCase loadTraineeUseCase;
  private TrainerCreationUseCase trainerCreationUseCase;
  private LoadTrainerUseCase loadTrainerUseCase;
  private TrainerUpdateUseCase trainerUpdateUseCase;
  private final TrainingCreationUseCase trainingCreationUseCase;
  private final AuthenticationUseCase authenticationUseCase;
  private final LoadTrainingUseCase loadTrainingUseCase;
  private UserCreationUseCase userCreationUseCase;
  private final LoadUserUseCase loadUserUseCase;

  @Autowired
  public CRMFacade(
      TrainingTypeCreationUseCase trainingTypeCreationUseCase,
      TraineeCreationUseCase traineeCreationUseCase,
      AuthenticationUseCase authenticationUseCase,
      TraineeUpdateUseCase traineeUpdateUseCase,
      LoadTraineeUseCase loadTraineeUseCase,
      TrainingCreationUseCase trainingCreationUseCase,
      LoadTrainingUseCase loadTrainingUseCase,
      LoadUserUseCase loadUserUseCase) {
    this.trainingTypeCreationUseCase = trainingTypeCreationUseCase;
    this.authenticationUseCase = authenticationUseCase;
    this.traineeCreationUseCase = traineeCreationUseCase;
    this.traineeUpdateUseCase = traineeUpdateUseCase;
    this.loadTraineeUseCase = loadTraineeUseCase;
    this.trainingCreationUseCase = trainingCreationUseCase;
    this.loadTrainingUseCase = loadTrainingUseCase;
    this.loadUserUseCase = loadUserUseCase;
  }

  @Autowired
  public void setTrainerCreationUseCase(TrainerCreationUseCase trainerCreationUseCase) {
    this.trainerCreationUseCase = trainerCreationUseCase;
  }

  @Autowired
  public void setLoadTrainerUseCase(LoadTrainerUseCase loadTrainerUseCase) {
    this.loadTrainerUseCase = loadTrainerUseCase;
  }

  @Autowired
  public void setTrainerUpdateUseCase(TrainerUpdateUseCase trainerUpdateUseCase) {
    this.trainerUpdateUseCase = trainerUpdateUseCase;
  }

  @Autowired
  public void setUserCreationUseCase(UserCreationUseCase userCreationUseCase) {
    this.userCreationUseCase = userCreationUseCase;
  }

  public void addTraineeWithUser(
      CreateTraineeCommand createTraineeCommand, CreateUserCommand createUserCommand) {
    logger.debug(
        "Adding trainee: {} {}", createUserCommand.getFirstName(), createUserCommand.getLastName());
    try {
      createUserCommand.setUserType(UserType.TRAINEE);
      var user = userCreationUseCase.create(createUserCommand);
      createTraineeCommand.setUser(user);
      traineeCreationUseCase.create(createTraineeCommand);
      logger.info("Trainee added successfully");
    } catch (Exception e) {
      logger.error(
          "Error adding trainee: {} {}, Reason: {}",
          createUserCommand.getFirstName(),
          createUserCommand.getLastName(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to add trainee", e);
    }
  }

  public List<Trainee> loadTrainees() {
    logger.debug("Fetching all trainees");
    try {
      return loadTraineeUseCase.loadAll();
    } catch (Exception e) {
      logger.error("Error fetching all trainees, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainees", e);
    }
  }

  public List<Training> findTraineeTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String trainerName, String trainingType) {
    logger.debug(
        "Fetching trainee trainings with criteria: startDate={}, endDate={}, trainerName={}, trainingType={}",
        startDate,
        endDate,
        trainerName,
        trainingType);
    try {
      return loadTrainingUseCase.findTraineeTrainingsByCriteria(
          startDate, endDate, trainerName, trainingType);
    } catch (Exception e) {
      logger.error("Error fetching trainee trainings by criteria, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainee trainings", e);
    }
  }

  public Trainee loadTraineeById(UUID traineeId) {
    logger.debug("Fetching trainee by ID: {}", traineeId);
    try {
      return loadTraineeUseCase.loadById(traineeId);
    } catch (Exception e) {
      logger.error("Error fetching trainee by ID: {}, Reason: {}", traineeId, e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainee by ID", e);
    }
  }

  public void modifyTrainee(UpdateTraineeCommand command) {
    logger.debug("Updating trainee with ID: {}", command.getTraineeId());
    try {
      traineeUpdateUseCase.update(command);
      logger.info("Trainee updated successfully");
    } catch (Exception e) {
      logger.error(
          "Error updating trainee with ID: {}, Reason: {}",
          command.getTraineeId(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update trainee", e);
    }
  }

  public void modifyTraineePassword(UpdateTraineePasswordCommand command) {
    logger.debug("Updating trainee password with ID: {}", command.getTraineeId());
    try {
      traineeUpdateUseCase.updatePassword(command);
      logger.info("Trainee password updated successfully");
    } catch (Exception e) {
      logger.error(
          "Error updating trainee password with ID: {}, Reason: {}",
          command.getTraineeId(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update trainee password", e);
    }
  }

  public void modifyTraineeTrainers(UUID traineeId, Map<UUID, UUID> map) {
    try {
      traineeUpdateUseCase.updateTrainersOfTrainee(traineeId, map);
    } catch (Exception e) {
      logger.error(
          "Error updating trainers for trainee with ID: {}, Reason: {}",
          traineeId,
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update trainee trainers", e);
    }
  }

  public void activateTrainee(UUID traineeId) {
    logger.debug("Activating trainee: {}", traineeId);
    try {
      traineeUpdateUseCase.activate(traineeId);
      logger.info("Trainee activated successfully");
    } catch (Exception e) {
      logger.error(
          "Error activating trainee with ID: {}, Reason: {}", traineeId, e.getMessage(), e);
      throw new RuntimeException("Failed to activate trainee", e);
    }
  }

  public void deactivateTrainee(UUID traineeId) {
    logger.debug("Deactivating trainee: {}", traineeId);
    try {
      traineeUpdateUseCase.deactivate(traineeId);
      logger.info("Trainee deactivated successfully");
    } catch (Exception e) {
      logger.error(
          "Error deactivating trainee with ID: {}, Reason: {}", traineeId, e.getMessage(), e);
      throw new RuntimeException("Failed to deactivate trainee", e);
    }
  }

  public void deleteTraineeById(UUID traineeId) {
    logger.debug("Removing trainee with ID: {}", traineeId);
    try {
      traineeUpdateUseCase.deleteById(traineeId);
      logger.info("Trainee removed successfully");
    } catch (Exception e) {
      logger.error("Error removing trainee with ID: {}, Reason: {}", traineeId, e.getMessage(), e);
      throw new RuntimeException("Failed to remove trainee", e);
    }
  }

  public void deleteTraineeByUsername(String username) {
    logger.debug("Removing trainee with username: {}", username);
    try {
      traineeUpdateUseCase.deleteByUsername(username);
      logger.info("Trainee removed successfully by username");
    } catch (Exception e) {
      logger.error(
          "Error removing trainee with username: {}, Reason: {}", username, e.getMessage(), e);
      throw new RuntimeException("Failed to remove trainee by username", e);
    }
  }

  public void addTrainerWithUser(
      CreateTrainerCommand createTrainerCommand, CreateUserCommand createUserCommand) {
    logger.debug(
        "Adding trainer: {} {}", createUserCommand.getFirstName(), createUserCommand.getLastName());
    try {
      createUserCommand.setUserType(UserType.TRAINER);
      User user = userCreationUseCase.create(createUserCommand);
      createTrainerCommand.setUser(user);
      trainerCreationUseCase.create(createTrainerCommand);
      logger.info("Trainer added successfully");
    } catch (Exception e) {
      logger.error(
          "Error adding trainer: {} {}, Reason: {}",
          createUserCommand.getFirstName(),
          createUserCommand.getLastName(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to add trainer", e);
    }
  }

  public List<Trainer> loadTrainers() {
    logger.debug("Fetching all trainers");
    try {
      return loadTrainerUseCase.loadAll();
    } catch (Exception e) {
      logger.error("Error fetching all trainers, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainers", e);
    }
  }

  public List<Trainer> loadTrainersNotAssignedToTrainee(String trainerName) {
    logger.debug("Fetching all trainers not assigned to trainee");
    try {
      return loadTrainerUseCase.loadTrainersNotAssignedToTrainee(trainerName);
    } catch (Exception e) {
      logger.error(
          "Error fetching trainers not assigned to trainee, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainers not assigned to trainee", e);
    }
  }

  public List<Training> findTrainerTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String traineeName) {
    logger.debug(
        "Fetching trainer trainings with criteria: startDate={}, endDate={}, traineeName={}",
        startDate,
        endDate,
        traineeName);
    try {
      return loadTrainingUseCase.findTrainerTrainingsByCriteria(startDate, endDate, traineeName);
    } catch (Exception e) {
      logger.error("Error fetching trainer trainings by criteria, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainer trainings by criteria", e);
    }
  }

  public Trainer loadTrainerById(UUID trainerId) {
    logger.debug("Fetching trainer by ID: {}", trainerId);
    try {
      return loadTrainerUseCase.loadById(trainerId);
    } catch (Exception e) {
      logger.error("Error fetching trainer by ID: {}, Reason: {}", trainerId, e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainer by ID", e);
    }
  }

  public void modifyTrainer(UpdateTrainerCommand command) {
    logger.debug("Updating trainer with ID: {}", command.getTrainerId());
    try {
      trainerUpdateUseCase.update(command);
      logger.info("Trainer updated successfully");
    } catch (Exception e) {
      logger.error(
          "Error updating trainer with ID: {}, Reason: {}",
          command.getTrainerId(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update trainer", e);
    }
  }

  public void modifyTrainerPassword(UpdateTrainerPasswordCommand command) {
    logger.debug("Updating trainer password with ID: {}", command.getTrainerId());
    try {
      trainerUpdateUseCase.updatePassword(command);
      logger.info("Trainer password updated successfully");
    } catch (Exception e) {
      logger.error(
          "Error updating trainer password with ID: {}, Reason: {}",
          command.getTrainerId(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to update trainer password", e);
    }
  }

  public void activateTrainer(UUID trainerId) {
    logger.debug("Activating trainer: {}", trainerId);
    try {
      trainerUpdateUseCase.activate(trainerId);
      logger.info("Trainer activated successfully");
    } catch (Exception e) {
      logger.error(
          "Error activating trainer with ID: {}, Reason: {}", trainerId, e.getMessage(), e);
      throw new RuntimeException("Failed to activate trainer", e);
    }
  }

  public void deactivateTrainer(UUID trainerId) {
    logger.debug("Deactivating trainer: {}", trainerId);
    try {
      trainerUpdateUseCase.deactivate(trainerId);
      logger.info("Trainer deactivated successfully");
    } catch (Exception e) {
      logger.error(
          "Error deactivating trainer with ID: {}, Reason: {}", trainerId, e.getMessage(), e);
      throw new RuntimeException("Failed to deactivate trainer", e);
    }
  }

  public void addTraining(CreateTrainingCommand command) {
    logger.debug("Adding training: {}", command.getTrainingName());
    try {
      trainingCreationUseCase.create(command);
      logger.info("Training added successfully");
    } catch (Exception e) {
      logger.error(
          "Error adding training: {}, Reason: {}", command.getTrainingName(), e.getMessage(), e);
      throw new RuntimeException("Failed to add training", e);
    }
  }

  public List<Training> loadTrainings() {
    logger.debug("Fetching all trainings");
    try {
      return loadTrainingUseCase.loadAll();
    } catch (Exception e) {
      logger.error("Error fetching all trainings, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainings", e);
    }
  }

  public Training loadTrainingById(UUID trainingId) {
    logger.debug("Fetching training by ID: {}", trainingId);
    try {
      return loadTrainingUseCase.loadById(trainingId);
    } catch (Exception e) {
      logger.error("Error fetching training by ID: {}, Reason: {}", trainingId, e.getMessage(), e);
      throw new RuntimeException("Failed to fetch training by ID", e);
    }
  }

  public TrainingType addTrainingType(CreateTrainingTypeCommand command) {
    logger.debug("Adding training type: {}", command.getTrainingTypeName());
    try {
      return trainingTypeCreationUseCase.create(command);
    } catch (Exception e) {
      logger.error(
          "Error adding training type: {}, Reason: {}",
          command.getTrainingTypeName(),
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to add training type", e);
    }
  }

  public boolean authenticateTrainee(String username, String password) {
    logger.debug("Authenticating trainee with username: {}", username);
    try {
      return authenticationUseCase.authenticateTrainee(username, password);
    } catch (Exception e) {
      logger.error(
          "Error authenticating trainee with username: {}, Reason: {}",
          username,
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to authenticate trainee", e);
    }
  }

  public boolean authenticateTrainer(String username, String password) {
    logger.debug("Authenticating trainer with username: {}", username);
    try {
      return authenticationUseCase.authenticateTrainer(username, password);
    } catch (Exception e) {
      logger.error(
          "Error authenticating trainer with username: {}, Reason: {}",
          username,
          e.getMessage(),
          e);
      throw new RuntimeException("Failed to authenticate trainer", e);
    }
  }

  public boolean authenticateAdmin(String username, String password) {
    logger.debug("Authenticating admin with username: {}", username);
    try {
      return authenticationUseCase.authenticateAdmin(username, password);
    } catch (Exception e) {
      logger.error(
          "Error authenticating admin with username: {}, Reason: {}", username, e.getMessage(), e);
      throw new RuntimeException("Failed to authenticate admin", e);
    }
  }

  public List<User> loadUsers() {
    logger.debug("Fetching all users");
    try {
      return loadUserUseCase.loadAll();
    } catch (Exception e) {
      logger.error("Error fetching all users, Reason: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch users", e);
    }
  }

  public Trainee loadTraineeByUsername(String username) {
    logger.debug("Fetching trainee by username: {}", username);
    try {
      return loadUserUseCase.loadUserByUsername(username).getTrainee();
    } catch (Exception e) {
      logger.error(
          "Error fetching trainee by username: {}, Reason: {}", username, e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainee by username", e);
    }
  }

  public Trainer loadTrainerByUsername(String username) {
    logger.debug("Fetching trainer by username: {}", username);
    try {
      return loadUserUseCase.loadUserByUsername(username).getTrainer();
    } catch (Exception e) {
      logger.error(
          "Error fetching trainer by username: {}, Reason: {}", username, e.getMessage(), e);
      throw new RuntimeException("Failed to fetch trainer by username", e);
    }
  }
}
