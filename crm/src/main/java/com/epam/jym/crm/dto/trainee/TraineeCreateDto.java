package com.epam.jym.crm.dto.trainee;

import java.time.LocalDate;

public record TraineeCreateDto(Long userId, LocalDate dateOfBirth, String address) {}
