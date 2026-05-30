package com.epam.jym.crm.dto.training;

import java.time.LocalDate;

public record TrainingDto(
    String trainingName, LocalDate trainingDate, String trainingType, int trainingDuration) {}
