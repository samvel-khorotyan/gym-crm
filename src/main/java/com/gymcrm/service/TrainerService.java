package com.gymcrm.service;

import com.gymcrm.dao.LoadTrainerPort;
import com.gymcrm.dao.UpdateTrainerPort;
import com.gymcrm.domain.Trainer;
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

  @Autowired
  public TrainerService(UpdateTrainerPort updateTrainerPort) {
    this.updateTrainerPort = updateTrainerPort;
  }

  @Autowired
  public void setLoadTrainerPort(LoadTrainerPort loadTrainerPort) {
    this.loadTrainerPort = loadTrainerPort;
  }

  @Override
  public void createTrainer(Trainer trainer) {
    logger.info("Creating trainer with ID: {}", trainer.getId());
    if (trainer.getSpecialization() == null) {
      throw new IllegalArgumentException("Trainer or specialization cannot be null.");
    }
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
  public void updateTrainer(UUID id, Trainer trainer) {
    logger.info("Updating trainer with ID: {}", id);
    if (id == null || trainer == null) {
      throw new IllegalArgumentException("ID and Trainer cannot be null.");
    }

    Trainer existingTrainer = loadTrainerPort.fetchById(id);
    if (existingTrainer == null) {
      throw new IllegalArgumentException("Trainer not found with ID: " + id);
    }

    existingTrainer.setSpecialization(trainer.getSpecialization());
    updateTrainerPort.saveOrUpdate(existingTrainer);
    logger.info("Trainer updated successfully: {}", existingTrainer);
  }
}
