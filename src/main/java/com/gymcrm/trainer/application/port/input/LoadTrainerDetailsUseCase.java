package com.gymcrm.trainer.application.port.input;

import com.gymcrm.trainer.adapter.input.web.response.TrainerDetailsWithWorkloadResponse;

public interface LoadTrainerDetailsUseCase {
	TrainerDetailsWithWorkloadResponse loadTrainerDetailsWithWorkload(String username);
}
