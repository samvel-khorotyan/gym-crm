package com.gymcrm.trainingtype.application.port.input;

import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.List;

public interface LoadTrainingTypeUseCase {
  List<TrainingType> loadAll();
}
