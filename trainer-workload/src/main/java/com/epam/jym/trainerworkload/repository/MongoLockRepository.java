package com.epam.jym.trainerworkload.repository;

import com.epam.jym.trainerworkload.domain.MongoLock;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoLockRepository extends MongoRepository<MongoLock, Long> {}
