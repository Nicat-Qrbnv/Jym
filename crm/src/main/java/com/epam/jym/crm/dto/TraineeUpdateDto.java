package com.epam.jym.crm.dto;

import java.time.LocalDate;

public record TraineeUpdateDto(
    String firstName,
    String lastName,
    String password,
    boolean active,
    LocalDate dateOfBirth,
    String address) {}

