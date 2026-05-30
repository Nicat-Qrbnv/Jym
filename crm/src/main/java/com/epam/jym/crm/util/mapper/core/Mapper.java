package com.epam.jym.crm.util.mapper.core;

public interface Mapper<S, T> {
  Class<S> sourceType();

  Class<T> targetType();

  T map(S source, MappingContext context);
}
