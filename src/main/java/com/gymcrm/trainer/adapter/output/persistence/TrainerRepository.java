package com.gymcrm.trainer.adapter.output.persistence;

import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerRepository implements UpdateTrainerPort, LoadTrainerPort {
  private final TrainerPersistenceRepository repository;

  @Autowired
  public TrainerRepository(TrainerPersistenceRepository repository) {
    this.repository = repository;
  }

  @Override
  public void save(Trainer trainer) {
    repository.save(trainer);
  }

  @Override
  public Trainer findById(UUID id) {
    return repository.findById(id).orElseThrow(() -> TrainerNotFoundException.by(id));
  }

  @Override
  public List<Trainer> findAll() {
    return repository.findAll();
  }

  @Override
  public List<Trainer> findTrainersNotAssignedToTrainee(String trainerName) {
    return repository.findTrainersNotAssignedToTrainee(trainerName);
  }
}
