package com.gymcrm.trainer.application.port.input;

import com.gymcrm.trainer.domain.Trainer;

public interface TrainerUpdateUseCase {
  Trainer update(UpdateTrainerCommand command);

  void activateDeactivate(ActivateDeactivateTrainerCommand command);
}
