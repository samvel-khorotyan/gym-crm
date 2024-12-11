package com.gymcrm.unit.util;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.domain.*;
import com.gymcrm.util.SerializationUtil;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SerializationUtilTest {
  private Trainee trainee;
  private Trainer trainer;
  private Training training;

  @BeforeEach
  void setUp() {
    trainee = new Trainee();
    trainee.setId(UUID.randomUUID());
    trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
    trainee.setAddress("123 Street");
    trainee.setUserId(UUID.randomUUID());
    trainee.setFirstName("John");
    trainee.setLastName("Doe");
    trainee.setUsername("johndoe");
    trainee.setPassword("password");
    trainee.setIsActive(true);

    trainer = new Trainer();
    trainer.setId(UUID.randomUUID());
    trainer.setSpecialization("Fitness");
    trainer.setUserId(UUID.randomUUID());
    trainer.setFirstName("Jane");
    trainer.setLastName("Smith");
    trainer.setUsername("janesmith");
    trainer.setPassword("password123");
    trainer.setIsActive(false);

    training = new Training();
    training.setId(UUID.randomUUID());
    training.setTraineeId(UUID.randomUUID());
    training.setTrainerId(UUID.randomUUID());
    training.setTrainingName("Strength Training");
    training.setTrainingType(TrainingType.WRESTLING);
    training.setTrainingDate(LocalDate.of(2023, 1, 1));
    training.setTrainingDuration(90);
  }

  @Test
  void serializeTrainee_shouldReturnSerializedString() {
    String result = SerializationUtil.serializeTrainee(trainee);

    String expected =
        String.join(
            ",",
            trainee.getId().toString(),
            trainee.getDateOfBirth().toString(),
            trainee.getAddress(),
            trainee.getUserId().toString(),
            trainee.getFirstName(),
            trainee.getLastName(),
            trainee.getUsername(),
            trainee.getPassword(),
            String.valueOf(trainee.getIsActive()));
    assertEquals(expected, result);
  }

  @Test
  void serializeTrainee_shouldThrowExceptionForNullField() {
    trainee.setAddress(null);
    assertThrows(IllegalArgumentException.class, () -> SerializationUtil.serializeTrainee(trainee));
  }

  @Test
  void serializeTrainer_shouldReturnSerializedString() {
    String result = SerializationUtil.serializeTrainer(trainer);

    String expected =
        String.join(
            ",",
            trainer.getId().toString(),
            trainer.getSpecialization(),
            trainer.getUserId().toString(),
            trainer.getFirstName(),
            trainer.getLastName(),
            trainer.getUsername(),
            trainer.getPassword(),
            String.valueOf(trainer.getIsActive()));
    assertEquals(expected, result);
  }

  @Test
  void serializeTrainer_shouldThrowExceptionForNullField() {
    trainer.setSpecialization(null);
    assertThrows(IllegalArgumentException.class, () -> SerializationUtil.serializeTrainer(trainer));
  }

  @Test
  void serializeTraining_shouldReturnSerializedString() {
    String result = SerializationUtil.serializeTraining(training);

    String expected =
        String.join(
            ",",
            training.getId().toString(),
            training.getTraineeId().toString(),
            training.getTrainerId().toString(),
            training.getTrainingName(),
            training.getTrainingType().toString(),
            training.getTrainingDate().toString(),
            String.valueOf(training.getTrainingDuration()));
    assertEquals(expected, result);
  }

  @Test
  void serializeTraining_shouldThrowExceptionForNullField() {
    training.setTrainingName(null);
    assertThrows(
        IllegalArgumentException.class, () -> SerializationUtil.serializeTraining(training));
  }
}
