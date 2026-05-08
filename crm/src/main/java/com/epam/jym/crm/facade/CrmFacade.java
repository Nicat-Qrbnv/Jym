package com.epam.jym.crm.facade;

import com.epam.jym.crm.dto.TraineeDto;
import com.epam.jym.crm.dto.TraineeUpdateDto;
import com.epam.jym.crm.dto.TrainerDto;
import com.epam.jym.crm.dto.TrainerUpdateDto;
import com.epam.jym.crm.dto.TrainingDto;
import java.util.List;
import java.util.Optional;

public interface CrmFacade {

  TraineeDto createTrainee(TraineeDto traineeDto);

  TraineeDto updateTrainee(Long traineeId, TraineeUpdateDto traineeDto);

  void deleteTrainee(Long traineeId);

  Optional<TraineeDto> selectTrainee(Long traineeId);

  Optional<TraineeDto> selectTraineeByUsername(String username);

  List<TraineeDto> selectAllTrainees();

  TrainerDto createTrainer(TrainerDto trainerDto);

  TrainerDto updateTrainer(Long trainerId, TrainerUpdateDto trainerDto);

  Optional<TrainerDto> selectTrainer(Long trainerId);

  Optional<TrainerDto> selectTrainerByUsername(String username);

  List<TrainerDto> selectAllTrainers();

  TrainingDto createTraining(TrainingDto trainingDto);

  Optional<TrainingDto> selectTraining(Long trainingId);

  List<TrainingDto> selectAllTrainings();
}

