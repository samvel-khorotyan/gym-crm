package com.gymcrm.service;

import com.gymcrm.domain.Trainee;
import java.util.UUID;

public interface TraineeUpdateUseCase {
  void updateTrainee(UUID userId, Trainee trainee);

  void removeTrainee(UUID id);
}
