package com.gymcrm.trainee.adapter.output.persistence;

import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainee.application.port.output.LoadTraineePort;
import com.gymcrm.trainee.application.port.output.UpdateTraineePort;
import com.gymcrm.trainee.domain.Trainee;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TraineeRepository implements UpdateTraineePort, LoadTraineePort {
	private final TraineePersistenceRepository repository;

	@Autowired
	public TraineeRepository(TraineePersistenceRepository repository) {
		this.repository = repository;
	}

	@Override
	public Trainee findByIdWithTrainers(UUID id) {
		return repository.findByIdWithTrainers(id).orElseThrow(() -> TraineeNotFoundException.by(id));
	}

	@Override
	public Trainee findByUsernameWithTrainers(String username) {
		return repository.findByUsernameWithTrainers(username).orElseThrow(() -> TraineeNotFoundException.by(username));
	}

	@Override
	public Trainee findByUsername(String username) {
		return repository.findByUserUsername(username).orElseThrow(() -> TraineeNotFoundException.by(username));
	}

	@Override
	public List<Trainee> findAll() {
		return repository.findAll();
	}

	@Override
	public Trainee save(Trainee trainee) {
		return repository.save(trainee);
	}

	@Override
	public void deleteByUsername(String username) {
		repository.deleteByUsername(username);
	}
}
