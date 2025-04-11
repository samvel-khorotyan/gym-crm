package com.gymcrm.trainer.application.port.output;

import com.gymcrm.trainer.domain.Trainer;
import java.util.List;
import java.util.UUID;

public interface LoadTrainerPort {
	Trainer findByIdWithTrainees(UUID id);

	List<Trainer> findAllByUsernames(List<String> usernames);

	Trainer findByUsernameWithTrainees(String username);

	Trainer findByUsername(String username);

	List<Trainer> findAll();

	List<Trainer> findActiveTrainersNotAssignedToTrainee(String traineeUsername);
}
