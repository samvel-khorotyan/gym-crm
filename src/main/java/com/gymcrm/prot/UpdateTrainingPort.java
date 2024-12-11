package com.gymcrm.prot;

import com.gymcrm.domain.Training;

public interface UpdateTrainingPort {
  void saveOrUpdate(Training training);
}
