package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TrainingMapper {

  public TrainingDto toTrainingDto(Training source, User user) {
    return new TrainingDto(
        source.getName(),
        source.getScheduledDate(),
        source.getType().getName(),
        source.getDurationInMinutes(),
        user.getFullName());
  }
}
