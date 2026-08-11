package com.epam.jym.trainerworkload.repository;

import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainingWorkloadIndexMongoRepository
    extends MongoRepository<TrainingWorkloadIndexEntry, Long> {}
