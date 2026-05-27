package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
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
public class TraineeServiceImpl implements TraineeService {

  private final TraineeRepository traineeRepo;
  private final TrainerService trainerService;
  private final UserService userService;

  @Transactional
  @Override
  public Trainee createTrainee(TraineeCreateDto traineeDto) {
    if (traineeDto == null) {
      throw new IllegalArgumentException("traineeDto must not be null");
    }

    User user = userService.register(traineeDto.userDto());
    Trainee trainee = new Trainee();
    trainee.setUser(user);
    trainee.setDateOfBirth(traineeDto.dateOfBirth());
    trainee.setAddress(traineeDto.address());

    trainee = traineeRepo.save(trainee);

    return trainee;
  }

  @Transactional
  @Override
  public Trainee updateTrainee(Long traineeId, TraineeUpdateDto traineeDto) {
    if (traineeDto == null) {
      throw new IllegalArgumentException("traineeDto must not be null");
    }

    Trainee trainee = getTrainee(traineeId);
    trainee.setDateOfBirth(traineeDto.dateOfBirth());
    trainee.setAddress(traineeDto.address());
    trainee = traineeRepo.save(trainee);

    return trainee;
  }

  @Transactional
  @Override
  public void deleteTrainee(Long traineeId) {
    traineeRepo.deleteById(traineeId);
  }

  @Transactional
  @Override
  public void deleteTrainee(String username) {
    Trainee trainee = getTrainee(username);
    traineeRepo.delete(trainee);
  }

  @Override
  public @NonNull Trainee getTraineeByUsername(String username) {
    return getTrainee(username);
  }

  @Override
  public @NonNull Trainee getTrainee(Long traineeId) {
    return traineeRepo
        .findById(traineeId)
        .orElseThrow(
            () -> {
              log.warn("Trainee not found by id: {}", traineeId);
              return new IllegalArgumentException("Trainee not found: " + traineeId);
            });
  }

  private Trainee getTrainee(String username) {
    return traineeRepo
        .findByUserUsername(username)
        .orElseThrow(
            () -> {
              log.warn("Trainee not found by username: {}", username);
              return new IllegalArgumentException("Trainee not found: " + username);
            });
  }

  @Override
  public List<Trainee> getAllTrainees() {
    return traineeRepo.findAll();
  }

  @Transactional
  @Override
  public List<Trainer> updateTraineeTrainers(Long traineeId, List<Long> trainerIds) {
    if (traineeId == null) {
      throw new IllegalArgumentException("trainerUsernames must not be null");
    }

    Trainee trainee = getTrainee(traineeId);
    if (trainerIds == null || trainerIds.isEmpty()) {
      trainee.setTrainers(List.of());
      traineeRepo.save(trainee);
      return List.of();
    }
    List<Trainer> trainers = trainerService.getTrainersByIds(trainerIds);
    trainee.setTrainers(trainers);
    traineeRepo.save(trainee);

    return trainers;
  }
}
