package com.gymcrm.trainer.application.port.input;

import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;

public interface LoadTrainerWorkloadUseCase {
	TrainerMonthlyWorkloadResponse loadTrainerMonthlyWorkload(String username, int year, int month);

	TrainerMonthlyWorkloadResponse loadTrainerCurrentMonthWorkload(String username);
}
