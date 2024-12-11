package com.gymcrm.service;

import com.gymcrm.domain.Trainer;
import java.util.List;
import java.util.UUID;

public interface LoadTrainerUseCase {
  Trainer getTrainerById(UUID id);

  List<Trainer> getAllTrainers();
}
