package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.entity.Trainee;
import java.util.List;
import org.jspecify.annotations.NonNull;

public interface TraineeService {

  TraineeDto createTrainee(TraineeCreateDto traineeDto);

  TraineeDto updateTrainee(Long traineeId, TraineeUpdateDto traineeDto);

  void deleteTrainee(Long traineeId);

  void deleteTrainee(String username);

  TraineeDto selectTrainee(Long traineeId);

  @NonNull Trainee getTraineeById(Long traineeId);

  TraineeDto selectTraineeByUsername(String username);

  List<TraineeDto> selectAllTrainees();
}
