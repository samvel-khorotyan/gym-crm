package com.gymcrm.trainingtype.application.port.output;

import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.UUID;

public interface LoadTrainingTypePort {
  TrainingType findById(UUID id);

  TrainingType findByTrainingTypeName(String trainingTypeName);
}
