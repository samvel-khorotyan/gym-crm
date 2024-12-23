package com.gymcrm.trainer.application.port.output;

import com.gymcrm.trainer.domain.Trainer;

public interface UpdateTrainerPort {
  void save(Trainer trainer);
}
