package com.gymcrm.training.adapter.output.persistence;

import com.gymcrm.training.domain.Training;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrainingPersistenceRepository
    extends JpaRepository<Training, UUID>, JpaSpecificationExecutor<Training> {}
