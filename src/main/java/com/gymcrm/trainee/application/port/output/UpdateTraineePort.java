package com.gymcrm.trainee.application.port.output;

import com.gymcrm.trainee.domain.Trainee;
import java.util.UUID;

public interface UpdateTraineePort {
  void save(Trainee trainee);

  void deleteById(UUID id);

  void deleteByUsername(String username);
}
