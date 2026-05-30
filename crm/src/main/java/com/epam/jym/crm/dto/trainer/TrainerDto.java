package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.user.UserProfileDto;

public record TrainerDto(UserProfileDto profile, String specialization) {}
