package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.entity.Trainee;
import java.util.List;
import org.jspecify.annotations.NonNull;

public interface TraineeService {

  TraineeDto createTrainee(TraineeCreateDto traineeDto);

  TraineeDto updateTrainee(Long traineeId, TraineeUpdateDto traineeDto);

  void deleteTrainee(Long traineeId);

  void deleteTrainee(String username);

  TraineeDto getTraineeById(Long traineeId);

  @NonNull Trainee getTrainee(Long traineeId);

  TraineeDto getTraineeByUsername(String username);

  List<TraineeDto> getAllTrainees();

  List<TrainerDto> updateTraineeTrainers(Long traineeId, List<Long> trainerIds);
}
