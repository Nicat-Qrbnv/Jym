package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.InvalidRequestException;
import com.epam.jym.crm.exception.ResourceNotFoundException;
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
  private final UserService userService;
  private final TrainingTypeService trainingTypeService;
  private final TraineeRepository traineeRepo;

  @Transactional
  @Override
  public Trainer createTrainer(TrainerCreateDto trainerDto) {
    if (trainerDto == null) {
      throw new InvalidRequestException("trainerDto must not be null");
    }

    User user = userService.register(trainerDto.profile());
    Trainer trainer = new Trainer();
    trainer.setUser(user);
    trainer.setSpecialization(trainingTypeService.getTypeIfValid(trainerDto.specialization()));

    return trainerRepo.save(trainer);
  }

  @Transactional
  @Override
  public Trainer updateTrainerProfile(String username, TrainerUpdateDto trainerDto) {
    if (trainerDto == null) {
      throw new InvalidRequestException("trainerDto must not be null");
    }

    Trainer trainer = getTrainerByUsername(username);
    User user = trainer.getUser();
    user.setFirstName(trainerDto.profile().firstName());
    user.setLastName(trainerDto.profile().lastName());
    user.setActive(trainerDto.profile().isActive());

    return trainerRepo.save(trainer);
  }

  @Override
  public Trainer getTrainerByUsername(String username) {
    return trainerRepo
        .findByUsername(username)
        .orElseThrow(
            () -> {
              log.warn("Trainer not found by username: {}", username);
              return new ResourceNotFoundException("Trainer not found: " + username);
            });
  }

  @Override
  public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
    if (!traineeRepo.existsByUsername(traineeUsername)) {
      log.warn("Trainee not found by username: {}", traineeUsername);
      throw new ResourceNotFoundException("Trainee not found: " + traineeUsername);
    }

    return trainerRepo.findTrainersNotAssignedToTrainee(traineeUsername);
  }

  @Override
  public List<Long> searchTrainersByName(String name) {
    if (name != null && !name.isBlank()) {
      return trainerRepo.findIdsByNameContaining('%' + name + '%');
    }
    return List.of();
  }

  @Override
  public @NonNull Trainer getTrainer(Long trainerId) {
    return trainerRepo
        .findById(trainerId)
        .orElseThrow(
            () -> {
              log.warn("Trainer not found by id: {}", trainerId);
              return new ResourceNotFoundException("Trainer not found by id: " + trainerId);
            });
  }

  @Override
  public List<Trainer> getTrainersByUsernames(List<String> usernames) {
    if (usernames != null && !usernames.isEmpty()) {
      return trainerRepo.findByUsernames(usernames);
    }
    return List.of();
  }
}
