package com.gymcrm.trainingtype.application.port.output;

import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.List;

public interface LoadTrainingTypePort {
  List<TrainingType> findAll();

  TrainingType findByTrainingTypeName(String trainingTypeName);
}
