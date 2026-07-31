package com.epam.jym.trainerworkload.repository;

import com.epam.jym.trainerworkload.domain.TrainerWorkloadDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainerWorkloadDocumentRepository
    extends MongoRepository<TrainerWorkloadDocument, String> {

  Optional<TrainerWorkloadDocument> findByUsername(String trainerUsername);
}
