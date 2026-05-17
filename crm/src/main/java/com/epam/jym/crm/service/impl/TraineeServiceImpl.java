package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraineeServiceImpl implements TraineeService {

  private final TraineeRepository traineeRepo;
  private final UserService userService;

  @Setter(onMethod_ = @Autowired)
  private ModelMapper mapper;

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

  @Override
  public TraineeDto updateTrainee(Long traineeId, TraineeUpdateDto traineeDto) {
    if (traineeDto == null) {
      throw new IllegalArgumentException("traineeDto must not be null");
    }

    Trainee trainee = getTraineeById(traineeId);
    trainee.setDateOfBirth(traineeDto.dateOfBirth());
    trainee.setAddress(traineeDto.address());
    trainee = traineeRepo.save(trainee);
    log.info("Updated trainee with id={} username={}", trainee.getId(), trainee.getUsername());

    return mapper.map(trainee, TraineeDto.class);
  }

  @Override
  public void deleteTrainee(Long traineeId) {
    log.debug("Deleting trainee with id={}", traineeId);
    traineeRepo.deleteById(traineeId);
    log.info("Deleted trainee with id={}", traineeId);
  }

  @Override
  public void deleteTrainee(String username) {

  }

  @Override
  public TraineeDto selectTrainee(Long traineeId) {
    log.debug("Selecting trainee by id={}", traineeId);
    return mapper.map(getTraineeById(traineeId), TraineeDto.class);
  }

  @Override
  public @NonNull Trainee getTraineeById(Long traineeId) {
    return traineeRepo
        .findById(traineeId)
        .orElseThrow(
            () -> {
              log.warn("Trainee not found by id: {}", traineeId);
              return new IllegalArgumentException("Trainee not found: " + traineeId);
            });
  }

  @Override
  public TraineeDto selectTraineeByUsername(String username) {
    log.debug("Selecting trainee by username={}", username);
    Trainee trainee =
        traineeRepo
            .findByUserUsername(username)
            .orElseThrow(
                () -> {
                  log.warn("Trainee not found by username: {}", username);
                  return new IllegalArgumentException("Trainee not found: " + username);
                });
    return mapper.map(trainee, TraineeDto.class);
  }

  @Override
  public List<TraineeDto> selectAllTrainees() {
    List<Trainee> trainees = traineeRepo.findAll();
    log.debug("Selected {} trainees", trainees.size());

    return trainees.stream().map(t -> mapper.map(t, TraineeDto.class)).toList();
  }

  private void ensureUserHasNoTraineeProfile(Long userId) {
    if (traineeRepo.userHasTraineeProfile(userId)) {
      log.warn("User already has a trainee profile: {}", userId);
      throw new IllegalArgumentException("User already has a trainee profile: " + userId);
    }
  }
}
