package com.gymcrm.dao;

import com.gymcrm.domain.Trainer;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerDAO extends GenericDAO<Trainer> implements UpdateTrainerPort, LoadTrainerPort {
  private static final Logger logger = LoggerFactory.getLogger(TrainerDAO.class);
  private final String trainerFilePath;

  @Autowired
  public TrainerDAO(
      Storage storage, @Value("${storage.data.trainer.file}") String trainerFilePath) {
    super(storage, Trainer.class);
    this.trainerFilePath = trainerFilePath;
  }

  @Override
  public void saveOrUpdate(Trainer trainer) {
    logger.info("Saving or updating trainer with ID: {}", trainer.getId());
    super.saveOrUpdate(trainer);
    writeTrainersToFile();
  }

  @Override
  public List<Trainer> fetchAll() {
    logger.info("Fetching all trainers.");
    return getAllTrainers();
  }

  @Override
  public Trainer fetchById(UUID id) {
    logger.info("Fetching trainer with ID: {}", id);
    Trainer trainer = (Trainer) super.storage.get(id);
    if (trainer == null) {
      logger.warn("Trainer with ID: {} not found.", id);
    }
    return trainer;
  }

  private void writeTrainersToFile() {
    File tempFile = new File(trainerFilePath + ".tmp");
    File originalFile = new File(trainerFilePath);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, false))) {
      List<Trainer> trainers = getAllTrainers();

      for (Trainer trainer : trainers) {
        writer.write(SerializationUtil.serializeTrainer(trainer));
        writer.newLine();
      }

      if (originalFile.exists() && !originalFile.delete()) {
        throw new IOException("Failed to delete the original file: " + trainerFilePath);
      }
      if (!tempFile.renameTo(originalFile)) {
        throw new IOException("Failed to rename temp file to original file: " + trainerFilePath);
      }

      logger.info("Trainers written to file successfully.");
    } catch (IOException e) {
      logger.error("Failed to write trainers to file", e);
      throw new IllegalStateException("Error writing trainers to file: " + trainerFilePath, e);
    }
  }

  private List<Trainer> getAllTrainers() {
    return super.storage.values().stream().map(obj -> (Trainer) obj).toList();
  }
}
