package com.epam.jym.trainerworkload.repository;

import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TrainingWorkloadIndexRepository {

  private static final String TRAINING_INDEX_KEY_PREFIX = "trainer-workload:training-index:";

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public Optional<TrainingWorkloadIndexEntry> findByTrainingId(long trainingId) {
    String value = redisTemplate.opsForValue().get(redisKey(trainingId));
    if (value == null) {
      return Optional.empty();
    }
    return Optional.of(read(value, TrainingWorkloadIndexEntry.class));
  }

  public void save(TrainingWorkloadIndexEntry entry) {
    redisTemplate.opsForValue().set(redisKey(entry.trainingId()), write(entry));
  }

  public void delete(long trainingId) {
    redisTemplate.delete(redisKey(trainingId));
  }

  private String redisKey(long trainingId) {
    return TRAINING_INDEX_KEY_PREFIX + trainingId;
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
