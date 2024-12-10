package com.gymcrm.dao;

import com.gymcrm.domain.Trainee;
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
public class TraineeDAO extends GenericDAO<Trainee> implements UpdateTraineePort, LoadTraineePort {
  private static final Logger logger = LoggerFactory.getLogger(TraineeDAO.class);
  private final String traineeFilePath;

  @Autowired
  public TraineeDAO(
      Storage storage, @Value("${storage.data.trainee.file}") String traineeFilePath) {
    super(storage, Trainee.class);
    this.traineeFilePath = traineeFilePath;
  }

  @Override
  public void saveOrUpdate(Trainee trainee) {
    logger.info("Saving or updating trainee with ID: {}", trainee.getId());
    super.saveOrUpdate(trainee);
    writeTraineesToFile();
  }

  @Override
  public void removeById(UUID id) {
    logger.info("Removing trainee with ID: {}", id);
    if (super.storage.remove(id) != null) {
      writeTraineesToFile();
      logger.info("Trainee with ID: {} removed successfully", id);
    } else {
      logger.warn("No trainee found with ID: {}", id);
    }
  }

  private void writeTraineesToFile() {
    File tempFile = new File(traineeFilePath + ".tmp");
    File originalFile = new File(traineeFilePath);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, false))) {
      List<Trainee> trainees = getAllTrainees();

      for (Trainee trainee : trainees) {
        writer.write(SerializationUtil.serializeTrainee(trainee));
        writer.newLine();
      }

      if (originalFile.exists() && !originalFile.delete()) {
        throw new IOException("Failed to delete the original file: " + traineeFilePath);
      }
      if (!tempFile.renameTo(originalFile)) {
        throw new IOException("Failed to rename temp file to original file: " + traineeFilePath);
      }

      logger.info("Trainees written to file successfully.");
    } catch (IOException e) {
      logger.error("Failed to write trainees to file", e);
      throw new IllegalStateException("Error writing trainees to file: " + traineeFilePath, e);
    }
  }

  private List<Trainee> getAllTrainees() {
    return super.storage.values().stream().map(obj -> (Trainee) obj).toList();
  }
}
