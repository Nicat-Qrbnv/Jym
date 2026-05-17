package com.epam.jym.crm.dto.trainee;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TraineeUpdateDto(LocalDate dateOfBirth, @Size(max = 255) String address) {}
