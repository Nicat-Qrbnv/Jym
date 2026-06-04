package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.entity.Training;
import java.util.List;

public interface TrainingService {

  Training createTraining(TrainingCreateDto trainingDto);

  List<Training> getTraineeTrainings(String traineeUsername, TraineeTrainingsCriteriaDto criteria);

  List<Training> getTrainerTrainings(String trainerUsername, TrainerTrainingsCriteriaDto criteria);
}
