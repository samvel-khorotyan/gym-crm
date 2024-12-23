package com.gymcrm.trainer.application.port.input;

import java.util.UUID;

public interface TrainerUpdateUseCase {
  void update(UpdateTrainerCommand command);

  void updatePassword(UpdateTrainerPasswordCommand command);

  boolean activateDeactivate(UUID trainerId);
}
