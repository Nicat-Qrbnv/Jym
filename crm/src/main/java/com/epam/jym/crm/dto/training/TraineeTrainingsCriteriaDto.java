package com.epam.jym.crm.dto.training;

import java.time.LocalDate;

public record TraineeTrainingsCriteriaDto(
    LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType) {}
