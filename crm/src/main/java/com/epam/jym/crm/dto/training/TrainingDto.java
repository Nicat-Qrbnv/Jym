package com.epam.jym.crm.dto.training;

import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import java.time.LocalDate;

public record TrainingDto(
    Long id,
    String name,
    TrainingTypeDto type,
    TraineeDto trainee,
    TrainerDto trainer,
    LocalDate date,
    int durationInMinutes) {}
