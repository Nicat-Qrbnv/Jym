package com.epam.jym.crm.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {

  T save(T entity);

  Optional<T> findById(Long id);

  List<T> findAll();

  void delete(Long id);

  default void requireNotNull(T obj) {
    if (obj == null) {
      throw new IllegalArgumentException("Object must not be null");
    }
  }

  default void requireId(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
  }

  default void requireName(Object name) {
    if (name == null) {
      throw new IllegalArgumentException("name must not be null");
    }
  }
}
