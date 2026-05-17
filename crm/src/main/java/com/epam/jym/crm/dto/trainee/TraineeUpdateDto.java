package com.epam.jym.crm.dto.trainee;

import java.time.LocalDate;

public record TraineeUpdateDto(LocalDate dateOfBirth, String address) {}
