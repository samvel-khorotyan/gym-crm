package com.gymcrm.training.application.port.input;

import com.gymcrm.training.domain.Training;
import java.util.UUID;

public interface UpdateTrainingUseCase {
	Training update(UUID trainingId, UpdateTrainingCommand command);

	void deleteById(UUID trainingId);
}
