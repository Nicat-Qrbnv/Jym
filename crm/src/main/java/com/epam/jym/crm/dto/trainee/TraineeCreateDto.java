package com.epam.jym.crm.dto.trainee;

import com.epam.jym.crm.dto.user.UserCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TraineeCreateDto(
    @NotNull @Valid UserCreateDto profile,
    LocalDate dateOfBirth,
    @Size(max = 255) String address) {}
