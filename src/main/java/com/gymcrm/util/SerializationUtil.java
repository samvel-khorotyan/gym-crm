package com.gymcrm.util;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import java.util.Objects;

public class SerializationUtil {
  public static String serializeTrainee(Trainee trainee) {
    validateNotNull(trainee, "Trainee");
    validateNotNull(trainee.getId(), "Trainee ID");
    validateNotNull(trainee.getDateOfBirth(), "Trainee Date of Birth");
    validateNotNull(trainee.getAddress(), "Trainee Address");
    validateNotNull(trainee.getUserId(), "Trainee User ID");
    validateNotNull(trainee.getFirstName(), "Trainee User Firs tName");
    validateNotNull(trainee.getLastName(), "Trainee User Last Name");
    validateNotNull(trainee.getUsername(), "Trainee User Username");
    validateNotNull(trainee.getPassword(), "Trainee User Password");
    validateNotNull(trainee.getIsActive(), "Trainee User Is Active");

    return joinFields(
        trainee.getId().toString(),
        trainee.getDateOfBirth().toString(),
        trainee.getAddress(),
        trainee.getUserId().toString(),
        trainee.getFirstName(),
        trainee.getLastName(),
        trainee.getUsername(),
        trainee.getPassword(),
        trainee.getIsActive().toString());
  }

  public static String serializeTrainer(Trainer trainer) {
    validateNotNull(trainer, "Trainer");
    validateNotNull(trainer.getId(), "Trainer ID");
    validateNotNull(trainer.getSpecialization(), "Trainer Specialization");
    validateNotNull(trainer.getUserId(), "Trainer User ID");
    validateNotNull(trainer.getFirstName(), "Trainer User Firs tName");
    validateNotNull(trainer.getLastName(), "Trainer User Last Name");
    validateNotNull(trainer.getUsername(), "Trainer User Username");
    validateNotNull(trainer.getPassword(), "Trainer User Password");
    validateNotNull(trainer.getIsActive(), "Trainer User Is Active");

    return joinFields(
        trainer.getId().toString(),
        trainer.getSpecialization(),
        trainer.getUserId().toString(),
        trainer.getFirstName(),
        trainer.getLastName(),
        trainer.getUsername(),
        trainer.getPassword(),
        trainer.getIsActive().toString());
  }

  public static String serializeTraining(Training training) {
    validateNotNull(training, "Training");
    validateNotNull(training.getId(), "Training ID");
    validateNotNull(training.getTraineeId(), "Training Trainee ID");
    validateNotNull(training.getTrainerId(), "Training Trainer ID");
    validateNotNull(training.getTrainingName(), "Training Name");
    validateNotNull(training.getTrainingType(), "Training Type");
    validateNotNull(training.getTrainingDate(), "Training Date");
    validateNotNull(training.getTrainingDuration(), "Training Duration");

    return joinFields(
        training.getId().toString(),
        training.getTraineeId().toString(),
        training.getTrainerId().toString(),
        training.getTrainingName(),
        training.getTrainingType().toString(),
        training.getTrainingDate().toString(),
        String.valueOf(training.getTrainingDuration()));
  }

  private static void validateNotNull(Object object, String fieldName) {
    if (Objects.isNull(object)) {
      throw new IllegalArgumentException(fieldName + " cannot be null.");
    }
  }

  private static String joinFields(String... fields) {
    return String.join(",", fields);
  }
}
