package com.gymcrm.trainer.application.port.input;

import com.gymcrm.trainer.adapter.input.web.response.TrainerWorkloadSummary;
import java.util.List;

public interface LoadTrainerSummaryUseCase {
	List<TrainerWorkloadSummary> loadAllTrainersWithWorkload();
}
