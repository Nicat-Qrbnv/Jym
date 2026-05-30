package com.epam.jym.crm.dto.trainee;

import com.epam.jym.crm.dto.user.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TraineeUpdateDto(
    @NotNull @Valid UserDto user,
    LocalDate dateOfBirth,
    @Size(max = 255) String address) {}
