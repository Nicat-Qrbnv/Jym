package com.epam.jym.trainerworkload.domain;

public record TrainingWorkloadIndexEntry(
    long trainingId,
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
