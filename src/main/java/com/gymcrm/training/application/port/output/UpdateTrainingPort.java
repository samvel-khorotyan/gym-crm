package com.gymcrm.training.application.port.output;

import com.gymcrm.training.domain.Training;
import java.util.List;
import java.util.UUID;

public interface UpdateTrainingPort {
  void save(Training training);

  void saveAll(List<Training> trainings);

  void deleteByTraineeId(UUID traineeId);
}
