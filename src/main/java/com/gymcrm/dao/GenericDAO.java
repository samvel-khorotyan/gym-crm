package com.gymcrm.dao;

import com.gymcrm.domain.CRMEntity;
import com.gymcrm.prot.LoadPort;
import com.gymcrm.prot.UpdatePort;
import com.gymcrm.util.NamespaceUtil;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public abstract class GenericDAO<T extends CRMEntity> implements UpdatePort<T>, LoadPort<T> {
  protected final Map<UUID, Object> storage;

  public GenericDAO(Storage storage, Class<?> clazz) {
    this.storage = storage.getNamespace(NamespaceUtil.getNamespace(clazz));
  }

  @Override
  public void saveOrUpdate(T entity) {
    if (entity == null || entity.getId() == null) {
      throw new IllegalArgumentException("Entity or its ID must not be null.");
    }
    storage.put(entity.getId(), entity);
  }

  @Override
  @SuppressWarnings("unchecked")
  public T fetchById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ID must not be null.");
    }
    return (T) storage.get(id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<T> fetchAll() {
    return storage.values().stream().map(value -> (T) value).toList();
  }

  @Override
  public void removeById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ID must not be null.");
    }
    storage.remove(id);
  }
}
