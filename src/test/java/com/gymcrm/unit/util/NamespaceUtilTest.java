package com.gymcrm.unit.util;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.util.NamespaceUtil;
import org.junit.jupiter.api.Test;

class NamespaceUtilTest {

  @Test
  void getNamespace_shouldReturnNamespaceForTraineeClass() {
    String namespace = NamespaceUtil.getNamespace(Trainee.class);
    assertEquals("trainee", namespace);
  }

  @Test
  void getNamespace_shouldReturnNamespaceForTrainerClass() {
    String namespace = NamespaceUtil.getNamespace(Trainer.class);
    assertEquals("trainer", namespace);
  }

  @Test
  void getNamespace_shouldReturnNamespaceForTrainingClass() {
    String namespace = NamespaceUtil.getNamespace(Training.class);
    assertEquals("training", namespace);
  }

  @Test
  void getNamespace_shouldThrowExceptionForUndefinedClass() {
    class Undefined {}

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> NamespaceUtil.getNamespace(Undefined.class));
    assertEquals("No namespace defined for class: Undefined", exception.getMessage());
  }

  @Test
  void getNamespace_shouldThrowExceptionForNullClass() {
    assertThrows(NullPointerException.class, () -> NamespaceUtil.getNamespace(null));
  }
}
