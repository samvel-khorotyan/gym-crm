package com.gymcrm.training.application.port.output;

import com.gymcrm.training.domain.Training;

public interface UpdateTrainingPort {
  void save(Training training);
}
