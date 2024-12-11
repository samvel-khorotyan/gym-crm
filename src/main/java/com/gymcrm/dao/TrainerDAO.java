package com.gymcrm.dao;

import com.gymcrm.common.ApplicationProperties;
import com.gymcrm.domain.Trainer;
import com.gymcrm.prot.LoadTrainerPort;
import com.gymcrm.prot.UpdateTrainerPort;
import com.gymcrm.util.SerializationUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerDAO extends GenericDAO<Trainer> implements UpdateTrainerPort, LoadTrainerPort {
  private static final Logger logger = LoggerFactory.getLogger(TrainerDAO.class);

  private final ApplicationProperties applicationProperties;

  @Autowired
  public TrainerDAO(Storage storage, ApplicationProperties applicationProperties) {
    super(storage, Trainer.class);
    this.applicationProperties = applicationProperties;
  }

  @Override
  public void saveOrUpdate(Trainer trainer) {
    logger.debug("Attempting to save or update trainer with ID: {}", trainer.getId());
    super.saveOrUpdate(trainer);
    try {
      writeTrainersToFile();
    } catch (IllegalStateException e) {
      logger.warn("Error while saving trainer to file: {}", e.getMessage());
    }
  }

  @Override
  public List<Trainer> fetchAll() {
    logger.debug("Fetching all trainers.");
    return getAllTrainers();
  }

  @Override
  public Trainer fetchById(UUID id) {
    logger.debug("Fetching trainer with ID: {}", id);
    Trainer trainer = (Trainer) super.storage.get(id);
    if (trainer == null) {
      logger.info("Trainer with ID: {} not found.", id);
    }
    return trainer;
  }

  private void writeTrainersToFile() {
    File tempFile = new File(applicationProperties.getTrainerFilePath() + ".tmp");
    File originalFile = new File(applicationProperties.getTrainerFilePath());

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, false))) {
      List<Trainer> trainers = getAllTrainers();

      for (Trainer trainer : trainers) {
        writer.write(SerializationUtil.serializeTrainer(trainer));
        writer.newLine();
      }

      if (originalFile.exists() && !originalFile.delete()) {
        throw new IOException("Failed to delete the original file.");
      }
      if (!tempFile.renameTo(originalFile)) {
        throw new IOException("Failed to rename temp file to original file.");
      }
    } catch (IOException e) {
      logger.error("Error writing trainers to file: {}", e.getMessage());
      throw new IllegalStateException("Error writing trainers to file.", e);
    }
  }

  private List<Trainer> getAllTrainers() {
    return super.storage.values().stream().map(obj -> (Trainer) obj).toList();
  }
}
