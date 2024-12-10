package com.gymcrm.service;

import com.gymcrm.domain.Training;
import java.util.List;
import java.util.UUID;

public interface LoadTrainingUseCase {
  Training getTrainingById(UUID trainingId);

  List<Training> getAllTrainings();
}
