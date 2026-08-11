package com.epam.jym.trainerworkload.repository;

import com.epam.jym.trainerworkload.domain.MongoLock;
import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexEntry;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TrainingWorkloadIndexRepository {

  private static final long LOCK_TTL_SECONDS = 30;

  private final TrainingWorkloadIndexMongoRepository indexMongoRepo;
  private final MongoLockRepository lockMongoRepo;
  private final MongoTemplate mongoTemplate;

  public Optional<TrainingWorkloadIndexEntry> findByTrainingId(long trainingId) {
    return indexMongoRepo.findById(trainingId);
  }

  public void save(TrainingWorkloadIndexEntry entry) {
    indexMongoRepo.save(entry);
  }

  public void runWithTrainingLock(long trainingId, Runnable action) {
    String token = UUID.randomUUID().toString();
    MongoLock lock = new MongoLock(trainingId, token, Instant.now().plusSeconds(LOCK_TTL_SECONDS));
    try {
      lockMongoRepo.insert(lock);
    } catch (DuplicateKeyException e) {
      throw new IllegalStateException(
          "Concurrent workload update is already in progress for training id: " + trainingId);
    }
    try {
      action.run();
    } finally {
      mongoTemplate.findAndRemove(
          Query.query(Criteria.where("_id").is(trainingId).and("token").is(token)),
          MongoLock.class);
    }
  }

  public void deleteAll() {
    indexMongoRepo.deleteAll();
    lockMongoRepo.deleteAll();
  }
}
