package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import java.util.List;

public interface TrainingService {

  TrainingDto createTraining(TrainingCreateDto trainingDto);

  TrainingDto selectTraining(Long trainingId);

  List<TrainingDto> selectAllTrainings();
}
