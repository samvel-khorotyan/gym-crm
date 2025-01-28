package com.gymcrm.trainingtype.adapter.output.persistence;

import com.gymcrm.trainingtype.application.exception.TrainingTypeNotFoundException;
import com.gymcrm.trainingtype.application.port.output.LoadTrainingTypePort;
import com.gymcrm.trainingtype.application.port.output.UpdateTrainingTypePort;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingTypeRepository implements UpdateTrainingTypePort, LoadTrainingTypePort {
  private final TrainingTypePersistenceRepository repository;

  @Autowired
  public TrainingTypeRepository(TrainingTypePersistenceRepository repository) {
    this.repository = repository;
  }

  @Override
  public TrainingType save(TrainingType trainingType) {
    return repository.save(trainingType);
  }

  @Override
  public TrainingType findById(UUID id) {
    return repository.findById(id).orElseThrow(() -> TrainingTypeNotFoundException.by(id));
  }

  @Override
  public TrainingType findByTrainingTypeName(String trainingTypeName) {
    return repository
        .findByTrainingTypeName(trainingTypeName)
        .orElseThrow(() -> TrainingTypeNotFoundException.by(trainingTypeName));
  }
}
