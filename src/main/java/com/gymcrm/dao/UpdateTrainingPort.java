package com.gymcrm.dao;

import com.gymcrm.domain.Training;

public interface UpdateTrainingPort {
  void saveOrUpdate(Training training);
}
