package com.epam.jym.trainerworkload.repository;

import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TrainingWorkloadIndexRepository {

  private static final String TRAINING_INDEX_KEY_PREFIX = "trainer-workload:training-index:";
  private static final String TRAINING_LOCK_KEY_PREFIX = "trainer-workload:training-lock:";
  private static final Duration LOCK_TTL = Duration.ofSeconds(30);

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public Optional<TrainingWorkloadIndexEntry> findByTrainingId(long trainingId) {
    String value = redisTemplate.opsForValue().get(redisKey(trainingId));
    if (value == null) {
      return Optional.empty();
    }
    return Optional.of(read(value));
  }

  public void save(TrainingWorkloadIndexEntry entry) {
    redisTemplate.opsForValue().set(redisKey(entry.trainingId()), write(entry));
  }

  public void runWithTrainingLock(long trainingId, Runnable action) {
    String lockKey = lockKey(trainingId);
    String lockValue = UUID.randomUUID().toString();
    Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, LOCK_TTL);
    if (!Boolean.TRUE.equals(acquired)) {
      throw new IllegalStateException(
          "Concurrent workload update is already in progress for training id: " + trainingId);
    }
    try {
      action.run();
    } finally {
      releaseLock(lockKey, lockValue);
    }
  }

  private String redisKey(long trainingId) {
    return TRAINING_INDEX_KEY_PREFIX + trainingId;
  }

  private String lockKey(long trainingId) {
    return TRAINING_LOCK_KEY_PREFIX + trainingId;
  }

  private void releaseLock(String lockKey, String lockValue) {
    String currentValue = redisTemplate.opsForValue().get(lockKey);
    if (lockValue.equals(currentValue)) {
      redisTemplate.delete(lockKey);
    }
  }

  private String write(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException("Failed to serialize Redis value", exception);
    }
  }

  private <T> T read(String value) {
    try {
      return objectMapper.readValue(value, (Class<T>) TrainingWorkloadIndexEntry.class);
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException("Failed to deserialize Redis value", exception);
    }
  }
}
