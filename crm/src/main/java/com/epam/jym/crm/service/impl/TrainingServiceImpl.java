package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.TrainingDto;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.repository.TrainingRepository;
import com.epam.jym.crm.service.TrainingService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingService {

  private final TrainingRepository trainingRepository;
  private ModelMapper modelMapper;

  @Autowired
  public void setModelMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public TrainingDto createTraining(TrainingDto trainingDto) {
    log.debug("Creating training");
    Training training = modelMapper.map(trainingDto, Training.class);
    training = trainingRepository.save(training);
    log.info("Created training with id={} name={}", training.getId(), training.getName());

    return modelMapper.map(training, TrainingDto.class);
  }

  @Override
  public Optional<TrainingDto> selectTraining(Long trainingId) {
    log.debug("Selecting training by id={}", trainingId);
    return trainingRepository
        .findById(trainingId)
        .map(training -> modelMapper.map(training, TrainingDto.class));
  }

  @Override
  public List<TrainingDto> selectAllTrainings() {
    List<TrainingDto> trainings = trainingRepository.findAll().stream()
        .map(training -> modelMapper.map(training, TrainingDto.class))
        .toList();
    log.debug("Selected {} trainings", trainings.size());

    return trainings;
  }
}
