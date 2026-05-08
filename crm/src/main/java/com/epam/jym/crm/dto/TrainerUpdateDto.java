package com.epam.jym.crm.dto;

public record TrainerUpdateDto(
    String firstName,
    String lastName,
    String password,
    boolean active,
    TrainingDto training,
    TrainingTypeDto specialization) {}

