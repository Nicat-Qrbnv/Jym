package com.epam.jym.crm.dto.trainee;

import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.user.UserDto;
import java.time.LocalDate;
import java.util.List;

public record TraineeProfileDto(
    UserDto user, LocalDate dateOfBirth, String address, List<TrainerDto> trainers) {}
