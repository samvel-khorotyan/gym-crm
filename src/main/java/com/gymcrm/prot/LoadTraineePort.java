package com.gymcrm.prot;

import com.gymcrm.domain.Trainee;
import java.util.List;
import java.util.UUID;

public interface LoadTraineePort {
  Trainee fetchById(UUID id);

  List<Trainee> fetchAll();
}
