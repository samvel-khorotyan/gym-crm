package com.gymcrm.training.application;

import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainee.application.port.output.LoadTraineePort;
import com.gymcrm.trainee.application.port.output.UpdateTraineePort;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerWorkloadPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.factory.TrainingFactory;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import com.gymcrm.training.application.port.input.UpdateTrainingUseCase;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.application.port.output.LoadTrainingTypePort;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingService implements TrainingCreationUseCase, LoadTrainingUseCase, UpdateTrainingUseCase {
	private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

	private final UpdateTrainingPort updateTrainingPort;
	private final LoadTrainingPort loadTrainingPort;
	private final TrainingFactory trainingFactory;
	private final LoadTraineePort loadTraineePort;
	private final LoadTrainerPort loadTrainerPort;
	private final UpdateTraineePort updateTraineePort;
	private final LoadTrainingTypePort loadTrainingTypePort;
	private final UpdateTrainerWorkloadPort updateTrainerWorkloadPort;

	public TrainingService(UpdateTrainingPort updateTrainingPort, LoadTrainingPort loadTrainingPort,
	        TrainingFactory trainingFactory, LoadTraineePort loadTraineePort, LoadTrainerPort loadTrainerPort,
	        UpdateTraineePort updateTraineePort, LoadTrainingTypePort loadTrainingTypePort,
	        UpdateTrainerWorkloadPort updateTrainerWorkloadPort) {
		this.updateTrainingPort = updateTrainingPort;
		this.loadTrainingPort = loadTrainingPort;
		this.trainingFactory = trainingFactory;
		this.loadTraineePort = loadTraineePort;
		this.loadTrainerPort = loadTrainerPort;
		this.updateTraineePort = updateTraineePort;
		this.loadTrainingTypePort = loadTrainingTypePort;
		this.updateTrainerWorkloadPort = updateTrainerWorkloadPort;
	}

	@Transactional
	@Override
	public void create(CreateTrainingCommand command) {
		String transactionId = MDC.get("transactionId");
		logger.info("Transaction ID: {} - Creating training with name: {}", transactionId, command.getTrainingName());

		try {
			prepareTrainingEntities(command);
			createAndSaveTraining(command, transactionId);
		} catch (TraineeNotFoundException | TrainerNotFoundException e) {
			logger.warn("Transaction ID: {} - Trainee or Trainer not found: {}", transactionId, e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to create training with name: {}, Reason: {}", transactionId,
			        command.getTrainingName(), e.getMessage(), e);
			throw new RuntimeException("Failed to create training", e);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteTraining(UUID trainingId) {
		String transactionId = MDC.get("transactionId");
		logger.info("Transaction ID: {} - Starting transaction to delete training with ID: {}", transactionId,
		        trainingId);

		try {
			Training training = loadTrainingPort.findById(trainingId);
			logTrainingBeforeDeletion(training, transactionId);

			Trainee trainee = training.getTrainee();
			Trainer trainer = training.getTrainer();

			deleteTrainingAndUpdateWorkload(training, trainingId, transactionId);
			updateTraineeTrainerRelationshipAfterDeletion(trainee, trainer, transactionId);

			logger.info("Transaction ID: {} - Successfully completed transaction to delete training with ID: {}",
			        transactionId, trainingId);
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Transaction rolled back for training with ID: {}, Reason: {}",
			        transactionId, trainingId, e.getMessage(), e);
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
		String transactionId = MDC.get("transactionId");

		logger.info(
		        "Transaction ID: {} - Fetching trainings for trainee: {}, Criteria - StartDate: {}, EndDate: {}, TrainerName: {}, TrainingType: {}",
		        transactionId, username, startDate, endDate, trainerName, trainingType);

		try {
			List<Training> trainings = loadTrainingPort.findTraineeTrainingsByCriteria(username, startDate, endDate,
			        trainerName, trainingType);

			logSuccessfulFetch(transactionId, trainings.size(), "trainee", username);
			return trainings;
		} catch (TraineeNotFoundException e) {
			logEntityNotFound(transactionId, "Trainee", e);
			throw e;
		} catch (Exception e) {
			logFetchError(transactionId, "trainee", username, e);
			throw new RuntimeException("Failed to fetch trainings by criteria", e);
		}
	}

	@Override
	public List<Training> findTrainerTrainingsByCriteria(String username, LocalDate startDate, LocalDate endDate,
	        String traineeName) {
		String transactionId = MDC.get("transactionId");

		logger.info(
		        "Transaction ID: {} - Fetching trainings for trainer: {}, Criteria - StartDate: {}, EndDate: {}, TraineeName: {}",
		        transactionId, username, startDate, endDate, traineeName);

		try {
			List<Training> trainings = loadTrainingPort.findTrainerTrainingsByCriteria(username, startDate, endDate,
			        traineeName);

			logSuccessfulFetch(transactionId, trainings.size(), "trainer", username);
			return trainings;
		} catch (TrainerNotFoundException e) {
			logEntityNotFound(transactionId, "Trainer", e);
			throw e;
		} catch (Exception e) {
			logFetchError(transactionId, "trainer", username, e);
			throw new RuntimeException("Failed to fetch trainings by criteria.", e);
		}
	}

	private void prepareTrainingEntities(CreateTrainingCommand command) {
		var trainee = loadTraineePort.findByUsername(command.getTraineeUsername());
		command.setTrainee(trainee);

		var trainer = loadTrainerPort.findByUsername(command.getTrainerUsername());
		command.setTrainer(trainer);

		var trainingType = loadTrainingTypePort.findByTrainingTypeName(command.getTrainingName());
		command.setTrainingType(trainingType);
	}

	private void createAndSaveTraining(CreateTrainingCommand command, String transactionId) {
		Training training = trainingFactory.createFrom(command);

		updateTraineeTrainerRelationship(command.getTrainee(), command.getTrainer());
		updateTrainingPort.save(training);

		// Send trainer workload to secondary service
		updateTrainerWorkloadPort.sendTrainerWorkload(training, "ADD");

		logger.info("Transaction ID: {} - Successfully created training: {}", transactionId, command.getTrainingName());
	}

	private void updateTraineeTrainerRelationship(Trainee trainee, Trainer trainer) {
		if (trainee.getTrainers() == null) {
			trainee.setTrainers(new ArrayList<>());
		}
		trainee.getTrainers().add(trainer);
		updateTraineePort.save(trainee);
	}

	private void logTrainingBeforeDeletion(Training training, String transactionId) {
		logger.info("Transaction ID: {} - Training details before deletion: ID={}, Name={}, Trainee={}, Trainer={}",
		        transactionId, training.getId(), training.getTrainingName(),
		        training.getTrainee().getUser().getUsername(), training.getTrainer().getUser().getUsername());
	}

	private void deleteTrainingAndUpdateWorkload(Training training, UUID trainingId, String transactionId) {
		updateTrainerWorkloadPort.sendTrainerWorkload(training, "DELETE");
		updateTrainingPort.deleteById(trainingId);
		logger.info("Transaction ID: {} - Deleted training with ID: {}", transactionId, trainingId);
	}

	private void updateTraineeTrainerRelationshipAfterDeletion(Trainee trainee, Trainer trainer, String transactionId) {
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

	private void logEntityNotFound(String transactionId, String entityType, Exception e) {
		logger.warn("Transaction ID: {} - {} not found: {}", transactionId, entityType, e.getMessage(), e);
	}

	private void logFetchError(String transactionId, String entityType, String username, Exception e) {
		logger.error("Transaction ID: {} - Failed to fetch trainings for {}: {}, Reason: {}", transactionId, entityType,
		        username, e.getMessage(), e);
	}
}
