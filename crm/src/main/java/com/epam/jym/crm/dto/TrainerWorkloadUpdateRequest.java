package com.epam.jym.crm.dto;

import java.time.LocalDate;

public record TrainerWorkloadUpdateRequest(
    String trainerUsername,
    String trainerFirstName,
    String trainerLastName,
    boolean trainerActive,
    LocalDate trainingDate,
    int durationInMinutes,
    ActionType actionType,
    long trainingId) {}
