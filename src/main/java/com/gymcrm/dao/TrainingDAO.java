package com.gymcrm.dao;

import com.gymcrm.domain.Training;
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
public class TrainingDAO extends GenericDAO<Training>
    implements UpdateTrainingPort, LoadTrainingPort {
  private static final Logger logger = LoggerFactory.getLogger(TrainingDAO.class);
  private final String trainingFilePath;

  @Autowired
  public TrainingDAO(
      Storage storage, @Value("${storage.data.training.file}") String trainingFilePath) {
    super(storage, Training.class);
    this.trainingFilePath = trainingFilePath;
  }

  @Override
  public void saveOrUpdate(Training training) {
    logger.info("Saving or updating training with ID: {}", training.getId());
    super.saveOrUpdate(training);
    writeTrainingsToFile();
  }

  @Override
  public List<Training> fetchAll() {
    logger.info("Fetching all trainings.");
    return getAllTrainings();
  }

  @Override
  public Training fetchById(UUID id) {
    logger.info("Fetching training with ID: {}", id);
    Training training = (Training) super.storage.get(id);
    if (training == null) {
      logger.warn("Training with ID: {} not found.", id);
    }
    return training;
  }

  private void writeTrainingsToFile() {
    File tempFile = new File(trainingFilePath + ".tmp");
    File originalFile = new File(trainingFilePath);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, false))) {
      List<Training> trainings = getAllTrainings();

      for (Training training : trainings) {
        writer.write(SerializationUtil.serializeTraining(training));
        writer.newLine();
      }

      if (originalFile.exists() && !originalFile.delete()) {
        throw new IOException("Failed to delete the original file: " + trainingFilePath);
      }
      if (!tempFile.renameTo(originalFile)) {
        throw new IOException("Failed to rename temp file to original file: " + trainingFilePath);
      }

      logger.info("Trainings written to file successfully.");
    } catch (IOException e) {
      logger.error("Error writing trainings to file", e);
      throw new IllegalStateException("Error writing trainings to file: " + trainingFilePath, e);
    }
  }

  private List<Training> getAllTrainings() {
    return super.storage.values().stream().map(obj -> (Training) obj).toList();
  }
}
