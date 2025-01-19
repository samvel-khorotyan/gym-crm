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
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainerService implements TrainerCreationUseCase, TrainerUpdateUseCase, LoadTrainerUseCase {
	private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

	private final UpdateTrainerPort updateTrainerPort;
	private LoadTrainerPort loadTrainerPort;
	private final TrainerFactory trainerFactory;
	private final UserCreationUseCase userCreationUseCase;
	private final UpdateUserPort updateUserPort;
	private final UserUpdateMapper userUpdateMapper;
	private final TrainerUpdateMapper trainerUpdateMapper;

	@Autowired
	public TrainerService(UpdateTrainerPort updateTrainerPort, TrainerFactory trainerFactory,
	        UserCreationUseCase userCreationUseCase, UpdateUserPort updateUserPort, UserUpdateMapper userUpdateMapper,
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
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Creating trainer with name: {} {}", transactionId, command.getFirstName(),
		        command.getLastName());

		try {
			command.setUser(userCreationUseCase
			        .create(new CreateUserCommand(command.getFirstName(), command.getLastName(), UserType.TRAINER)));

			Trainer trainer = trainerFactory.createFrom(command);
			Trainer savedTrainer = updateTrainerPort.save(trainer);

			logger.info("Transaction ID: {} - Successfully created trainer with username: {}", transactionId,
			        savedTrainer.getUser().getUsername());
			return savedTrainer;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to create trainer: {}", transactionId, e.getMessage(), e);
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
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Fetching active trainers not assigned to trainee: {}", transactionId,
		        traineeUsername);

		try {
			List<Trainer> trainers = loadTrainerPort.findActiveTrainersNotAssignedToTrainee(traineeUsername);

			logger.info("Transaction ID: {} - Successfully fetched {} trainers not assigned to trainee: {}",
			        transactionId, trainers.size(), traineeUsername);
			return trainers;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to fetch trainers not assigned to trainee: {}, Reason: {}",
			        transactionId, traineeUsername, e.getMessage(), e);
			throw new RuntimeException("Failed to fetch trainers not assigned to trainee.", e);
		}
	}

	@Override
	public Trainer loadByUsername(String username) {
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Fetching trainer details by username: {}", transactionId, username);

		try {
			Trainer trainer = loadTrainerPort.findByUsernameWithTrainees(username);
			logger.info("Transaction ID: {} - Successfully fetched trainer details for username: {}", transactionId,
			        username);
			return trainer;
		} catch (TrainerNotFoundException e) {
			logger.warn("Transaction ID: {} - Trainer not found: {}", transactionId, username);
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to fetch trainer details for username: {}, Reason: {}",
			        transactionId, username, e.getMessage(), e);
			throw new RuntimeException("Failed to fetch trainer details.", e);
		}
	}

	@Transactional
	@Override
	public Trainer update(UpdateTrainerCommand command) {
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Updating trainer with ID: {}", transactionId, command.getTrainerId());

		try {
			Trainer existingTrainer = loadTrainerPort.findByIdWithTrainees(command.getTrainerId());

			userUpdateMapper.updateUserFromCommand(
			        new UpdateUserCommand(command.getFirstName(), command.getLastName(), command.getIsActive()),
			        existingTrainer.getUser());

			trainerUpdateMapper.updateTrainerFromCommand(command, existingTrainer);

			Trainer updatedTrainer = updateTrainerPort.save(existingTrainer);

			logger.info("Transaction ID: {} - Successfully updated trainer with ID: {}", transactionId,
			        command.getTrainerId());
			return updatedTrainer;
		} catch (TrainerNotFoundException e) {
			logger.warn("Transaction ID: {} - Trainer not found with ID: {}", transactionId, command.getTrainerId());
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to update trainer with ID: {}, Reason: {}", transactionId,
			        command.getTrainerId(), e.getMessage(), e);
			throw new RuntimeException("Failed to update trainer.", e);
		}
	}

	@Override
	public void activateDeactivate(ActivateDeactivateTrainerCommand command) {
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Updating state for trainer: {}", transactionId, command.getUsername());

		try {
			User user = loadTrainerPort.findByUsername(command.getUsername()).getUser();

			user.setIsActive(command.getIsActive());
			updateUserPort.save(user);

			logger.info("Transaction ID: {} - Successfully updated state for trainer: {}", transactionId,
			        command.getUsername());
		} catch (TrainerNotFoundException e) {
			logger.warn("Transaction ID: {} - Trainer not found with username: {}", transactionId,
			        command.getUsername());
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format("Transaction ID: %s - Failed to activate/deactivate trainer. Cause: %s",
			        transactionId, e.getMessage());
			logger.error(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		}
	}
}
