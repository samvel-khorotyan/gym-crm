package com.gymcrm.trainer.application.port.output;

import com.gymcrm.trainer.domain.Trainer;
import java.util.List;
import java.util.UUID;

public interface LoadTrainerPort {
  Trainer findById(UUID id);

  List<Trainer> findAll();

  List<Trainer> findTrainersNotAssignedToTrainee(String trainerName);
}
