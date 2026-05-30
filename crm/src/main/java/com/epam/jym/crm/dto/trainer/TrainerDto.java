package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserProfileDto;

public record TrainerDto(UserProfileDto profile, TrainingTypeDto specialization) {}
