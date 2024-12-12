package com.gymcrm.usecase;

import com.gymcrm.command.UpdateTraineeCommand;
import java.util.UUID;

public interface TraineeUpdateUseCase {
  void updateTrainee(UpdateTraineeCommand command);

  void removeTrainee(UUID id);
}
