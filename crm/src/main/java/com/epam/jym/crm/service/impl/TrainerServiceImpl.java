package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingTypeService;
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
public class TrainerServiceImpl implements TrainerService {

  private final TrainerRepository trainerRepo;
  private final UserService userService;
  private final TrainingTypeService trainingTypeService;

  @Setter(onMethod_ = @Autowired)
  private ModelMapper mapper;

  @Override
  public TrainerDto createTrainer(TrainerCreateDto trainerDto) {
    if (trainerDto == null) {
      throw new IllegalArgumentException("trainerDto must not be null");
    }

    log.debug("Creating trainer");
    ensureUserHasNoTrainerProfile(trainerDto.userId());
    User user = userService.getUser(trainerDto.userId());

    Trainer trainer = new Trainer();
    trainer.setUser(user);
    trainer.setSpecialization(trainingTypeService.getTypeById(trainerDto.specializationId()));

    trainer = trainerRepo.save(trainer);
    log.info("Created trainer profile: {}", trainer);

    return mapper.map(trainer, TrainerDto.class);
  }

  @Override
  public TrainerDto updateTrainer(Long trainerId, TrainerUpdateDto trainerDto) {
    if (trainerDto == null) {
      throw new IllegalArgumentException("trainerDto must not be null");
    }

    Trainer trainer = getTrainerById(trainerId);
    trainer.setSpecialization(trainingTypeService.getTypeById(trainerDto.specializationId()));
    trainer = trainerRepo.save(trainer);
    log.info("Updated trainer with id={} username={}", trainer.getId(), trainer.getUsername());

    return mapper.map(trainer, TrainerDto.class);
  }

  @Override
  public TrainerDto selectTrainer(Long trainerId) {
    log.debug("Selecting trainer by id={}", trainerId);
    return mapper.map(getTrainerById(trainerId), TrainerDto.class);
  }

  @Override
  public TrainerDto selectTrainerByUsername(String username) {
    log.debug("Selecting trainer by username={}", username);
    Trainer trainer =
        trainerRepo
            .findByUserUsername(username)
            .orElseThrow(
                () -> {
                  log.warn("Trainer not found by username: {}", username);
                  return new IllegalArgumentException("Trainer not found: " + username);
                });
    return mapper.map(trainer, TrainerDto.class);
  }

  @Override
  public List<TrainerDto> selectAllTrainers() {
    List<TrainerDto> trainers =
        trainerRepo.findAll().stream().map(t -> mapper.map(t, TrainerDto.class)).toList();
    log.debug("Selected {} trainers", trainers.size());

    return trainers;
  }

  @Override
  public @NonNull Trainer getTrainerById(Long trainerId) {
    return trainerRepo
        .findById(trainerId)
        .orElseThrow(
            () -> {
              log.warn("Trainer not found by id: {}", trainerId);
              return new IllegalArgumentException("Trainer not found by id: " + trainerId);
            });
  }

  private void ensureUserHasNoTrainerProfile(Long userId) {
    if (trainerRepo.userHasTrainerProfile(userId)) {
      log.warn("User already has a trainer profile: {}", userId);
      throw new IllegalArgumentException("User already has a trainer profile: " + userId);
    }
  }
}
