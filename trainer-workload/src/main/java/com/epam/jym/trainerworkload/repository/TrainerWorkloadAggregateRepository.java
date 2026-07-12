package com.epam.jym.trainerworkload.repository;

import com.epam.jym.trainerworkload.domain.MonthlyWorkloadAggregate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TrainerWorkloadAggregateRepository {

  private static final String AGGREGATE_KEY_PREFIX = "trainer-workload:aggregates:";

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public Optional<MonthlyWorkloadAggregate> findByMonth(
      String trainerUsername, int year, int month) {
    Object value =
        redisTemplate.opsForHash().get(redisKey(trainerUsername), monthField(year, month));
    if (value == null) {
      return Optional.empty();
    }
    if (!(value instanceof String jsonValue)) {
      throw new IllegalStateException("Unexpected Redis value type for trainer aggregate");
    }
    return Optional.of(read(jsonValue, MonthlyWorkloadAggregate.class));
  }

  public List<MonthlyWorkloadAggregate> findAllByTrainerUsername(String trainerUsername) {
    Map<Object, Object> entries = redisTemplate.opsForHash().entries(redisKey(trainerUsername));
    List<MonthlyWorkloadAggregate> aggregates = new ArrayList<>(entries.size());
    for (Object value : entries.values()) {
      if (value instanceof String jsonValue) {
        aggregates.add(read(jsonValue, MonthlyWorkloadAggregate.class));
      }
    }
    return aggregates;
  }

  public void save(MonthlyWorkloadAggregate aggregate) {
    redisTemplate
        .opsForHash()
        .put(
            redisKey(aggregate.getTrainerUsername()),
            monthField(aggregate.getYear(), aggregate.getMonth()),
            write(aggregate));
  }

  public void delete(String trainerUsername, int year, int month) {
    redisTemplate.opsForHash().delete(redisKey(trainerUsername), monthField(year, month));
  }

  private String redisKey(String trainerUsername) {
    return AGGREGATE_KEY_PREFIX + trainerUsername;
  }

  private String monthField(int year, int month) {
    return year + "-" + month;
  }

  private String write(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException("Failed to serialize Redis value", exception);
    }
  }

  private <T> T read(String value, Class<T> valueType) {
    try {
      return objectMapper.readValue(value, valueType);
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException("Failed to deserialize Redis value", exception);
    }
  }
}
