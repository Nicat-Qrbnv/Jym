package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TrainerMapper {
  private final UserMapper userMapper;
  @Setter(onMethod_ = {@Autowired})
  private TraineeMapper traineeMapper;

  public TrainerProfileDto toTrainerProfileDto(Trainer source) {
    if (source == null) {
      return null;
    }

    User user = source.getUser();
    TrainingType specialization = source.getSpecialization();
    return new TrainerProfileDto(
        userMapper.toUserDto(user),
        specialization == null ? null : specialization.getName(),
        toTraineeDtos(source.getTrainees()));
  }

  private List<TraineeDto> toTraineeDtos(List<Trainee> trainees) {
    if (trainees == null) {
      return List.of();
    }
    return trainees.stream().map(traineeMapper::toTraineeDto).toList();
  }

  public TrainerDto toTrainerDto(Trainer trainer) {
    User user = trainer.getUser();
    TrainingType specialization = trainer.getSpecialization();
    return new TrainerDto(
        user.getUsername(),
        user.getFirstName(),
        user.getLastName(),
        specialization == null ? null : specialization.getName());
  }
}
