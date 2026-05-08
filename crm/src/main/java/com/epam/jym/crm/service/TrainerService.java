package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.TrainerDto;
import java.util.List;
import java.util.Optional;

public interface TrainerService {

  TrainerDto createTrainer(TrainerDto trainerDto);

  TrainerDto updateTrainer(Long trainerId, TrainerDto trainerDto);

  Optional<TrainerDto> selectTrainer(Long trainerId);

  Optional<TrainerDto> selectTrainerByUsername(String username);

  List<TrainerDto> selectAllTrainers();
}
