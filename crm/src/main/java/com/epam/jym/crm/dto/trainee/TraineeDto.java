package com.epam.jym.crm.dto.trainee;

import java.time.LocalDate;

public record TraineeDto(
    String username, String firstName, String lastName, LocalDate dateOfBirth, String address) {}
