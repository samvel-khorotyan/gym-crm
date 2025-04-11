package com.gymcrm.trainer.application.port.output;

import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadResponseMessage;

public interface ReceiveTrainerWorkloadResponsePort {
	void handleWorkloadResponse(TrainerWorkloadResponseMessage response);
}
