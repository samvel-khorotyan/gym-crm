package com.gymcrm.trainee.application.port.input;

import java.util.Map;
import java.util.UUID;

public interface TraineeUpdateUseCase {
  void update(UpdateTraineeCommand command);

  void updatePassword(UpdateTraineePasswordCommand command);

  void updateTrainersOfTrainee(UUID traineeId, Map<UUID, UUID> trainerToTrainingMap);

  void updateTrainersOfTrainee(UpdateTraineeCommand command);

  void activate(UUID traineeId);

  void deactivate(UUID traineeId);

  void deleteById(UUID id);

  void deleteByUsername(String username);
}
