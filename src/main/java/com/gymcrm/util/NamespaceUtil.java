package com.gymcrm.util;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import java.util.Map;
import java.util.Optional;

public class NamespaceUtil {

  private static final Map<Class<?>, String> NAMESPACE_MAP =
      Map.of(
          Trainee.class, "trainee",
          Trainer.class, "trainer",
          Training.class, "training");

  public static String getNamespace(Class<?> clazz) {
    return Optional.ofNullable(NAMESPACE_MAP.get(clazz))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "No namespace defined for class: " + clazz.getSimpleName()));
  }
}
