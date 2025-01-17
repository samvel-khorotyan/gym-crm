package com.gymcrm.trainee.application.port.input;

import com.gymcrm.trainee.domain.Trainee;
import java.util.List;

public interface LoadTraineeUseCase {
  Trainee loadByUsername(String username);

  List<Trainee> loadAll();
}
