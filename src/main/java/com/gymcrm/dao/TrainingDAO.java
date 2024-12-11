package com.gymcrm.dao;

import com.gymcrm.common.ApplicationProperties;
import com.gymcrm.domain.Training;
import com.gymcrm.prot.LoadTrainingPort;
import com.gymcrm.prot.UpdateTrainingPort;
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
public class TrainingDAO extends GenericDAO<Training>
    implements UpdateTrainingPort, LoadTrainingPort {
  private static final Logger logger = LoggerFactory.getLogger(TrainingDAO.class);

  private final ApplicationProperties applicationProperties;

  @Autowired
  public TrainingDAO(Storage storage, ApplicationProperties applicationProperties) {
    super(storage, Training.class);
    this.applicationProperties = applicationProperties;
  }

  @Override
  public void saveOrUpdate(Training training) {
    logger.debug("Attempting to save or update training with ID: {}", training.getId());
    super.saveOrUpdate(training);
    try {
      writeTrainingsToFile();
    } catch (IllegalStateException e) {
      logger.warn("Error while saving training to file: {}", e.getMessage());
    }
  }

  @Override
  public List<Training> fetchAll() {
    logger.debug("Fetching all trainings.");
    return getAllTrainings();
  }

  @Override
  public Training fetchById(UUID id) {
    logger.debug("Fetching training with ID: {}", id);
    Training training = (Training) super.storage.get(id);
    if (training == null) {
      logger.info("Training with ID: {} not found.", id);
    }
    return training;
  }

  private void writeTrainingsToFile() {
    File tempFile = new File(applicationProperties.getTrainingFilePath() + ".tmp");
    File originalFile = new File(applicationProperties.getTrainingFilePath());

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, false))) {
      List<Training> trainings = getAllTrainings();

      for (Training training : trainings) {
        writer.write(SerializationUtil.serializeTraining(training));
        writer.newLine();
      }

      if (originalFile.exists() && !originalFile.delete()) {
        throw new IOException("Failed to delete the original file.");
      }
      if (!tempFile.renameTo(originalFile)) {
        throw new IOException("Failed to rename temp file to original file.");
      }
    } catch (IOException e) {
      logger.error("Error writing trainings to file: {}", e.getMessage());
      throw new IllegalStateException("Error writing trainings to file.", e);
    }
  }

  private List<Training> getAllTrainings() {
    return super.storage.values().stream().map(obj -> (Training) obj).toList();
  }
}
