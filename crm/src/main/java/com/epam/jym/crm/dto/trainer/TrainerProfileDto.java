package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.user.UserProfileDto;
import com.epam.jym.crm.dto.user.UserDto;
import java.util.List;

public record TrainerProfileDto(
    UserDto profile, String specialization, List<UserProfileDto> trainees) {}
