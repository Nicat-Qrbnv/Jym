package com.epam.jym.crm.util.mapper.core;

import java.util.Collection;
import java.util.stream.Stream;

public interface MappingContext {
  <S, T> T map(S source, Class<T> targetType);

  <S, T> Stream<T> mapCollection(Collection<S> source, Class<T> targetType);
}
