package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.NonNull;

public interface TraineeService {

  Trainee createTrainee(TraineeCreateDto traineeDto);

  Trainee updateTraineeProfile(String username, TraineeUpdateDto traineeDto);

  List<Training> deleteTrainee(String username);

  @NonNull Trainee getTrainee(Long traineeId);

  @NonNull Trainee getTraineeByUsername(String username);

  Set<Trainer> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames);
}
