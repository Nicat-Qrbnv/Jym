package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Setter(onMethod_ = @Autowired)
  private ModelMapper mapper;

  @Transactional
  @Override
  public TraineeDto createTrainee(TraineeCreateDto traineeDto) {
    if (traineeDto == null) {
      throw new IllegalArgumentException("traineeDto must not be null");
    }

    log.debug("Creating trainee");
    ensureUserHasNoTraineeProfile(traineeDto.userId());
    User user = userService.getUser(traineeDto.userId());
    Trainee trainee = new Trainee();
    trainee.setUser(user);
    trainee.setDateOfBirth(traineeDto.dateOfBirth());
    trainee.setAddress(traineeDto.address());

    trainee = traineeRepo.save(trainee);
    log.info("Created trainee profile: {}", trainee);

    return mapper.map(trainee, TraineeDto.class);
  }

  @Transactional
  @Override
  public TraineeDto updateTrainee(Long traineeId, TraineeUpdateDto traineeDto) {
    if (traineeDto == null) {
      throw new IllegalArgumentException("traineeDto must not be null");
    }

    Trainee trainee = getTrainee(traineeId);
    trainee.setDateOfBirth(traineeDto.dateOfBirth());
    trainee.setAddress(traineeDto.address());
    trainee = traineeRepo.save(trainee);
    log.info("Updated trainee with id={} username={}", trainee.getId(), trainee.getUsername());

    return mapper.map(trainee, TraineeDto.class);
  }

  @Transactional
  @Override
  public void deleteTrainee(Long traineeId) {
    log.debug("Deleting trainee with id={}", traineeId);
    traineeRepo.deleteById(traineeId);
    log.info("Deleted trainee with id={}", traineeId);
  }

  @Transactional
  @Override
  public void deleteTrainee(String username) {
    log.debug("Deleting trainee by username={}", username);
    Trainee trainee = getTrainee(username);
    traineeRepo.delete(trainee);
    log.info("Deleted trainee with id={} username={}", trainee.getId(), username);
  }

  @Override
  public TraineeDto getTraineeById(Long traineeId) {
    log.debug("Selecting trainee by id={}", traineeId);
    return mapper.map(getTrainee(traineeId), TraineeDto.class);
  }

  @Override
  public TraineeDto getTraineeByUsername(String username) {
    log.debug("Selecting trainee by username={}", username);
    Trainee trainee = getTrainee(username);
    return mapper.map(trainee, TraineeDto.class);
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
  public List<TraineeDto> getAllTrainees() {
    List<Trainee> trainees = traineeRepo.findAll();
    log.debug("Selected {} trainees", trainees.size());

    return trainees.stream().map(t -> mapper.map(t, TraineeDto.class)).toList();
  }

  @Transactional
  @Override
  public List<TrainerDto> updateTraineeTrainers(Long traineeId, List<Long> trainerIds) {
    if (traineeId == null) {
      throw new IllegalArgumentException("trainerUsernames must not be null");
    }

    Trainee trainee = getTrainee(traineeId);
    if (trainerIds == null || trainerIds.isEmpty()) {
      trainee.setTrainers(List.of());
      traineeRepo.save(trainee);
      log.info("Cleared trainee trainers list for username={}", trainee.getUsername());
      return List.of();
    }
    List<Trainer> trainers = trainerService.getTrainersByIds(trainerIds);
    trainee.setTrainers(trainers);
    traineeRepo.save(trainee);
    log.info("Updated trainers list for user #{}", traineeId);

    return trainers.stream().map(trainer -> mapper.map(trainer, TrainerDto.class)).toList();
  }

  private void ensureUserHasNoTraineeProfile(Long userId) {
    if (traineeRepo.userHasTraineeProfile(userId)) {
      log.warn("User already has a trainee profile: {}", userId);
      throw new IllegalArgumentException("User already has a trainee profile: " + userId);
    }
  }
}
