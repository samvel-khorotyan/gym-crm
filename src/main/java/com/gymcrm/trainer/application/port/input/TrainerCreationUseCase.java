package com.gymcrm.trainer.application.port.input;

import com.gymcrm.trainer.domain.Trainer;

public interface TrainerCreationUseCase {
	Trainer create(CreateTrainerCommand command);
}
