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
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TraineeService implements TraineeCreationUseCase, TraineeUpdateUseCase, LoadTraineeUseCase {
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
	public TraineeService(UpdateTraineePort updateTraineePort, UpdateUserPort updateUserPort,
	        LoadTrainerPort loadTrainerPort, UpdateTrainingPort updateTrainingPort,
	        UserCreationUseCase userCreationUseCase, TraineeFactory traineeFactory,
	        TraineeUpdateMapper traineeUpdateMapper, UserUpdateMapper userUpdateMapper,
	        LoadTrainingPort loadTrainingPort, TrainingFactory trainingFactory) {
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
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Creating user for trainee with first name: {}, last name: {}", transactionId,
		        command.getFirstName(), command.getLastName());

		try {
			User user = userCreationUseCase
			        .create(new CreateUserCommand(command.getFirstName(), command.getLastName(), UserType.TRAINEE));
			command.setUser(user);

			logger.debug("Transaction ID: {} - User created successfully with username: {}", transactionId,
			        user.getUsername());

			Trainee trainee = updateTraineePort.save(traineeFactory.createFrom(command));

			logger.info("Transaction ID: {} - Trainee saved successfully with ID: {}", transactionId, trainee.getId());
			return trainee;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to create trainee. Reason: {}", transactionId, e.getMessage(), e);
			throw e;
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
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Loading trainee by username: {}", transactionId, username);

		try {
			Trainee trainee = loadTraineePort.findByUsernameWithTrainers(username);

			logger.info("Transaction ID: {} - Successfully loaded trainee with username: {}", transactionId, username);

			return trainee;
		} catch (TraineeNotFoundException e) {
			logger.warn("Transaction ID: {} - Trainee not found with username: {}", transactionId, username);
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to load trainee with username: {}", transactionId, e.getMessage(),
			        e);
			throw e;
		}
	}

	@Transactional
	@Override
	public Trainee update(UpdateTraineeCommand command) {
		String transactionId = MDC.get("transactionId");
		logger.info("Transaction ID: {} - Updating trainee with ID: {}", transactionId, command.getTraineeId());

		try {
			Trainee existingTrainee = loadTraineePort.findByIdWithTrainers(command.getTraineeId());

			userUpdateMapper.updateUserFromCommand(
			        new UpdateUserCommand(command.getFirstName(), command.getLastName(), command.getIsActive()),
			        existingTrainee.getUser());

			traineeUpdateMapper.updateTraineeFromCommand(command, existingTrainee);

			logger.info("Transaction ID: {} - Successfully updated trainee with ID: {}", transactionId,
			        command.getTraineeId());

			return updateTraineePort.save(existingTrainee);
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to update trainee with ID: {}, Reason: {}", transactionId,
			        command.getTraineeId(), e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void activateDeactivate(ActivateDeactivateTraineeCommand command) {
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Updating state for trainee: {}", transactionId, command.getUsername());

		try {
			User user = loadTraineePort.findByUsername(command.getUsername()).getUser();
			user.setIsActive(command.getIsActive());
			updateUserPort.save(user);

			logger.info("Transaction ID: {} - Successfully updated state for trainee: {}", transactionId,
			        command.getUsername());
		} catch (TraineeNotFoundException e) {
			logger.warn("Transaction ID: {} - Trainee not found: {}", transactionId, command.getUsername());
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to update state for trainee: {}, Reason: {}", transactionId,
			        command.getUsername(), e.getMessage(), e);
			throw new RuntimeException("Failed to update state for trainee.", e);
		}
	}

	@Override
	public void deleteByUsername(String username) {
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Deleting trainee with username: {}", transactionId, username);

		try {
			updateTraineePort.deleteByUsername(username);
			logger.info("Transaction ID: {} - Successfully deleted trainee with username: {}", transactionId, username);
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to delete trainee with username: {}, Reason: {}", transactionId,
			        username, e.getMessage(), e);
			throw new RuntimeException("Failed to delete trainee by username", e);
		}
	}

	@Transactional
	@Override
	public Trainee updateTraineeTrainers(UpdateTraineeTrainersCommand command) {
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Updating trainers for trainee: {}", transactionId,
		        command.getTraineeUsername());

		try {
			Trainee trainee = loadTraineePort.findByUsername(command.getTraineeUsername());

			List<Trainer> trainers = loadTrainerPort.findAllByUsernames(command.getTrainerUsernames());
			var trainingData = loadTrainingPort.findAllByTrainerUsernames(command.getTrainerUsernames());
			List<Training> trainings = trainingData.stream().collect(Collectors.toMap(e -> e.getTrainer().getId(),
			        Function.identity(), (existing, replacement) -> existing)).values().stream().toList();

			validateTrainers(command, trainers, trainings);

			updateTrainingPort.deleteByTraineeId(trainee.getId());
			trainee.setTrainers(trainers);
			updateTrainingPort.saveAll(trainings.stream()
			        .map(training -> trainingFactory.createFrom(new CreateTrainingCommand(trainee,
			                training.getTrainer(), training.getTrainingName(), training.getTrainingType(),
			                training.getTrainingDate(), training.getTrainingDuration())))
			        .toList());

			Trainee updatedTrainee = updateTraineePort.save(trainee);
			logger.info("Transaction ID: {} - Successfully updated trainers for trainee: {}", transactionId,
			        command.getTraineeUsername());
			return updatedTrainee;
		} catch (BadRequestException | TraineeNotFoundException | TrainerNotFoundException e) {
			logger.warn("Transaction ID: {} - Validation error while updating trainers for trainee: {}, Reason: {}",
			        transactionId, command.getTraineeUsername(), e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Unexpected error while updating trainers for trainee: {}, Reason: {}",
			        transactionId, command.getTraineeUsername(), e.getMessage(), e);
			throw e;
		}
	}

	private void validateTrainers(UpdateTraineeTrainersCommand command, List<Trainer> trainers,
	        List<Training> trainings) {
		if (trainers.isEmpty()) {
			throw new TrainerNotFoundException("No trainers found for usernames: " + command.getTrainerUsernames());
		}

		if (trainers.size() != trainings.size()) {
			List<String> trainerUsernames = trainings.stream()
			        .map(training -> training.getTrainer().getUser().getUsername()).toList();

			List<String> missingUsernames = trainers.stream().map(trainer -> trainer.getUser().getUsername())
			        .filter(username -> !trainerUsernames.contains(username)).toList();

			throw new BadRequestException("The following trainers do not have corresponding trainings: "
			        + String.join(", ", missingUsernames));
		}
	}
}
