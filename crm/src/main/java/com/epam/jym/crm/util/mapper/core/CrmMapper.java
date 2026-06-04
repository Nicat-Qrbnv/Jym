package com.epam.jym.crm.util.mapper.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class CrmMapper implements MappingContext {
  private final Map<MapperKey, Mapper<?, ?>> mappers;

  public CrmMapper(List<Mapper<?, ?>> mappers) {
    this.mappers =
        mappers.stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    mapper -> new MapperKey(mapper.sourceType(), mapper.targetType()),
                    Function.identity()));
  }

  @Override
  public <S, T> T map(S source, Class<T> targetType) {
    if (source == null) {
      return null;
    }

    @SuppressWarnings("unchecked")
    Mapper<S, T> mapper = (Mapper<S, T>) findMapper(source.getClass(), targetType);

    if (mapper == null) {
      throw new IllegalArgumentException(
          "No mapper registered for "
              + source.getClass().getName()
              + " -> "
              + targetType.getName());
    }

    return mapper.map(source, this);
  }

  @Override
  public <S, T> Stream<T> mapCollection(Collection<S> source, Class<T> targetType) {
    return source.stream().map(item -> map(item, targetType));
  }

  private Mapper<?, ?> findMapper(Class<?> sourceType, Class<?> targetType) {
    Mapper<?, ?> exactMapper = mappers.get(new MapperKey(sourceType, targetType));
    if (exactMapper != null) {
      return exactMapper;
    }

    return mappers.entrySet().stream()
        .filter(entry -> entry.getKey().targetType().equals(targetType))
        .filter(entry -> entry.getKey().sourceType().isAssignableFrom(sourceType))
        .map(Map.Entry::getValue)
        .findFirst()
        .orElse(null);
  }
}
