package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.training.TrainingTypeDto;

public record TrainerDto(Long id, Long userId, String username, TrainingTypeDto specialization) {}
