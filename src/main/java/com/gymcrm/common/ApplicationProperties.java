package com.gymcrm.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApplicationProperties {
  @Value("${storage.data.trainee.file}")
  private String traineeFilePath;

  @Value("${storage.data.trainer.file}")
  private String trainerFilePath;

  @Value("${storage.data.training.file}")
  private String trainingFilePath;
}
