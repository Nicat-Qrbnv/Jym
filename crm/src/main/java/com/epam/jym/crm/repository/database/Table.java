package com.epam.jym.crm.repository.database;

import com.epam.jym.crm.entity.Entity;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Table<E extends Entity> {
  private final Map<Long, E> idIndex = new ConcurrentHashMap<>();
  private final Map<String, E> nameIndex = new ConcurrentHashMap<>();
  private long idCounter = 0;

  public void write(E entity) {
    entity.setId(getNextId(entity.getId()));
    E existingEntity = idIndex.get(entity.getId());
    if (existingEntity != null && !Objects.equals(existingEntity.getName(), entity.getName())) {
      nameIndex.remove(existingEntity.getName(), existingEntity);
    }
    nameIndex
        .entrySet()
        .removeIf(
            entry ->
                entry.getValue() == entity && !Objects.equals(entry.getKey(), entity.getName()));
    idIndex.put(entity.getId(), entity);
    nameIndex.put(entity.getName(), entity);
  }

  private long getNextId(Long entityId) {
    if (entityId == null) {
      return ++idCounter;
    } else if (idIndex.keySet().stream().max(Long::compareTo).orElse(0L) < entityId) {
      idCounter = entityId;
    }
    return entityId;
  }

  public Optional<E> getById(Long id) {
    return Optional.ofNullable(idIndex.get(id));
  }

  public Optional<E> getByName(String name) {
    return Optional.ofNullable(nameIndex.get(name));
  }

  public Collection<E> getAll() {
    return idIndex.values();
  }

  public void delete(Long id) {
    E removedEntity = idIndex.remove(id);
    if (removedEntity != null) {
      nameIndex.remove(removedEntity.getName(), removedEntity);
    }
  }
}
