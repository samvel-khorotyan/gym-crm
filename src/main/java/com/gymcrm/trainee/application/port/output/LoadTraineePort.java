package com.gymcrm.trainee.application.port.output;

import com.gymcrm.trainee.domain.Trainee;
import java.util.List;
import java.util.UUID;

public interface LoadTraineePort {
  Trainee findById(UUID id);

  List<Trainee> findAll();
}
