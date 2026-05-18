package com.epam.jym.crm.dto.trainee;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TraineeCreateDto(
    @NotNull @Positive Long userId, LocalDate dateOfBirth, @Size(max = 255) String address) {}
