package com.gymcrm.trainer.application.port.output;

import com.gymcrm.training.domain.Training;

public interface UpdateTrainerWorkloadPort {
	void sendTrainerWorkload(Training training, String actionType);
}
