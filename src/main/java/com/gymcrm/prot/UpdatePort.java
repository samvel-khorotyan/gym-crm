package com.gymcrm.prot;

import java.util.UUID;

public interface UpdatePort<T> {
  void saveOrUpdate(T entity);

  void removeById(UUID id);
}
