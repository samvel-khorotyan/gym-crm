package com.gymcrm.trainingtype.adapter.output.persistence;

import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingTypePersistenceRepository extends JpaRepository<TrainingType, UUID> {
  Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);
}
