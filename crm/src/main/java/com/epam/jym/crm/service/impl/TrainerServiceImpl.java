package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingTypeService;
import com.epam.jym.crm.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TrainerServiceImpl implements TrainerService {

  private final TrainerRepository trainerRepo;
  private final TraineeRepository traineeRepo;
  private final UserService userService;
  private final TrainingTypeService trainingTypeService;

  @Transactional
  @Override
  public Trainer createTrainer(TrainerCreateDto trainerDto) {
    if (trainerDto == null) {
      throw new IllegalArgumentException("trainerDto must not be null");
    }

    User user = userService.register(trainerDto.userDto());
    Trainer trainer = new Trainer();
    trainer.setUser(user);
    trainer.setSpecialization(trainingTypeService.getType(trainerDto.specializationId()));

    return trainerRepo.save(trainer);
  }

  @Transactional
  @Override
  public Trainer updateTrainer(Long trainerId, TrainerUpdateDto trainerDto) {
    if (trainerDto == null) {
      throw new IllegalArgumentException("trainerDto must not be null");
    }

    Trainer trainer = getTrainer(trainerId);
    trainer.setSpecialization(trainingTypeService.getType(trainerDto.specializationId()));
    trainer = trainerRepo.save(trainer);

    return trainer;
  }

  @Override
  public Trainer getTrainerByUsername(String username) {
    return trainerRepo
        .findByUserUsername(username)
        .orElseThrow(
            () -> {
              log.warn("Trainer not found by username: {}", username);
              return new IllegalArgumentException("Trainer not found: " + username);
            });
  }

  @Override
  public List<Trainer> getAllTrainers() {
    return trainerRepo.findAll();
  }

  @Override
  public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
    if (traineeRepo.findByUserUsername(traineeUsername).isEmpty()) {
      log.warn("Trainee not found by username: {}", traineeUsername);
      throw new IllegalArgumentException("Trainee not found: " + traineeUsername);
    }

    return trainerRepo.findTrainersNotAssignedToTrainee(traineeUsername);
  }

  @Override
  public @NonNull Trainer getTrainer(Long trainerId) {
    return trainerRepo
        .findById(trainerId)
        .orElseThrow(
            () -> {
              log.warn("Trainer not found by id: {}", trainerId);
              return new IllegalArgumentException("Trainer not found by id: " + trainerId);
            });
  }

  @Override
  public List<Trainer> getTrainersByIds(List<Long> trainerIds) {
    if (trainerIds != null && !trainerIds.isEmpty()) {
      return trainerRepo.findAllById(trainerIds);
    }
    return List.of();
  }
}
