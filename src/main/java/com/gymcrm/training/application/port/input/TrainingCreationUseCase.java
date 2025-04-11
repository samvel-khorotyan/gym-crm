package com.gymcrm.training.application.port.input;

import com.gymcrm.training.domain.Training;

public interface TrainingCreationUseCase {
	Training create(CreateTrainingCommand command);
}
