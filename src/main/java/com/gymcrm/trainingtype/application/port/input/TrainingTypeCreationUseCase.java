package com.gymcrm.trainingtype.application.port.input;

import com.gymcrm.trainingtype.domain.TrainingType;

public interface TrainingTypeCreationUseCase {
  TrainingType create(CreateTrainingTypeCommand command);
}
