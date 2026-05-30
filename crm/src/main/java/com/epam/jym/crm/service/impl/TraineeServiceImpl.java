package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.InvalidRequestException;
import com.epam.jym.crm.exception.ResourceNotFoundException;
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
      throw new InvalidRequestException("traineeDto must not be null");
    }

    User user = userService.register(traineeDto.profile());
    Trainee trainee = new Trainee();
    trainee.setUser(user);
    trainee.setDateOfBirth(traineeDto.dateOfBirth());
    trainee.setAddress(traineeDto.address());

    trainee = traineeRepo.save(trainee);

    return trainee;
  }

  @Transactional
  @Override
  public Trainee updateTraineeProfile(String username, TraineeUpdateDto traineeDto) {
    if (traineeDto == null) {
      throw new InvalidRequestException("traineeDto must not be null");
    }

    Trainee trainee = getTraineeByUsername(username);
    User user = trainee.getUser();
    user.setFirstName(traineeDto.user().firstName());
    user.setLastName(traineeDto.user().lastName());
    user.setActive(traineeDto.user().isActive());
    trainee.setDateOfBirth(traineeDto.dateOfBirth());
    trainee.setAddress(traineeDto.address());

    return traineeRepo.save(trainee);
  }

  @Transactional
  @Override
  public void deleteTrainee(String username) {
    Trainee trainee = getTraineeByUsername(username);
    traineeRepo.delete(trainee);
    userService.deactivateUser(trainee.getUser().getId());
  }

  @Override
  public @NonNull Trainee getTraineeByUsername(String username) {
    return traineeRepo
        .findTraineeByUsername(username)
        .orElseThrow(
            () -> {
              log.warn("Trainee not found by username: {}", username);
              return new ResourceNotFoundException("Trainee not found: " + username);
            });
  }

  @Override
  public @NonNull Trainee getTrainee(Long traineeId) {
    return traineeRepo
        .findById(traineeId)
        .orElseThrow(
            () -> {
              log.warn("Trainee not found by id: {}", traineeId);
              return new ResourceNotFoundException("Trainee not found: " + traineeId);
            });
  }

  @Transactional
  @Override
  public List<Trainer> updateTraineeTrainers(
      String traineeUsername, List<String> trainerUsernames) {
    Trainee trainee = getTraineeByUsername(traineeUsername);
    if (trainerUsernames.isEmpty()) {
      trainee.setTrainers(List.of());
      trainee = traineeRepo.save(trainee);
      return trainee.getTrainers();
    }

    List<Trainer> trainers = trainerService.getTrainersByUsernames(trainerUsernames);
    trainee.setTrainers(trainers);
    traineeRepo.save(trainee);

    return trainers;
  }
}
