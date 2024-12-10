package com.gymcrm.dao;

import com.gymcrm.domain.Trainee;
import java.util.UUID;

public interface UpdateTraineePort {
  void saveOrUpdate(Trainee trainee);

  void removeById(UUID id);
}
