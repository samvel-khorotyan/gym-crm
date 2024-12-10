package com.gymcrm.dao;

import com.gymcrm.domain.Training;
import java.util.List;
import java.util.UUID;

public interface LoadTrainingPort {
  Training fetchById(UUID id);

  List<Training> fetchAll();
}
