package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.TrainerDto;
import com.epam.jym.crm.dto.TrainerUpdateDto;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.service.AuthenticationService;
import com.epam.jym.crm.service.TrainerService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerServiceImpl implements TrainerService {

  private final TrainerRepository trainerRepository;
  private final AuthenticationService authenticationService;
  private final ModelMapper modelMapper;

  @Override
  public TrainerDto createTrainer(TrainerDto trainerDto) {
    log.debug("Creating trainer");
    Trainer trainer = modelMapper.map(trainerDto, Trainer.class);
    trainer = authenticationService.register(trainer);
    trainer = trainerRepository.save(trainer);
    log.info(
        "Created trainer with id={} username={}",
        trainer.getId(),
        trainer.getUsername());

    return modelMapper.map(trainer, TrainerDto.class);
  }

  @Override
  public TrainerDto updateTrainer(Long trainerId, TrainerUpdateDto trainerDto) {
    if (trainerDto == null) {
      throw new IllegalArgumentException("trainerDto must not be null");
    }

    Trainer trainer = trainerRepository
        .findById(trainerId)
        .orElseThrow(
            () -> {
              log.warn("Failed to update trainer: trainerId={} not found", trainerId);
              return new IllegalArgumentException("Trainer not found: " + trainerId);
            });

    if (isNameChanged(trainer, trainerDto.firstName(), trainerDto.lastName())) {
      trainer.setUsername(authenticationService.generateUsername(
          trainerDto.firstName(), trainerDto.lastName()));
    }
    trainer.setFirstName(trainerDto.firstName());
    trainer.setLastName(trainerDto.lastName());
    trainer.setPassword(trainerDto.password());
    trainer.setActive(trainerDto.active());
    trainer.setTraining(trainerDto.training() == null
        ? null
        : modelMapper.map(trainerDto.training(), Training.class));
    trainer.setSpecialization(trainerDto.specialization() == null
        ? null
        : modelMapper.map(trainerDto.specialization(), TrainingType.class));

    trainer = trainerRepository.save(trainer);
    log.info(
        "Updated trainer with id={} username={}",
        trainer.getId(),
        trainer.getUsername());

    return modelMapper.map(trainer, TrainerDto.class);
  }

  private boolean isNameChanged(Trainer trainer, String firstName, String lastName) {
    return !Objects.equals(trainer.getFirstName(), firstName)
        || !Objects.equals(trainer.getLastName(), lastName);
  }

  @Override
  public Optional<TrainerDto> selectTrainer(Long trainerId) {
    log.debug("Selecting trainer by id={}", trainerId);
    return trainerRepository
        .findById(trainerId)
        .map(trainer -> modelMapper.map(trainer, TrainerDto.class));
  }

  @Override
  public Optional<TrainerDto> selectTrainerByUsername(String username) {
    log.debug("Selecting trainer by username={}", username);
    return trainerRepository
        .findByUsername(username)
        .map(trainer -> modelMapper.map(trainer, TrainerDto.class));
  }

  @Override
  public List<TrainerDto> selectAllTrainers() {
    List<TrainerDto> trainers = trainerRepository.findAll().stream()
        .map(trainer -> modelMapper.map(trainer, TrainerDto.class))
        .toList();
    log.debug("Selected {} trainers", trainers.size());

    return trainers;
  }
}
