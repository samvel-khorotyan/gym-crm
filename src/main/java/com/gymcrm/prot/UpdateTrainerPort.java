package com.gymcrm.prot;

import com.gymcrm.domain.Trainer;

public interface UpdateTrainerPort {
  void saveOrUpdate(Trainer trainer);
}
