package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserProfileDto;
import com.epam.jym.crm.dto.user.UserDto;
import java.util.List;

public record TrainerProfileDto(
    UserDto profile, TrainingTypeDto specialization, List<UserProfileDto> trainees) {}
