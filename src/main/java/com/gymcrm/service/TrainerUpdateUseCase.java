package com.gymcrm.service;

import com.gymcrm.domain.Trainer;
import java.util.UUID;

public interface TrainerUpdateUseCase {
  void updateTrainer(UUID id, Trainer updatedTrainer);
}
