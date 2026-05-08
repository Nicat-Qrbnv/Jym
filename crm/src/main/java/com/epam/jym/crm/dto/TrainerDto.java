package com.epam.jym.crm.dto;

public record TrainerDto(
    Long id,
    String firstName,
    String lastName,
    String username,
    String password,
    boolean active,
    TrainingDto training,
    TrainingTypeDto specialization) {}
