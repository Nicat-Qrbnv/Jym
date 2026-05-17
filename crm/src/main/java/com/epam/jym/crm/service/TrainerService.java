package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.entity.Trainer;
import java.util.List;
import org.jspecify.annotations.NonNull;

public interface TrainerService {

  TrainerDto createTrainer(TrainerCreateDto trainerDto);

  TrainerDto updateTrainer(Long trainerId, TrainerUpdateDto trainerDto);

  TrainerDto selectTrainer(Long trainerId);

  TrainerDto selectTrainerByUsername(String username);

  List<TrainerDto> selectAllTrainers();

  @NonNull Trainer getTrainer(Long trainerId);
}
