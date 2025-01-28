package com.gymcrm.trainer.application.port.input;

import com.gymcrm.trainer.domain.Trainer;
import java.util.List;
import java.util.UUID;

public interface LoadTrainerUseCase {
  Trainer loadById(UUID id);

  List<Trainer> loadAll();

  List<Trainer> loadTrainersNotAssignedToTrainee(String trainerName);
}
