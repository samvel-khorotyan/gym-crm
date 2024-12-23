package com.gymcrm.trainingtype.application.port.output;

import com.gymcrm.trainingtype.domain.TrainingType;

public interface UpdateTrainingTypePort {
  TrainingType save(TrainingType trainingType);
}
