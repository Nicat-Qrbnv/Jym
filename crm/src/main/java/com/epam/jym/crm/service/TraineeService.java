package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import java.util.List;
import org.jspecify.annotations.NonNull;

public interface TraineeService {

  Trainee createTrainee(TraineeCreateDto traineeDto);

  Trainee updateTrainee(Long traineeId, TraineeUpdateDto traineeDto);

  void deleteTrainee(Long traineeId);

  void deleteTrainee(String username);

  @NonNull Trainee getTrainee(Long traineeId);

  @NonNull Trainee getTraineeByUsername(String username);

  List<Trainee> getAllTrainees();

  List<Trainer> updateTraineeTrainers(Long traineeId, List<Long> trainerIds);
}
