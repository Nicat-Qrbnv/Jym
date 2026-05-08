package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.TrainingDto;
import java.util.List;
import java.util.Optional;

public interface TrainingService {

  TrainingDto createTraining(TrainingDto trainingDto);

  Optional<TrainingDto> selectTraining(Long trainingId);

  List<TrainingDto> selectAllTrainings();
}
