package com.gymcrm.dao;

import com.gymcrm.domain.Trainer;

public interface UpdateTrainerPort {
  void saveOrUpdate(Trainer trainer);
}
