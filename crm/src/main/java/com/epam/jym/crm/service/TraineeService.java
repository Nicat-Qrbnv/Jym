package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.TraineeDto;
import java.util.List;
import java.util.Optional;

public interface TraineeService {

  TraineeDto createTrainee(TraineeDto traineeDto);

  TraineeDto updateTrainee(Long traineeId, TraineeDto traineeDto);

  void deleteTrainee(Long traineeId);

  Optional<TraineeDto> selectTrainee(Long traineeId);

  Optional<TraineeDto> selectTraineeByUsername(String username);

  List<TraineeDto> selectAllTrainees();
}
