package com.gymcrm.service;

import com.gymcrm.domain.Trainee;
import java.util.List;
import java.util.UUID;

public interface LoadTraineeUseCase {
  Trainee getTraineeById(UUID userId);

  List<Trainee> getAllTrainees();
}
