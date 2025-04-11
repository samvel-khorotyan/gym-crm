package com.gymcrm.training.application;

import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainee.application.port.output.LoadTraineePort;
import com.gymcrm.trainee.application.port.output.UpdateTraineePort;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerWorkloadPort;
import com.gymcrm.trainer.domain.ActionType;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.factory.TrainingFactory;
import com.gymcrm.training.application.port.input.*;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.application.port.output.LoadTrainingTypePort;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class TrainingService implements TrainingCreationUseCase, LoadTrainingUseCase, UpdateTrainingUseCase {
	private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

	private final TrainingFactory trainingFactory;
	private final LoadTraineePort loadTraineePort;
	private final LoadTrainerPort loadTrainerPort;
	private final LoadTrainingPort loadTrainingPort;
	private final UpdateTraineePort updateTraineePort;
	private final UpdateTrainingPort updateTrainingPort;
	private final LoadTrainingTypePort loadTrainingTypePort;
	private final UpdateTrainerWorkloadPort updateTrainerWorkloadPort;

	@Override
	@Transactional
	public Training create(CreateTrainingCommand command) {
		String transactionId = getTransactionId();
		logOperationStart("create", command.getTrainingName());

		try {
			prepareTrainingEntities(command);
			return createAndSaveTraining(command, transactionId);
		} catch (TraineeNotFoundException | TrainerNotFoundException e) {
			logEntityNotFound(transactionId, e);
			throw e;
		} catch (Exception e) {
			logOperationError("create", command.getTrainingName(), e);
			throw new RuntimeException("Failed to create training", e);
		}
	}

	@Override
	public Training findById(UUID id) {
		String transactionId = getTransactionId();
		logOperationStart("find", id.toString());

		try {
			Training training = loadTrainingPort.findById(id);
			logger.info("Transaction ID: {} - Successfully retrieved training with ID: {}", transactionId, id);
			return training;
		} catch (Exception e) {
			logOperationError("find", id.toString(), e);
			throw new RuntimeException("Failed to get training", e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Training update(UUID trainingId, UpdateTrainingCommand command) {
		String transactionId = getTransactionId();
		logOperationStart("update", trainingId.toString());

		try {
			Training existingTraining = loadTrainingPort.findById(trainingId);
			notifyWorkloadChangeBeforeUpdate(existingTraining);
			updateTrainingDetails(existingTraining, command);
			saveAndNotifyAfterUpdate(existingTraining);

			logger.info("Transaction ID: {} - Successfully updated training with ID: {}", transactionId, trainingId);
			return existingTraining;
		} catch (Exception e) {
			logOperationError("update", trainingId.toString(), e);
			throw new RuntimeException("Failed to update training", e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(UUID trainingId) {
		String transactionId = getTransactionId();
		logOperationStart("delete", trainingId.toString());

		try {
			Training training = loadTrainingPort.findById(trainingId);
			logTrainingBeforeDeletion(training);

			Trainee trainee = training.getTrainee();
			Trainer trainer = training.getTrainer();

			deleteTrainingAndUpdateWorkload(training, trainingId);
			updateTraineeTrainerRelationshipAfterDeletion(trainee, trainer);

			logger.info("Transaction ID: {} - Successfully completed deletion of training with ID: {}", transactionId,
			        trainingId);
		} catch (Exception e) {
			logOperationError("delete", trainingId.toString(), e);
			throw new RuntimeException("Failed to delete training: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Training> loadAll() {
		logger.debug("Fetching all trainings.");
		try {
			return loadTrainingPort.findAll();
		} catch (Exception e) {
			logger.error("Error fetching all trainings, Reason: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to fetch trainings", e);
		}
	}

	@Override
	public List<Training> findTraineeTrainingsByCriteria(String username, LocalDate startDate, LocalDate endDate,
	        String trainerName, String trainingType) {
		String transactionId = getTransactionId();

		logger.info(
		        "Transaction ID: {} - Fetching trainings for trainee: {}, Criteria - StartDate: {}, EndDate: {}, TrainerName: {}, TrainingType: {}",
		        transactionId, username, startDate, endDate, trainerName, trainingType);

		try {
			List<Training> trainings = loadTrainingPort.findTraineeTrainingsByCriteria(username, startDate, endDate,
			        trainerName, trainingType);

			logSuccessfulFetch(transactionId, trainings.size(), "trainee", username);
			return trainings;
		} catch (TraineeNotFoundException e) {
			logEntityNotFound(transactionId, e);
			throw e;
		} catch (Exception e) {
			logFetchError(transactionId, "trainee", username, e);
			throw new RuntimeException("Failed to fetch trainings by criteria", e);
		}
	}

	@Override
	public List<Training> findTrainerTrainingsByCriteria(String username, LocalDate startDate, LocalDate endDate,
	        String traineeName) {
		String transactionId = getTransactionId();

		logger.info(
		        "Transaction ID: {} - Fetching trainings for trainer: {}, Criteria - StartDate: {}, EndDate: {}, TraineeName: {}",
		        transactionId, username, startDate, endDate, traineeName);

		try {
			List<Training> trainings = loadTrainingPort.findTrainerTrainingsByCriteria(username, startDate, endDate,
			        traineeName);

			logSuccessfulFetch(transactionId, trainings.size(), "trainer", username);
			return trainings;
		} catch (TrainerNotFoundException e) {
			logEntityNotFound(transactionId, e);
			throw e;
		} catch (Exception e) {
			logFetchError(transactionId, "trainer", username, e);
			throw new RuntimeException("Failed to fetch trainings by criteria.", e);
		}
	}

	private String getTransactionId() {
		return MDC.get("transactionId");
	}

	private void logOperationStart(String operation, String entityId) {
		String transactionId = getTransactionId();
		logger.info("Transaction ID: {} - Starting {} operation for entity: {}", transactionId, operation, entityId);
	}

	private void logOperationError(String operation, String entityId, Exception e) {
		String transactionId = getTransactionId();
		logger.error("Transaction ID: {} - Failed to {} entity: {}, Reason: {}", transactionId, operation, entityId,
		        e.getMessage(), e);
	}

	private void prepareTrainingEntities(CreateTrainingCommand command) {
		Assert.notNull(command, "Command cannot be null");
		Assert.hasText(command.getTraineeUsername(), "Trainee username cannot be empty");
		Assert.hasText(command.getTrainerUsername(), "Trainer username cannot be empty");
		Assert.hasText(command.getTrainingName(), "Training name cannot be empty");

		var trainee = loadTraineePort.findByUsername(command.getTraineeUsername());
		command.setTrainee(trainee);

		var trainer = loadTrainerPort.findByUsername(command.getTrainerUsername());
		command.setTrainer(trainer);

		var trainingType = loadTrainingTypePort.findByTrainingTypeName(command.getTrainingName());
		command.setTrainingType(trainingType);
	}

	private Training createAndSaveTraining(CreateTrainingCommand command, String transactionId) {
		Training training = trainingFactory.createFrom(command);

		updateTraineeTrainerRelationship(command.getTrainee(), command.getTrainer());
		updateTrainingPort.save(training);

		updateTrainerWorkloadPort.sendTrainerWorkload(training, ActionType.ADD);

		logger.info("Transaction ID: {} - Successfully created training: {}", transactionId, command.getTrainingName());

		return training;
	}

	private void notifyWorkloadChangeBeforeUpdate(Training training) {
		updateTrainerWorkloadPort.sendTrainerWorkload(training, ActionType.DELETE);
	}

	private void updateTrainingDetails(Training existingTraining, UpdateTrainingCommand command) {
		Assert.notNull(command, "Update command cannot be null");

		updateTraineeIfNeeded(existingTraining, command);
		updateTrainerIfNeeded(existingTraining, command);
		updateTrainingTypeIfNeeded(existingTraining, command);
		updateTrainingPropertiesIfNeeded(existingTraining, command);

		// Update trainee-trainer relationship if needed
		updateTraineeTrainerRelationshipIfNeeded(existingTraining);
	}

	private void updateTraineeIfNeeded(Training existingTraining, UpdateTrainingCommand command) {
		if (command.getTraineeUsername() != null) {
			Trainee trainee = loadTraineePort.findByUsername(command.getTraineeUsername());
			existingTraining.setTrainee(trainee);
			command.setTrainee(trainee);
		}
	}

	private void updateTrainerIfNeeded(Training existingTraining, UpdateTrainingCommand command) {
		if (command.getTrainerUsername() != null) {
			Trainer trainer = loadTrainerPort.findByUsername(command.getTrainerUsername());
			existingTraining.setTrainer(trainer);
			command.setTrainer(trainer);
		}
	}

	private void updateTrainingTypeIfNeeded(Training existingTraining, UpdateTrainingCommand command) {
		if (command.getTrainingName() != null) {
			TrainingType trainingType = loadTrainingTypePort.findByTrainingTypeName(command.getTrainingName());
			existingTraining.setTrainingType(trainingType);
			existingTraining.setTrainingName(command.getTrainingName());
			command.setTrainingType(trainingType);
		}
	}

	private void updateTrainingPropertiesIfNeeded(Training existingTraining, UpdateTrainingCommand command) {
		if (command.getTrainingDate() != null) {
			existingTraining.setTrainingDate(command.getTrainingDate());
		}

		if (command.getTrainingDuration() != null) {
			existingTraining.setTrainingDuration(command.getTrainingDuration());
		}
	}

	private void saveAndNotifyAfterUpdate(Training training) {
		updateTrainingPort.save(training);
		updateTrainerWorkloadPort.sendTrainerWorkload(training, ActionType.UPDATE);
	}

	private void updateTraineeTrainerRelationshipIfNeeded(Training training) {
		Trainee trainee = training.getTrainee();
		Trainer trainer = training.getTrainer();

		// Check if trainee already has this trainer
		boolean trainerExists = trainee.getTrainers() != null
		        && trainee.getTrainers().stream().anyMatch(t -> t.getId().equals(trainer.getId()));

		if (!trainerExists) {
			ensureTraineeHasTrainersList(trainee);
			trainee.getTrainers().add(trainer);
			updateTraineePort.save(trainee);
		}
	}

	private void updateTraineeTrainerRelationship(Trainee trainee, Trainer trainer) {
		ensureTraineeHasTrainersList(trainee);
		trainee.getTrainers().add(trainer);
		updateTraineePort.save(trainee);
	}

	private void ensureTraineeHasTrainersList(Trainee trainee) {
		if (trainee.getTrainers() == null) {
			trainee.setTrainers(new ArrayList<>());
		}
	}

	private void logTrainingBeforeDeletion(Training training) {
		String transactionId = getTransactionId();
		logger.info("Transaction ID: {} - Training details before deletion: ID={}, Name={}, Trainee={}, Trainer={}",
		        transactionId, training.getId(), training.getTrainingName(),
		        training.getTrainee().getUser().getUsername(), training.getTrainer().getUser().getUsername());
	}

	private void deleteTrainingAndUpdateWorkload(Training training, UUID trainingId) {
		String transactionId = getTransactionId();
		updateTrainerWorkloadPort.sendTrainerWorkload(training, ActionType.DELETE);
		updateTrainingPort.deleteById(trainingId);
		logger.info("Transaction ID: {} - Deleted training with ID: {}", transactionId, trainingId);
	}

	private void updateTraineeTrainerRelationshipAfterDeletion(Trainee trainee, Trainer trainer) {
		String transactionId = getTransactionId();
		// Check if there are other trainings between this trainee and trainer
		boolean hasOtherTrainings = loadTrainingPort.existsByTraineeAndTrainer(trainee.getId(), trainer.getId());

		// If no other trainings exist, remove the relationship
		if (!hasOtherTrainings && trainee.getTrainers() != null) {
			trainee.getTrainers().remove(trainer);
			updateTraineePort.save(trainee);

			logger.info(
			        "Transaction ID: {} - Removed trainer-trainee relationship as no more trainings exist between them",
			        transactionId);
		}
	}

	private void logSuccessfulFetch(String transactionId, int count, String entityType, String username) {
		logger.info("Transaction ID: {} - Successfully fetched {} trainings for {}: {}", transactionId, count,
		        entityType, username);
	}

	private void logEntityNotFound(String transactionId, Exception e) {
		logger.warn("Transaction ID: {} - Entity not found: {}", transactionId, e.getMessage(), e);
	}

	private void logFetchError(String transactionId, String entityType, String username, Exception e) {
		logger.error("Transaction ID: {} - Failed to fetch trainings for {}: {}, Reason: {}", transactionId, entityType,
		        username, e.getMessage(), e);
	}
}
