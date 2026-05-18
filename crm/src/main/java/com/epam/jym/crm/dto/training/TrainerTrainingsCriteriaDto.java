package com.epam.jym.crm.dto.training;

import java.time.LocalDate;

public record TrainerTrainingsCriteriaDto(
    LocalDate fromDate, LocalDate toDate, String traineeName) {}
