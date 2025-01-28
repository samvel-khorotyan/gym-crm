package com.gymcrm.trainer.adapter.output.persistence;

import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerRepository implements UpdateTrainerPort, LoadTrainerPort {
	private final TrainerPersistenceRepository repository;

	public TrainerRepository(TrainerPersistenceRepository repository) {
		this.repository = repository;
	}

	@Override
	public Trainer save(Trainer trainer) {
		return repository.save(trainer);
	}

	@Override
	public Trainer findByIdWithTrainees(UUID id) {
		return repository.findByIdWithTrainees(id).orElseThrow(() -> TrainerNotFoundException.by(id));
	}

	@Override
	public List<Trainer> findAllByUsernames(List<String> usernames) {
		return repository.findAllByUsernames(usernames);
	}

	@Override
	public Trainer findByUsernameWithTrainees(String username) {
		return repository.findByUsernameWithTrainees(username).orElseThrow(() -> TrainerNotFoundException.by(username));
	}

	@Override
	public Trainer findByUsername(String username) {
		return repository.findByUserUsername(username).orElseThrow(() -> TrainerNotFoundException.by(username));
	}

	@Override
	public List<Trainer> findAll() {
		return repository.findAll();
	}

	@Override
	public List<Trainer> findActiveTrainersNotAssignedToTrainee(String traineeUsername) {
		return repository.findActiveTrainersNotAssignedToTrainee(traineeUsername);
	}
}
