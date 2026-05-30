package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.entity.Trainer;
import java.util.List;
import org.jspecify.annotations.NonNull;

public interface TrainerService {

  Trainer createTrainer(TrainerCreateDto trainerDto);

  Trainer updateTrainer(Long trainerId, TrainerUpdateDto trainerDto);

  @NonNull Trainer getTrainer(Long trainerId);

  Trainer getTrainerByUsername(String username);

  List<Trainer> getTrainersByUsernames(List<String> usernames);

  List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername);

  List<Long> searchTrainersByName(String name);
}
