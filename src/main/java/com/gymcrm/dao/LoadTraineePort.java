package com.gymcrm.dao;

import com.gymcrm.domain.Trainee;
import java.util.List;
import java.util.UUID;

public interface LoadTraineePort {
  Trainee fetchById(UUID id);

  List<Trainee> fetchAll();
}
