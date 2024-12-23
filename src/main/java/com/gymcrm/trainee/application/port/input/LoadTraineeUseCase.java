package com.gymcrm.trainee.application.port.input;

import com.gymcrm.trainee.domain.Trainee;
import java.util.List;
import java.util.UUID;

public interface LoadTraineeUseCase {
  Trainee loadById(UUID userId);

  List<Trainee> loadAll();
}
