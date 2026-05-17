package com.epam.jym.crm.dto.trainee;

import java.time.LocalDate;

public record TraineeDto(
    Long id,
    Long userId,
    String username,
    LocalDate dateOfBirth,
    String address) {}
