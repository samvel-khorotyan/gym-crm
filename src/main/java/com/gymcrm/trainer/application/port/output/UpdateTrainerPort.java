package com.gymcrm.trainer.application.port.output;

import com.gymcrm.trainer.domain.Trainer;

public interface UpdateTrainerPort {
	Trainer save(Trainer trainer);
}
