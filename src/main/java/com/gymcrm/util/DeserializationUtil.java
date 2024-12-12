package com.gymcrm.util;

import com.gymcrm.domain.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class DeserializationUtil {
  private static final int TRAINEE_FIELD_COUNT = 9;
  private static final int TRAINER_FIELD_COUNT = 8;
  private static final int TRAINING_FIELD_COUNT = 7;

  private DeserializationUtil() {
    // Utility class; prevent instantiation
  }

  public static Trainee deserializeTrainee(String line) {
    String[] fields = splitAndValidate(line, TRAINEE_FIELD_COUNT, "Trainee");
    try {
      Trainee trainee = new Trainee();
      trainee.setId(UUID.fromString(fields[0]));
      trainee.setDateOfBirth(LocalDate.parse(fields[1]));
      trainee.setAddress(fields[2]);
      trainee.setUserId(UUID.fromString(fields[3]));
      trainee.setFirstName(fields[4]);
      trainee.setLastName(fields[5]);
      trainee.setUsername(fields[6]);
      trainee.setPassword(fields[7]);
      trainee.setIsActive(Boolean.parseBoolean(fields[8]));
      return trainee;
    } catch (IllegalArgumentException | DateTimeParseException e) {
      throw new IllegalArgumentException("Failed to deserialize Trainee: " + line, e);
    }
  }

  public static Trainer deserializeTrainer(String line) {
    String[] fields = splitAndValidate(line, TRAINER_FIELD_COUNT, "Trainer");
    try {
      Trainer trainer = new Trainer();
      trainer.setId(UUID.fromString(fields[0]));
      trainer.setSpecialization(fields[1]);
      trainer.setUserId(UUID.fromString(fields[2]));
      trainer.setFirstName(fields[3]);
      trainer.setLastName(fields[4]);
      trainer.setUsername(fields[5]);
      trainer.setPassword(fields[6]);
      trainer.setIsActive(Boolean.parseBoolean(fields[7]));
      return trainer;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to deserialize Trainer: " + line, e);
    }
  }

  public static Training deserializeTraining(String line) {
    String[] fields = splitAndValidate(line, TRAINING_FIELD_COUNT, "Training");
    try {
      Training training = new Training();
      training.setId(UUID.fromString(fields[0]));
      training.setTraineeId(UUID.fromString(fields[1]));
      training.setTrainerId(UUID.fromString(fields[2]));
      training.setTrainingName(fields[3]);
      training.setTrainingType(TrainingType.valueOf(fields[4]));
      training.setTrainingDate(LocalDate.parse(fields[5]));
      training.setTrainingDuration(Integer.parseInt(fields[6]));
      return training;
    } catch (IllegalArgumentException | DateTimeParseException e) {
      throw new IllegalArgumentException("Failed to deserialize Training: " + line, e);
    }
  }

  private static String[] splitAndValidate(String line, int expectedFieldCount, String entityName) {
    if (line == null || line.isBlank()) {
      throw new IllegalArgumentException(entityName + " data is null or empty");
    }

    String[] fields = line.split(",");
    if (fields.length < expectedFieldCount) {
      throw new IllegalArgumentException(entityName + " data has insufficient fields: " + line);
    }

    return fields;
  }
}