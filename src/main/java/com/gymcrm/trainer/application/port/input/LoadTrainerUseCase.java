package com.gymcrm.trainer.application.port.input;

import com.gymcrm.trainer.domain.Trainer;
import java.util.List;

public interface LoadTrainerUseCase {
	Trainer loadByUsername(String username);

	List<Trainer> loadAll();

	List<Trainer> loadActiveTrainersNotAssignedToTrainee(String traineeUsername);
}
