package com.gymcrm.trainer.application.port.output;

import com.gymcrm.trainer.domain.ActionType;
import com.gymcrm.training.domain.Training;

public interface UpdateTrainerWorkloadPort {
	void sendTrainerWorkload(Training training, ActionType actionType);
}
