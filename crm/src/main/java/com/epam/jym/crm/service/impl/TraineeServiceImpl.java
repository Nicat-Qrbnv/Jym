package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.TraineeDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.service.AuthenticationService;
import com.epam.jym.crm.service.TraineeService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraineeServiceImpl implements TraineeService {

  private final TraineeRepository traineeRepository;
  private final AuthenticationService authenticationService;
  private final ModelMapper modelMapper;

  @Override
  public TraineeDto createTrainee(TraineeDto traineeDto) {
    log.debug("Creating trainee");
    Trainee trainee = modelMapper.map(traineeDto, Trainee.class);
    trainee = authenticationService.register(trainee);
    trainee = traineeRepository.save(trainee);
    log.info(
        "Created trainee with id={} username={}",
        trainee.getId(),
        trainee.getUsername());

    return modelMapper.map(trainee, TraineeDto.class);
  }

  @Override
  public TraineeDto updateTrainee(Long traineeId, TraineeDto traineeDto) {
    traineeRepository
        .findById(traineeId)
        .orElseThrow(
            () -> {
              log.warn("Failed to update trainee: traineeId={} not found", traineeId);
              return new IllegalArgumentException("Trainee not found: " + traineeId);
            });
    Trainee trainee = modelMapper.map(traineeDto, Trainee.class);
    trainee.setId(traineeId);
    trainee = traineeRepository.save(trainee);
    log.info(
        "Updated trainee with id={} username={}",
        trainee.getId(),
        trainee.getUsername());

    return modelMapper.map(trainee, TraineeDto.class);
  }

  @Override
  public void deleteTrainee(Long traineeId) {
    log.debug("Deleting trainee with id={}", traineeId);
    traineeRepository.delete(traineeId);
    log.info("Deleted trainee with id={}", traineeId);
  }

  @Override
  public Optional<TraineeDto> selectTrainee(Long traineeId) {
    log.debug("Selecting trainee by id={}", traineeId);
    return traineeRepository
        .findById(traineeId)
        .map(trainee -> modelMapper.map(trainee, TraineeDto.class));
  }

  @Override
  public Optional<TraineeDto> selectTraineeByUsername(String username) {
    log.debug("Selecting trainee by username={}", username);
    return traineeRepository
        .findByUsername(username)
        .map(trainee -> modelMapper.map(trainee, TraineeDto.class));
  }

  @Override
  public List<TraineeDto> selectAllTrainees() {
    List<TraineeDto> trainees = traineeRepository.findAll().stream()
        .map(trainee -> modelMapper.map(trainee, TraineeDto.class))
        .toList();
    log.debug("Selected {} trainees", trainees.size());

    return trainees;
  }
}
