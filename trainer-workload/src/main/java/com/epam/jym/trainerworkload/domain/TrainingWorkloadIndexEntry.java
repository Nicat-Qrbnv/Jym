package com.epam.jym.trainerworkload.domain;

public record TrainingWorkloadIndexEntry(
    long trainingId, String trainerUsername, int year, int month, int durationInMinutes) {}
