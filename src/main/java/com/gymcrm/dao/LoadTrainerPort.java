package com.gymcrm.dao;

import com.gymcrm.domain.Trainer;
import java.util.List;
import java.util.UUID;

public interface LoadTrainerPort {
  Trainer fetchById(UUID id);

  List<Trainer> fetchAll();
}
