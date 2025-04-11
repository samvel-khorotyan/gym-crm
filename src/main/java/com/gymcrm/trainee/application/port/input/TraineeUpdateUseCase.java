package com.gymcrm.trainee.application.port.input;

import com.gymcrm.trainee.domain.Trainee;

public interface TraineeUpdateUseCase {
	Trainee update(UpdateTraineeCommand command);

	Trainee updateTraineeTrainers(UpdateTraineeTrainersCommand command);

	void activateDeactivate(ActivateDeactivateTraineeCommand command);

	void deleteByUsername(String username);
}
