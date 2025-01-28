package com.gymcrm.training.application;

import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainee.application.port.output.LoadTraineePort;
import com.gymcrm.trainee.application.port.output.UpdateTraineePort;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.training.application.factory.TrainingFactory;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.application.port.input.LoadTrainingUseCase;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.application.port.output.LoadTrainingTypePort;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingService implements TrainingCreationUseCase, LoadTrainingUseCase {
	private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

	private final UpdateTrainingPort updateTrainingPort;
	private final LoadTrainingPort loadTrainingPort;
	private final TrainingFactory trainingFactory;
	private final LoadTraineePort loadTraineePort;
	private final LoadTrainerPort loadTrainerPort;
	private final UpdateTraineePort updateTraineePort;
	private final LoadTrainingTypePort loadTrainingTypePort;

	@Autowired
	public TrainingService(UpdateTrainingPort updateTrainingPort, LoadTrainingPort loadTrainingPort,
	        TrainingFactory trainingFactory, LoadTraineePort loadTraineePort, LoadTrainerPort loadTrainerPort,
	        UpdateTraineePort updateTraineePort, LoadTrainingTypePort loadTrainingTypePort) {
		this.updateTrainingPort = updateTrainingPort;
		this.loadTrainingPort = loadTrainingPort;
		this.trainingFactory = trainingFactory;
		this.loadTraineePort = loadTraineePort;
		this.loadTrainerPort = loadTrainerPort;
		this.updateTraineePort = updateTraineePort;
		this.loadTrainingTypePort = loadTrainingTypePort;
	}

	@Transactional
	@Override
	public void create(CreateTrainingCommand command) {
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Creating training with name: {}", transactionId, command.getTrainingName());

		try {
			var trainee = loadTraineePort.findByUsername(command.getTraineeUsername());
			command.setTrainee(trainee);

			var trainer = loadTrainerPort.findByUsername(command.getTrainerUsername());
			command.setTrainer(trainer);

			var trainingType = loadTrainingTypePort.findByTrainingTypeName(command.getTrainingName());
			command.setTrainingType(trainingType);

			Training training = trainingFactory.createFrom(command);

			if (trainee.getTrainers() == null) {
				trainee.setTrainers(new ArrayList<>());
			}
			trainee.getTrainers().add(trainer);

			updateTraineePort.save(trainee);
			updateTrainingPort.save(training);

			logger.info("Transaction ID: {} - Successfully created training: {}", transactionId,
			        command.getTrainingName());
		} catch (TraineeNotFoundException | TrainerNotFoundException e) {
			logger.warn("Transaction ID: {} - Trainee or Trainer not found: {}", transactionId, e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to create training with name: {}, Reason: {}", transactionId,
			        command.getTrainingName(), e.getMessage(), e);
			throw new RuntimeException("Failed to create training", e);
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

			logger.info("Transaction ID: {} - Successfully fetched {} trainings for trainee: {}", transactionId,
			        trainings.size(), username);
			return trainings;
		} catch (TraineeNotFoundException e) {
			logger.warn("Transaction ID: {} - Trainee not found: {}", transactionId, e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to fetch trainings for trainee: {}, Reason: {}", transactionId,
			        username, e.getMessage(), e);
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

			logger.info("Transaction ID: {} - Successfully fetched {} trainings for trainer: {}", transactionId,
			        trainings.size(), username);
			return trainings;
		} catch (TrainerNotFoundException e) {
			logger.warn("Transaction ID: {} - Trainer not found: {}", transactionId, e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to fetch trainings for trainer: {}, Reason: {}", transactionId,
			        username, e.getMessage(), e);
			throw new RuntimeException("Failed to fetch trainings by criteria.", e);
		}
	}
}
