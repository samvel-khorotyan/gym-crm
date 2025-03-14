package com.gymcrm.trainer.application.port.output;

import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;

public interface LoadTrainerWorkloadPort {
	TrainerMonthlyWorkloadResponse getTrainerMonthlyWorkload(String username, int year, int month);
}
