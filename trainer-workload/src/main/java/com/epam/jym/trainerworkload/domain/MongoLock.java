package com.epam.jym.trainerworkload.domain;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "training_workload_locks")
public record MongoLock(
    @Id Long trainingId,
    String token,
    @Indexed(expireAfter = "0s") Instant expiresAt) {}
