package com.gymcrm.dao;

import com.gymcrm.common.ApplicationProperties;
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
import org.springframework.stereotype.Repository;

@Repository
public class TraineeDAO extends GenericDAO<Trainee> implements UpdateTraineePort, LoadTraineePort {
  private static final Logger logger = LoggerFactory.getLogger(TraineeDAO.class);

  private final ApplicationProperties applicationProperties;

  @Autowired
  public TraineeDAO(Storage storage, ApplicationProperties applicationProperties) {
    super(storage, Trainee.class);
    this.applicationProperties = applicationProperties;
  }

  @Override
  public void saveOrUpdate(Trainee trainee) {
    logger.debug("Attempting to save or update trainee with ID: {}", trainee.getId());
    super.saveOrUpdate(trainee);
    try {
      writeTraineesToFile();
    } catch (IllegalStateException e) {
      logger.warn("Error while saving trainee to file: {}", e.getMessage());
    }
  }

  @Override
  public void removeById(UUID id) {
    logger.debug("Attempting to remove trainee with ID: {}", id);
    if (super.storage.remove(id) != null) {
      try {
        writeTraineesToFile();
        logger.info("Trainee with ID: {} removed successfully", id);
      } catch (IllegalStateException e) {
        logger.warn("Error while removing trainee: {}", e.getMessage());
      }
    } else {
      logger.info("No trainee found with ID: {}", id);
    }
  }

  private void writeTraineesToFile() {
    File tempFile = new File(applicationProperties.getTraineeFilePath() + ".tmp");
    File originalFile = new File(applicationProperties.getTraineeFilePath());

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, false))) {
      List<Trainee> trainees = getAllTrainees();

      for (Trainee trainee : trainees) {
        writer.write(SerializationUtil.serializeTrainee(trainee));
        writer.newLine();
      }

      if (originalFile.exists() && !originalFile.delete()) {
        throw new IOException("Failed to delete the original file.");
      }
      if (!tempFile.renameTo(originalFile)) {
        throw new IOException("Failed to rename temp file to original file.");
      }
    } catch (IOException e) {
      logger.error("Error writing trainees to file: {}", e.getMessage());
      throw new IllegalStateException("Error writing trainees to file.", e);
    }
  }

  private List<Trainee> getAllTrainees() {
    return super.storage.values().stream().map(obj -> (Trainee) obj).toList();
  }
}
