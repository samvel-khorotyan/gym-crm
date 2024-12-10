package com.gymcrm.dao;

import java.util.List;
import java.util.UUID;

public interface LoadPort<T> {
  T fetchById(UUID id);

  List<T> fetchAll();
}
