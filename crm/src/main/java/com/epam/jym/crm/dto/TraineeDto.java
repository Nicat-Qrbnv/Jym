package com.epam.jym.crm.dto;

import java.time.LocalDate;

public record TraineeDto(
    Long id,
    String firstName,
    String lastName,
    String username,
    String password,
    boolean active,
    LocalDate dateOfBirth,
    String address) {}
