package com.gymcrm.unit.util;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.domain.*;
import com.gymcrm.util.DeserializationUtil;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeserializationUtilTest {
  @Test
  void deserializeTrainee_shouldReturnTrainee() {
    String line =
        UUID.randomUUID()
            + ",1990-01-01,123 Street,"
            + UUID.randomUUID()
            + ",John,Doe,johndoe,password,true";

    Trainee result = DeserializationUtil.deserializeTrainee(line);

    assertNotNull(result);
    assertEquals("123 Street", result.getAddress());
    assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth());
    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
    assertEquals("johndoe", result.getUsername());
    assertEquals("password", result.getPassword());
    assertTrue(result.getIsActive());
  }

  @Test
  void deserializeTrainee_shouldThrowExceptionForInvalidUUID() {
    String line =
        "invalid-uuid,1990-01-01,123 Street,"
            + UUID.randomUUID()
            + ",John,Doe,johndoe,password,true";
    assertThrows(
        IllegalArgumentException.class, () -> DeserializationUtil.deserializeTrainee(line));
  }

  @Test
  void deserializeTrainer_shouldReturnTrainer() {
    String line =
        UUID.randomUUID()
            + ",Fitness,"
            + UUID.randomUUID()
            + ",Jane,Smith,janesmith,password,false";

    Trainer result = DeserializationUtil.deserializeTrainer(line);

    assertNotNull(result);
    assertEquals("Fitness", result.getSpecialization());
    assertEquals("Jane", result.getFirstName());
    assertEquals("Smith", result.getLastName());
    assertEquals("janesmith", result.getUsername());
    assertEquals("password", result.getPassword());
    assertFalse(result.getIsActive());
  }

  @Test
  void deserializeTrainer_shouldThrowExceptionForInsufficientFields() {
    String line = UUID.randomUUID() + ",Fitness," + UUID.randomUUID();
    assertThrows(
        IllegalArgumentException.class, () -> DeserializationUtil.deserializeTrainer(line));
  }

  @Test
  void deserializeTraining_shouldReturnTraining() {
    String line =
        UUID.randomUUID()
            + ","
            + UUID.randomUUID()
            + ","
            + UUID.randomUUID()
            + ",TrainingName,WRESTLING,2023-01-01,90";

    Training result = DeserializationUtil.deserializeTraining(line);

    assertNotNull(result);
    assertEquals("TrainingName", result.getTrainingName());
    assertEquals(TrainingType.WRESTLING, result.getTrainingType());
    assertEquals(LocalDate.of(2023, 1, 1), result.getTrainingDate());
    assertEquals(90, result.getTrainingDuration());
  }

  @Test
  void deserializeTraining_shouldThrowExceptionForInvalidTrainingType() {
    String line =
        UUID.randomUUID()
            + ","
            + UUID.randomUUID()
            + ","
            + UUID.randomUUID()
            + ",TrainingName,INVALID_TYPE,2023-01-01,90";
    assertThrows(
        IllegalArgumentException.class, () -> DeserializationUtil.deserializeTraining(line));
  }

  @Test
  void splitAndValidate_shouldThrowExceptionForNullInput() {
    assertThrows(
        IllegalArgumentException.class, () -> DeserializationUtil.deserializeTrainee(null));
  }

  @Test
  void splitAndValidate_shouldThrowExceptionForInsufficientFields() {
    String line = "Insufficient,Fields";
    assertThrows(
        IllegalArgumentException.class, () -> DeserializationUtil.deserializeTrainee(line));
  }
}
