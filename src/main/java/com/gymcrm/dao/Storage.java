package com.gymcrm.dao;

import com.gymcrm.common.ApplicationProperties;
import com.gymcrm.domain.*;
import com.gymcrm.util.DeserializationUtil;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class Storage {
  private final Map<String, Map<UUID, Object>> storage = new HashMap<>();
  private final ApplicationProperties applicationProperties;

  public Storage(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
    storage.put("trainee", new HashMap<>());
    storage.put("trainer", new HashMap<>());
    storage.put("training", new HashMap<>());
  }

  @PostConstruct
  private void initialize() {
    loadDataFromFile(
        applicationProperties.getTraineeFilePath(),
        "trainee",
        DeserializationUtil::deserializeTrainee);
    loadDataFromFile(
        applicationProperties.getTrainerFilePath(),
        "trainer",
        DeserializationUtil::deserializeTrainer);
    loadDataFromFile(
        applicationProperties.getTrainingFilePath(),
        "training",
        DeserializationUtil::deserializeTraining);
  }

  public Map<UUID, Object> getNamespace(String namespace) {
    return storage.get(namespace);
  }

  private void loadDataFromFile(
      String filePath, String namespace, Function<String, CRMEntity> deserializer) {
    File file = new File(filePath);

    if (file.exists()) {
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        Map<UUID, Object> namespaceMap =
            reader
                .lines()
                .map(deserializer)
                .collect(Collectors.toMap(CRMEntity::getId, obj -> obj));
        storage.put(namespace, namespaceMap);
      } catch (IOException e) {
        throw new RuntimeException("Failed to load data from file: " + filePath, e);
      }
    }
  }
}
