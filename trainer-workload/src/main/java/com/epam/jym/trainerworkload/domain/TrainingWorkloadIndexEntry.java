package com.epam.jym.trainerworkload.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "training_workload_index")
public record TrainingWorkloadIndexEntry(
    @Id long trainingId,
    String trainerUsername,
    int year,
    int month,
    int durationInMinutes,
    TrainingWorkloadIndexState state) {

  public static TrainingWorkloadIndexEntry active(
      long trainingId, String trainerUsername, int year, int month, int durationInMinutes) {
    return new TrainingWorkloadIndexEntry(
        trainingId,
        trainerUsername,
        year,
        month,
        durationInMinutes,
        TrainingWorkloadIndexState.ACTIVE);
  }

  public TrainingWorkloadIndexEntry asDeleted() {
    return new TrainingWorkloadIndexEntry(
        trainingId,
        trainerUsername,
        year,
        month,
        durationInMinutes,
        TrainingWorkloadIndexState.DELETED);
  }

  public boolean isDeleted() {
    return state == TrainingWorkloadIndexState.DELETED;
  }
}
