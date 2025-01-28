package com.gymcrm.trainee.application.port.output;

import com.gymcrm.trainee.domain.Trainee;
import java.util.List;
import java.util.UUID;

public interface LoadTraineePort {
	Trainee findByIdWithTrainers(UUID id);

	Trainee findByUsernameWithTrainers(String username);

	Trainee findByUsername(String username);

	List<Trainee> findAll();
}
