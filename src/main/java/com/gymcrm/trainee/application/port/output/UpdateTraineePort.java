package com.gymcrm.trainee.application.port.output;

import com.gymcrm.trainee.domain.Trainee;

public interface UpdateTraineePort {
	Trainee save(Trainee trainee);

	void deleteByUsername(String username);
}
