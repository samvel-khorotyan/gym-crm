package com.gymcrm.service;

import com.gymcrm.command.CreateTrainerCommand;
import com.gymcrm.command.UpdateTrainerCommand;
import com.gymcrm.dao.LoadTrainerPort;
import com.gymcrm.dao.UpdateTrainerPort;
import com.gymcrm.domain.Trainer;
import com.gymcrm.factory.TrainerFactory;
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

  @Autowired
  public TrainerService(UpdateTrainerPort updateTrainerPort, TrainerFactory trainerFactory) {
    this.updateTrainerPort = updateTrainerPort;
    this.trainerFactory = trainerFactory;
  }

  @Autowired
  public void setLoadTrainerPort(LoadTrainerPort loadTrainerPort) {
    this.loadTrainerPort = loadTrainerPort;
  }

  @Override
  public void createTrainer(CreateTrainerCommand command) {
    if (command.getSpecialization() == null) {
      throw new IllegalArgumentException("Trainer or specialization cannot be null.");
    }

    Trainer trainer = trainerFactory.createFrom(command);

    logger.info("Creating trainer with ID: {}", trainer.getId());
    updateTrainerPort.saveOrUpdate(trainer);
    logger.info("Trainer created successfully: {}", trainer);
  }

  @Override
  public List<Trainer> getAllTrainers() {
    logger.info("Fetching all trainers.");
    List<Trainer> trainers = loadTrainerPort.fetchAll();
    logger.info("Found {} trainers.", trainers.size());
    return trainers;
  }

  @Override
  public Trainer getTrainerById(UUID id) {
    logger.info("Fetching trainer with ID: {}", id);
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null.");
    }
    Trainer trainer = loadTrainerPort.fetchById(id);
    if (trainer == null) {
      throw new IllegalArgumentException("Trainer not found with ID: " + id);
    }
    logger.info("Trainer fetched successfully: {}", trainer);
    return trainer;
  }

  @Override
  public void updateTrainer(UpdateTrainerCommand command) {
    logger.info("Updating trainer with ID: {}", command.getTrainerId());
    if (command.getTrainerId() == null) {
      throw new IllegalArgumentException("ID and Trainer cannot be null.");
    }

    Trainer existingTrainer = loadTrainerPort.fetchById(command.getTrainerId());
    if (existingTrainer == null) {
      throw new IllegalArgumentException("Trainer not found with ID: " + command.getTrainerId());
    }

    existingTrainer.setSpecialization(command.getSpecialization());
    updateTrainerPort.saveOrUpdate(existingTrainer);
    logger.info("Trainer updated successfully: {}", existingTrainer);
  }
}
